package com.ncorti.kotlin.template.app

import android.app.admin.DevicePolicyManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ncorti.kotlin.template.app.databinding.ActivityAttendanceBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AttendanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAttendanceBinding
    private lateinit var studentManager: StudentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        studentManager = StudentManager(this)

        // Block back navigation while in kiosk mode
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Intentionally blocked in kiosk mode
            }
        })

        // Enter kiosk / lock-task mode so students cannot leave the app
        enterKioskMode()

        binding.textDate.text =
            SimpleDateFormat("EEEE, MMM d yyyy", Locale.getDefault()).format(Date())

        binding.buttonDone.setOnLongClickListener {
            showExitDialog()
            true
        }

        refreshList()
    }

    private fun enterKioskMode() {
        val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        if (dpm.isLockTaskPermitted(packageName) ||
            dpm.isDeviceOwnerApp(packageName) ||
            dpm.isProfileOwnerApp(packageName)
        ) {
            startLockTask()
        } else {
            // Fallback: attempt screen pinning (requires user confirmation on non-managed devices)
            try {
                startLockTask()
            } catch (e: SecurityException) {
                Toast.makeText(this, getString(R.string.kiosk_not_available), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun exitKioskMode() {
        try {
            stopLockTask()
        } catch (e: Exception) {
            // Already exited or not in lock task
        }
        finish()
    }

    private fun showExitDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.exit_kiosk_title)
            .setMessage(R.string.exit_kiosk_message)
            .setPositiveButton(R.string.yes) { _, _ -> exitKioskMode() }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun refreshList() {
        val students = studentManager.getAllStudents()
        val presentIds = studentManager.getPresentIds()

        binding.textPresentCount.text = presentIds.size.toString()

        val container = binding.attendanceList
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
                .inflate(R.layout.item_attendance_student, container, false)
            val nameView = itemView.findViewById<TextView>(R.id.text_student_name)
            val checkView = itemView.findViewById<View>(R.id.view_check_mark)
            nameView.text = student.name

            val isPresent = presentIds.contains(student.id)
            checkView.visibility = if (isPresent) View.VISIBLE else View.GONE
            itemView.setBackgroundResource(
                if (isPresent) R.drawable.bg_student_present else R.drawable.bg_student_absent
            )

            itemView.setOnClickListener {
                if (presentIds.contains(student.id)) {
                    studentManager.markAbsent(student.id)
                } else {
                    studentManager.markPresent(student.id)
                }
                refreshList()
            }
            container.addView(itemView)
        }
    }
}

