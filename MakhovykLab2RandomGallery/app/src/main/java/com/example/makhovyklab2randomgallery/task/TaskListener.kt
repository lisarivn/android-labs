package com.example.makhovyklab2randomgallery.task

interface TaskListener<T> {

    fun onTaskResult(result: Result<T>)
}