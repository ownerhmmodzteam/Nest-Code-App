package com.example.data.cloud

import com.example.BuildConfig
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.Tasks
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class CodeNestCloudApi {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(false)
        .build()

    private val jsonType = "application/json; charset=utf-8".toMediaType()
    private val baseUrl = BuildConfig.CODENEST_BACKEND_URL.trimEnd('/')

    private fun token(): String? {
        val user = FirebaseAuth.getInstance().currentUser ?: return null
        return try {
            Tasks.await(user.getIdToken(false))?.token
        } catch (_: Exception) {
            null
        }
    }

    private fun appCheckToken(): String {
        return Tasks.await(FirebaseAppCheck.getInstance().getAppCheckToken(false)).token
            ?: throw IllegalStateException("Verifikasi aplikasi gagal.")
    }

    fun get(path: String): JSONObject {
        return request("GET", path, JSONObject())
    }

    fun post(path: String, body: JSONObject): JSONObject {
        return request("POST", path, body)
    }

    private fun request(method: String, path: String, body: JSONObject): JSONObject {
        val authToken = token() ?: throw IllegalStateException("Akun Google belum terautentikasi.")
        val appToken = appCheckToken()
        val builder = Request.Builder()
            .url("$baseUrl/$path")
            .header("Authorization", "Bearer $authToken")
            .header("X-Firebase-AppCheck", appToken)
            .header("Accept", "application/json")
            .header("Cache-Control", "no-store")
        if (method == "POST") {
            builder.post(body.toString().toRequestBody(jsonType))
        } else {
            builder.get()
        }
        client.newCall(builder.build()).execute().use { response ->
            val raw = response.body?.string().orEmpty()
            val parsed = if (raw.isBlank()) JSONObject() else JSONObject(raw)
            if (!response.isSuccessful) {
                throw IllegalStateException(parsed.optString("error", "Cloud request gagal (${response.code})."))
            }
            return parsed
        }
    }
}
