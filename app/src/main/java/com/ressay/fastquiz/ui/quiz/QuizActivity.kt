package com.ressay.fastquiz.ui.quiz

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.ressay.fastquiz.R
import com.ressay.fastquiz.databinding.ActivityQuizBinding
import com.ressay.fastquiz.data.models.Quiz
import com.ressay.fastquiz.ui.score.ScoreActivity
import com.ressay.fastquiz.utils.Constants
import com.ressay.fastquiz.utils.SegmentUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class QuizActivity : AppCompatActivity(), QuizAdapter.Presenter {

    private lateinit var binding: ActivityQuizBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var quizzesRef: CollectionReference
    private var quizzes = mutableListOf<Quiz>()
    private lateinit var mTypeQuiz: String
    private lateinit var quizAdapter: QuizAdapter
    private var mIndicator = 0
    private var mPoints = 0
    private var mIsAnswerSelected = false
    private lateinit var mSegments: SegmentUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        toggleTypeQuiz()
        setupListQuizzes()
        loadChunkQuizzes()
    }

    private fun toggleTypeQuiz() {

        mTypeQuiz = intent.getStringExtra("type").toString()
        when (mTypeQuiz) {

            Constants.KIDS -> binding.quizType.setImageResource(R.drawable.quiz_kids)
            else -> binding.quizType.setImageResource(R.drawable.quiz_culture)
        }
    }

    private fun setupListQuizzes() {

        quizAdapter = QuizAdapter(this)

        binding.options.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = quizAdapter
        }
    }

    private fun loadChunkQuizzes() = CoroutineScope(Dispatchers.IO).launch {

        quizzesRef = db.collection(mTypeQuiz)

        quizzesRef.get()
            .addOnSuccessListener {
                val countQuizzes = it.size()
                if(countQuizzes > 0) {
                    mSegments = SegmentUtil(countQuizzes, 5).apply {
                        getPairSegments()
                    }
                    mSegments.nextPair()
                    loadQuizzes()

                }else {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(applicationContext, "No available Quizzes!", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun loadQuizzes() {

        toggleLoading()

        quizzesRef.whereIn("__name__", mSegments.currentPairToRange())
            .get()
            .addOnSuccessListener {
                quizzes.addAll(
                    it.toObjects(Quiz::class.java).shuffled(Random(Random.nextInt()))
                )

                if (quizzes.size == Constants.LIMIT_QUIZZES || !mSegments.hasPairs()) {
                    toggleLoading(false)
                    nextQuiz()
                } else {
                    if (mSegments.hasPairs()) {

                        mSegments.nextPair()
                        loadQuizzes()
                    }
                }
            }
    }


    private fun nextQuiz() {

        if (mIndicator >= quizzes.size) {
            showScore()
            return
        }

        val quiz = quizzes[mIndicator]
        binding.question.text = quiz.question
        quizAdapter.submitList(quiz.options)

        mIndicator++
        binding.indicator.text = String.format("%d / %d", mIndicator, quizzes.size)
    }

    private fun checkIfAnswerCorrect(selected: Int) =
        if (quizzes[mIndicator - 1].answer == selected.plus(1)) {
            mPoints++
            Color.GREEN
        } else {
            Color.RED
        }

    private fun showScore() {

        startActivity(
            Intent(this, ScoreActivity::class.java).apply {
                putExtra("score", "$mPoints / ${quizzes.size}")
                putExtra("points", mPoints)
                putExtra("nbr_quizzes", quizzes.size)
            }
        )
        finish()
    }

    private fun toggleLoading(isLoading: Boolean = true) = CoroutineScope(Dispatchers.Main).launch {
        binding.loading.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun changeAnswersBackgroundColor(reset: Boolean = false) {

        val currentQuiz = quizzes[mIndicator - 1]
        for (i in currentQuiz.options.indices) {

            val color = if (currentQuiz.answer == i.plus(1)) Color.GREEN else Color.RED
            binding.options[i].setBackgroundColor(color)

            if (reset) {
                binding.options[i].setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    override fun onItemClick(position: Int, itemView: View) {

        if (!mIsAnswerSelected) {
            val color = checkIfAnswerCorrect(position)
            itemView.setBackgroundColor(color)
            mIsAnswerSelected = true
            changeAnswersBackgroundColor()
            Handler(Looper.getMainLooper()).postDelayed({
                changeAnswersBackgroundColor(true)
                nextQuiz()
                mIsAnswerSelected = false
            }, Constants.QUIZ_DELAY)
        }
    }
}