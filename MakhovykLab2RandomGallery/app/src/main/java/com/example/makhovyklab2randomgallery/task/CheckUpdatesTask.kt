package com.example.makhovyklab2randomgallery.task

import ua.cn.stu.randomgallery.RandomGalleryClient
import java.util.concurrent.Callable

class CheckUpdatesTask(
    private val client: RandomGalleryClient
) : Callable<Boolean> {

    override fun call(): Boolean {
        return client.hasUpdates()
    }
}
