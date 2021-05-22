package com.ressay.fastquiz.ui.score

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.ressay.fastquiz.R
import com.ressay.fastquiz.data.models.User
import com.ressay.fastquiz.databinding.ActivityScoreBinding
import com.ressay.fastquiz.utils.Constants

class ScoreActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityScoreBinding

    private lateinit var db : FirebaseFirestore
    private lateinit var userRef : CollectionReference
    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDB()
        setScore()
        binding.home.setOnClickListener(this)
    }

    private fun initDB() {

        db = FirebaseFirestore.getInstance()
        userRef = db.collection(Constants.USERS)
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onClick(v: View) {

        when(v.id) {

            R.id.home -> home()
        }
    }

    private fun home() {
        finish()
    }

    private fun setScore() {

        val points = intent.getIntExtra("points", 0)
        val nbrQuizzes = intent.getIntExtra("nbr_quizzes", 0)
        binding.score.text = String.format("%d / %d", points, nbrQuizzes)

        updateUserScore(points)
    }

    private fun updateUserScore(points : Int) {

        val email = mAuth.currentUser?.email
        val userID = mAuth.currentUser?.uid

        if(email != null && userID != null) {

            // Getting old score
            userRef.document(userID)
                .get()
                .addOnSuccessListener {

                    val userScore = it.toObject(User::class.java)

                    userScore?.let { usr ->

                        usr.username = User.generateUsername(email)
                        usr.increaseScore(points)

                        // Updating the score
                        userRef.document(userID)
                            .set(usr)
                            .addOnSuccessListener {
                                Toast.makeText(applicationContext, getString(R.string.score_updated), Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(applicationContext, getString(R.string.score_not_updated), Toast.LENGTH_SHORT).show()
                            }
                    }
                }
        }
    }
}