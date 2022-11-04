package com.nulltwenty.rabobankcsvparser

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
        val service = Retrofit.Builder().baseUrl(CsvFileService.BASE_URL).client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi)).build()
            .create(CsvFileService::class.java)
        lifecycleScope.launch {
            val response: Response<ResponseBody> = service.fetchCsvFile()
            response.body().use { responseBody ->
                val inputStream: InputStream? = responseBody?.byteStream()
                val userList = inputStream?.let { readCsv(it) }
                    ?: throw NullPointerException("InputStream is null")
                userList.toString()
            }
        }
    }

    private fun readCsv(inputStream: InputStream): List<UserModel> {
        val reader = inputStream.bufferedReader()
        reader.readLine()
        val dateFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()) // 1978-01-02T00:00:00
        return reader.lineSequence().filter { it.isNotBlank() }.map {
            val (firstName, surname, issueCount, birthdate, avatarUrl) = it.split(
                ',', ignoreCase = false
            )
            UserModel(
                firstName.removeSurrounding("\""),
                surname.removeSurrounding("\""),
                issueCount.toInt(),
                dateFormat.parse(birthdate.removeSurrounding("\"")) ?: Date(),
                avatarUrl.removeSurrounding("\"")
            )
        }.toList()
    }
}