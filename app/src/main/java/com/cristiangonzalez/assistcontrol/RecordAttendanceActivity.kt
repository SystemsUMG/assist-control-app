package com.cristiangonzalez.assistcontrol

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.cristiangonzalez.assistcontrol.databinding.ActivityRecordAttendanceBinding
import com.cristiangonzalez.assistcontrol.interfaces.CourseService
import com.cristiangonzalez.assistcontrol.models.Course
import com.cristiangonzalez.assistcontrol.models.RecordAttendance
import com.cristiangonzalez.assistcontrol.models.ResponseAttendance
import com.cristiangonzalez.assistcontrol.utils.goToActivity
import com.cristiangonzalez.assistcontrol.utils.toast
import org.json.JSONObject
import retrofit2.Response

class RecordAttendanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordAttendanceBinding
    private lateinit var retrofitInstance: CourseService
    private lateinit var course: Course

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecordAttendanceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.loginProgressBar.progressBar.bringToFront()//Mostrar progressBar al frente

        retrofitInstance = RetrofitInstance
            .getRetrofitInstance()
            .create(CourseService::class.java)

        setUpActionBar()
        setUpUI(getExtras())

        binding.recordAttendanceButton.setOnClickListener {
            val observation = binding.editTextObservation.text.toString()
            recordAttendance(observation)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.recordAttendanceToolbar)

        if (supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }
    }

    private fun getExtras(): Course {
        return intent.extras!!.getSerializable("course") as Course
    }

    private fun setUpUI(course: Course) {
        this.course = course
        binding.textViewTitle.text = course.course
        binding.textViewTeacher.text = course.teacher
        binding.textViewSchedule.text = course.schedule
    }

    private fun recordAttendance(observation: String) {
        showProgressBar()
        val recordAttendance = RecordAttendance(course.id, observation)
        val postResponse : LiveData<Response<ResponseAttendance>> = liveData {
            val response = retrofitInstance.recordAttendance(recordAttendance)
            emit(response)
        }

        postResponse.observe(this) {
            if (it.body() != null) {
                val message = it.body()!!.message
                toast(message)
                //Ir a MainActivity
                goToActivity<MainActivity> {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                hideProgressBar()
            } else {
                try {
                    val responseError = it.errorBody()?.string()?.let { it1 -> JSONObject(it1) }
                    responseError?.getString("message")?.let { it1 -> toast(it1) }
                } catch (e: Exception) {
                    e.message?.let { it1 -> toast(it1) }
                }
                hideProgressBar()
            }
        }
    }

    private fun showProgressBar() {
        binding.loginProgressBar.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.loginProgressBar.progressBar.visibility = View.GONE
    }

}