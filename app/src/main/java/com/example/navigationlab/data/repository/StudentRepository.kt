package com.example.navigationlab.data.repository

import com.example.navigationlab.data.model.Student
//import com.example.navigationlab.data.source.StudentLocalDataSource


interface StudentRepository {
    fun getStudents(callback: (Result<List<Student>>) -> Unit)
    fun getStudent(id: Int, callback: (Result<Student>) -> Unit)
}