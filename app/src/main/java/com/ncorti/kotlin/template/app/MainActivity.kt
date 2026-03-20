package com.ncorti.kotlin.template.app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ncorti.kotlin.template.app.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var studentManager: StudentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        studentManager = StudentManager(this)

        binding.buttonRegister.setOnClickListener {
            startActivity(Intent(this, RegisterStudentActivity::class.java))
        }

        binding.buttonKiosk.setOnClickListener {
            startActivity(Intent(this, AttendanceActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        refreshDashboard()
    }

    private fun refreshDashboard() {
        val students = studentManager.getAllStudents()
        val presentIds = studentManager.getPresentIds()
        val presentCount = presentIds.size
        val totalCount = students.size

        binding.textTotalCount.text = totalCount.toString()
        binding.textPresentCount.text = presentCount.toString()
        binding.textDate.text = SimpleDateFormat("EEEE, MMM d yyyy", Locale.getDefault()).format(Date())

        val container = binding.studentListContainer
        container.removeAllViews()

        if (students.isEmpty()) {
            val empty = TextView(this)
            empty.text = getString(R.string.no_students_registered)
            empty.setPadding(0, 32, 0, 0)
            container.addView(empty)
            return
        }

        students.forEach { student ->
            val itemView = LayoutInflater.from(this)
                .inflate(R.layout.item_student_row, container, false)
            val nameView = itemView.findViewById<TextView>(R.id.text_student_name)
            val statusView = itemView.findViewById<View>(R.id.view_status_dot)
            nameView.text = student.name
            statusView.setBackgroundResource(
                if (presentIds.contains(student.id)) R.drawable.bg_present_dot
                else R.drawable.bg_absent_dot
            )
            container.addView(itemView)
        }
    }
}

