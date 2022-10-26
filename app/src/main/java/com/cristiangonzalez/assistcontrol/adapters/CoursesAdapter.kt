package com.cristiangonzalez.assistcontrol.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cristiangonzalez.assistcontrol.R
import com.cristiangonzalez.assistcontrol.databinding.FragmentCoursesItemBinding
import com.cristiangonzalez.assistcontrol.models.Course

class CoursesAdapter(private val courses: List<Course>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val binding = FragmentCoursesItemBinding.bind(itemView)

        //Cargar atributos de api
        fun bind(course: Course) = with(itemView){
            binding.textViewTitle.text = course.course
            binding.textViewSchedule.text = course.schedule
            binding.textViewTeacher.text = course.teacher
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.fragment_courses_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(courses[position])
    }

    override fun getItemCount() = courses.size

}