package com.sparkingpathshala.app

import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import com.sparkingpathshala.app.data.remote.MockInterceptor

class MockInterceptorTest {

    @Test
    fun testPendingStudentsEndpoint() {
        val client = OkHttpClient.Builder()
            .addInterceptor(MockInterceptor(useMock = true))
            .build()

        val request = Request.Builder()
            .url("https://api.sparkingpathshala.in/api/admin/pending-students")
            .build()

        val response = try {
            client.newCall(request).execute()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
        val bodyString = response.body?.string() ?: ""
        println("Response Body: $bodyString")
        
        assertTrue(response.isSuccessful)
        assertTrue(bodyString.contains("Harish Chander"))
        assertTrue(bodyString.contains("Kavita Roy"))
    }
}
