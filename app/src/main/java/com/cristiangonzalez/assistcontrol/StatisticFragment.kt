package com.cristiangonzalez.assistcontrol

import android.database.sqlite.SQLiteException
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.cristiangonzalez.assistcontrol.adapters.StatisticAdapter
import com.cristiangonzalez.assistcontrol.databinding.FragmentStatisticBinding
import com.cristiangonzalez.assistcontrol.interfaces.CourseService
import com.cristiangonzalez.assistcontrol.models.Statistic
import com.cristiangonzalez.assistcontrol.models.StatisticResponse
import com.cristiangonzalez.assistcontrol.ui.UserViewModel
import com.cristiangonzalez.assistcontrol.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

@AndroidEntryPoint
class StatisticFragment : Fragment(R.layout.fragment_statistic) {

    private var _binding: FragmentStatisticBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by viewModels()

    private lateinit var retrofitInstance: CourseService
    private lateinit var adapter: StatisticAdapter
    private val statistics = ArrayList<Statistic>()
    private val statisticsFilter = ArrayList<Statistic>()
    private var currentUser: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticBinding.inflate(inflater, container, false)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getUser() {
        showProgressBar()
        //Acceso a db asincrona
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val user = userViewModel.getUser()
                if (user != null) {
                    currentUser = user.user_id
                    getStatistics(currentUser!!)
                }
            } catch (e: SQLiteException) {
                requireActivity().toast(R.string.error_unexpected)
                hideProgressBar()
            }
        }
    }

    private fun getStatistics(userID: String){
        val responseLiveData: LiveData<Response<StatisticResponse>> = liveData {
            val response = userID.let { retrofitInstance.getStatistics(it) }
            emit(response)
        }

        responseLiveData.observe(viewLifecycleOwner) {
            if (it.body() != null) {
                setStatistics(it.body()!!.records)
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

    private fun setStatistics(statisticsList: ArrayList<Statistic>) {
        //Limpiar lista
        statistics.clear()
        //Agregar elementos de response a lista
        statistics.addAll(statisticsList)
        statisticsFilter.addAll(statistics)
        adapter.notifyItemInserted(statisticsList.size - 1)

        binding.TextViewEmptyStatistics.visibility = if(statisticsFilter.size == 0)  View.VISIBLE else View.GONE

        hideProgressBar()
    }

    private fun initRecycleView() {
        adapter = StatisticAdapter(statisticsFilter) //Enviar lista filtrada
        binding.StatisticsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.StatisticsRecyclerView.adapter = adapter
    }

    private fun showProgressBar() {
        binding.loginProgressBar.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.loginProgressBar.progressBar.visibility = View.GONE
    }


}