package com.cristiangonzalez.assistcontrol

import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.cristiangonzalez.assistcontrol.adapters.CoursesAdapter
import com.cristiangonzalez.assistcontrol.databinding.FragmentCoursesBinding
import com.cristiangonzalez.assistcontrol.interfaces.CourseService
import com.cristiangonzalez.assistcontrol.models.Course
import com.cristiangonzalez.assistcontrol.models.CourseResponse
import com.cristiangonzalez.assistcontrol.models.User
import com.cristiangonzalez.assistcontrol.ui.UserViewModel
import com.cristiangonzalez.assistcontrol.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

@AndroidEntryPoint
class CoursesFragment : Fragment(R.layout.fragment_courses) {

    private var _binding: FragmentCoursesBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by viewModels()

    private lateinit var retrofitInstance: CourseService
    private lateinit var adapter: CoursesAdapter
    private val courses = ArrayList<Course>()
    private val coursesFilter = ArrayList<Course>()
    private var currentUser: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoursesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        retrofitInstance = RetrofitInstance
            .getRetrofitInstance()
            .create(CourseService::class.java)

        getUser()
        initRecycleView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getUser() {
        //Acceso a db asincrona
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val user = userViewModel.getUser()
                if (user != null) {
                    currentUser = user.user_id
                    getCoursesAssigned(currentUser!!)
                }
//                hideProgressBar()
            } catch (e: SQLiteException) {
                requireActivity().toast(R.string.error_unexpected)
//                hideProgressBar()
            }
        }
    }

    private fun getCoursesAssigned(userID: String){
        val responseLiveData: LiveData<Response<CourseResponse>> = liveData {
            val response = userID.let { retrofitInstance.getCourses(it) }
            emit(response)
        }

        responseLiveData.observe(viewLifecycleOwner) {
            if (it.body() != null) {
                setCourses(it.body()!!.records)
            } else {
                try {
                    val responseError = it.errorBody()?.string()?.let { it1 -> JSONObject(it1) }
                    responseError?.getString("message")?.let { it1 -> requireActivity().toast(it1) }
                } catch (e: Exception) {
                    e.message?.let { it1 -> requireActivity().toast(it1) }
                }
//                hideProgressBar()
            }

        }
    }

    private fun setCourses(coursesList: ArrayList<Course>) {
        Log.i("records", coursesList.toString())
        //Limpiar lista
        courses.clear()
        //Agregar elementos de response a lista
        courses.addAll(coursesList)
        coursesFilter.addAll(courses)
        adapter.notifyItemInserted(coursesList.size - 1)
    }

    private fun initRecycleView() {
        adapter = CoursesAdapter(coursesFilter) //Enviar lista filtrada
        binding.CoursesRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.CoursesRecyclerView.adapter = adapter
    }

}