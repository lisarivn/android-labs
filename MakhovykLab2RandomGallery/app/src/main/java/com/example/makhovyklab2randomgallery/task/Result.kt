package com.example.makhovyklab2randomgallery.task

data class Result<T>(
    val status: Status,
    val data: T? = null,
    val error: Throwable? = null
)