package com.ncorti.kotlin.template.app

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class StudentManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun todayKey(): String = dateFormat.format(Date())

    fun getAllStudents(): List<Student> {
        val json = prefs.getString(KEY_STUDENTS, "[]") ?: "[]"
        val arr = JSONArray(json)
        val list = mutableListOf<Student>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            list.add(Student(id = obj.getString("id"), name = obj.getString("name")))
        }
        return list
    }

    fun addStudent(name: String): Student {
        val student = Student(id = UUID.randomUUID().toString(), name = name.trim())
        val arr = JSONArray(prefs.getString(KEY_STUDENTS, "[]") ?: "[]")
        val obj = JSONObject().put("id", student.id).put("name", student.name)
        arr.put(obj)
        prefs.edit().putString(KEY_STUDENTS, arr.toString()).apply()
        return student
    }

    fun removeStudent(id: String) {
        val arr = JSONArray(prefs.getString(KEY_STUDENTS, "[]") ?: "[]")
        val newArr = JSONArray()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            if (obj.getString("id") != id) newArr.put(obj)
        }
        prefs.edit().putString(KEY_STUDENTS, newArr.toString()).apply()
        // also remove from today's attendance
        val presentIds = getPresentIds().toMutableSet()
        presentIds.remove(id)
        savePresentIds(presentIds)
    }

    fun getPresentIds(date: String = todayKey()): Set<String> {
        val key = "$KEY_ATTENDANCE_PREFIX$date"
        return prefs.getStringSet(key, emptySet()) ?: emptySet()
    }

    fun markPresent(studentId: String, date: String = todayKey()) {
        val current = getPresentIds(date).toMutableSet()
        current.add(studentId)
        savePresentIds(current, date)
    }

    fun markAbsent(studentId: String, date: String = todayKey()) {
        val current = getPresentIds(date).toMutableSet()
        current.remove(studentId)
        savePresentIds(current, date)
    }

    fun getPresentCount(date: String = todayKey()): Int = getPresentIds(date).size

    fun resetAttendance(date: String = todayKey()) {
        savePresentIds(emptySet(), date)
    }

    private fun savePresentIds(ids: Set<String>, date: String = todayKey()) {
        val key = "$KEY_ATTENDANCE_PREFIX$date"
        prefs.edit().putStringSet(key, ids).apply()
    }

    companion object {
        private const val PREFS_NAME = "attendance_prefs"
        private const val KEY_STUDENTS = "students"
        private const val KEY_ATTENDANCE_PREFIX = "attendance_"
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    }
}
