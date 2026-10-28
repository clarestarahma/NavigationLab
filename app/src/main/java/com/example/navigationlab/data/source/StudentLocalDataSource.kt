package com.example.navigationlab.data.source

import com.example.navigationlab.data.model.Student

class StudentLocalDataSource {
    private val students = listOf(
        Student(2026,  "Alya Putri", "Teknik Informatika", 2026),
        Student(2027,  "Bima Pratama", "Teknik Informatika", 2026),
        Student(2028,  "Citra Lestari", "Teknik Informatika", 2026),
        Student(2029,  "Dodi Susanto", "Teknik Informatika", 2026),
        Student(1034,  "Claresta Rahma El Riyanto", "Teknik Informatika", 2024),
    )

    fun getStudentById(id: Int): Student? =
        students.find { it.id == id }

    fun getStudents() = students
}