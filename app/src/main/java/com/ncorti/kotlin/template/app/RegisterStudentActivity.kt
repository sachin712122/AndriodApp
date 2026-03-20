package com.ncorti.kotlin.template.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ncorti.kotlin.template.app.databinding.ActivityRegisterStudentBinding

class RegisterStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterStudentBinding
    private lateinit var studentManager: StudentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        studentManager = StudentManager(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.register_students)

        binding.buttonAdd.setOnClickListener {
            val name = binding.editTextName.text.toString().trim()
            if (name.isEmpty()) {
                binding.editTextName.error = getString(R.string.name_required)
                return@setOnClickListener
            }
            studentManager.addStudent(name)
            binding.editTextName.setText("")
            Toast.makeText(this, getString(R.string.student_added, name), Toast.LENGTH_SHORT).show()
            refreshList()
        }

        refreshList()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun refreshList() {
        val students = studentManager.getAllStudents()
        val container = binding.registeredStudentsList
        container.removeAllViews()

        if (students.isEmpty()) {
            val empty = TextView(this)
            empty.text = getString(R.string.no_students_registered)
            empty.setPadding(0, 32, 0, 32)
            container.addView(empty)
            return
        }

        students.forEach { student ->
            val itemView = LayoutInflater.from(this)
                .inflate(R.layout.item_register_student, container, false)
            itemView.findViewById<TextView>(R.id.text_student_name).text = student.name
            itemView.findViewById<ImageButton>(R.id.button_delete).setOnClickListener {
                studentManager.removeStudent(student.id)
                Toast.makeText(
                    this,
                    getString(R.string.student_removed, student.name),
                    Toast.LENGTH_SHORT
                ).show()
                refreshList()
            }
            container.addView(itemView)
        }
    }
}
