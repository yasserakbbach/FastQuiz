package com.ressay.fastquiz.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ressay.fastquiz.R
import com.ressay.fastquiz.data.models.User
import com.ressay.fastquiz.databinding.ActivityMainBinding
import com.ressay.fastquiz.databinding.LeaderboardBinding
import com.ressay.fastquiz.ui.login.LoginActivity
import com.ressay.fastquiz.ui.quiz.QuizActivity
import com.ressay.fastquiz.utils.Constants
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mAuth : FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userRef : CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        animateSplash()
        binding.apply {
            kids.setOnClickListener(this@MainActivity)
            culture.setOnClickListener(this@MainActivity)
            logout.setOnClickListener(this@MainActivity)
            leaderboard.setOnClickListener(this@MainActivity)
        }
    }

    private fun animateSplash() {

        Timer().schedule(Constants.SPLASH_DELAY) {
            runOnUiThread {

                val loggedUser = mAuth.currentUser

                if(loggedUser != null) {
                    binding.apply {
                        splash.visibility = View.GONE
                        categories.visibility = View.VISIBLE
                        logout.visibility = View.VISIBLE
                        leaderboard.visibility = View.VISIBLE
                    }

                }else {
                    showLogin()
                }

            }
        }
    }

    override fun onClick(v: View) {

        when(v.id) {
            R.id.kids -> showQuizzes(Constants.KIDS)
            R.id.culture -> showQuizzes(Constants.CULTURE)
            R.id.logout -> logout()
            R.id.leaderboard -> leaderBoard()
        }
    }

    private fun showQuizzes(type : String) {

        startActivity(
            Intent(this, QuizActivity::class.java).apply {
                putExtra("type", type)
            }
        )
    }

    private fun showLogin() {

        startActivity(
            Intent(applicationContext, LoginActivity::class.java)
        )
        finish()
    }

    private fun logout() {

        MaterialAlertDialogBuilder(this@MainActivity).apply {
            setMessage(getString(R.string.prompt_logout))
            setPositiveButton(R.string.yes) { _, _ ->
                mAuth.signOut()
                showLogin()
            }
            setNegativeButton(R.string.no, null)
            show()
        }
    }

    private fun initScore(getScores : (List<User>) -> Unit) {

        userRef = db.collection(Constants.USERS)

        userRef.orderBy("score", Query.Direction.DESCENDING)
            .limit(Constants.LIMIT_LEADERBOARD)
            .get()
            .addOnSuccessListener {

                val scores = it.toObjects(User::class.java)
                getScores.invoke(scores)
            }
    }

    private fun leaderBoard() {

        val lBBS = BottomSheetDialog(this@MainActivity)
        val leaderBoardBinding = LeaderboardBinding.inflate(layoutInflater, binding.root, false)

        initScore {

            val leaderBoardAdapter = LeaderBoardAdapter()
            leaderBoardAdapter.submitList(it)

            leaderBoardBinding.scores.apply {
                this.layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = leaderBoardAdapter
            }

            lBBS.setContentView(leaderBoardBinding.root)
            lBBS.show()
        }
    }
}