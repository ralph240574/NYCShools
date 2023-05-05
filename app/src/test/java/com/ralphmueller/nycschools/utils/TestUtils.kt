package com.ralphmueller.nycschools.utils

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import java.nio.charset.StandardCharsets


fun MockWebServer.enqueueResponse(fileName: String, code: Int) {


    val inputStream = javaClass.classLoader!!.getResourceAsStream("api-response/$fileName")

    val source = inputStream!!.let { inputStream.source().buffer() }
    enqueue(
        MockResponse()
            .setResponseCode(code)
            .setBody(source.readString(StandardCharsets.UTF_8))
    )
}