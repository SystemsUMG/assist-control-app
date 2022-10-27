package com.cristiangonzalez.assistcontrol

import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.cristiangonzalez.assistcontrol.adapters.CoursesAdapter
import com.cristiangonzalez.assistcontrol.databinding.FragmentCoursesBinding
import com.cristiangonzalez.assistcontrol.interfaces.CourseService
import com.cristiangonzalez.assistcontrol.interfaces.CoursesClickListener
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
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class CoursesFragment : Fragment(R.layout.fragment_courses), CoursesClickListener {

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
        setHasOptionsMenu(true)
        _binding = FragmentCoursesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        retrofitInstance = RetrofitInstance
            .getRetrofitInstance()
            .create(CourseService::class.java)

        binding.loginProgressBar.progressBar.bringToFront()//Mostrar progressBar al frente

        getUser()
        initRecycleView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_course, menu)

        val searchView = menu.findItem(R.id.menu_search).actionView as SearchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                coursesFilter.clear() //Limpiar lista
                if (newText.isNullOrEmpty()) {
                    coursesFilter.addAll(courses) //Agregar todos
                    adapter.notifyDataSetChanged()
                } else {
                    val search = newText.lowercase(Locale.ROOT)
                    courses.forEach {
                        if (it.course.lowercase(Locale.ROOT).contains(search)) coursesFilter.add(it)
                    }
                    adapter.notifyDataSetChanged()
                }
                return false
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCoursesClickListener(data: Course) {
        val intent = Intent(activity, RecordAttendanceActivity::class.java)
        intent.putExtra("course", data as Serializable)
        startActivity(intent)
    }

    private fun getUser() {
        showProgressBar()
        //Acceso a db asincrona
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val user = userViewModel.getUser()
                if (user != null) {
                    currentUser = user.user_id
                    getCoursesAssigned(currentUser!!)
                }
            } catch (e: SQLiteException) {
                requireActivity().toast(R.string.error_unexpected)
                hideProgressBar()
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
                hideProgressBar()
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
        hideProgressBar()
    }

    private fun initRecycleView() {
        adapter = CoursesAdapter(coursesFilter, this) //Enviar lista filtrada
        binding.CoursesRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.CoursesRecyclerView.adapter = adapter
    }

    private fun showProgressBar() {
        binding.loginProgressBar.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.loginProgressBar.progressBar.visibility = View.GONE
    }

}