package com.nulltwenty.rabobankcsvparser.domain.usecase

import android.content.Context
import android.util.Log
import com.nulltwenty.rabobankcsvparser.data.di.DefaultCoroutineDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

class SaveFileUseCase @Inject constructor(
    private val context: Context,
    @DefaultCoroutineDispatcher private val defaultCoroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        body: ResponseBody, path: String? = null
    ): InputStream? = withContext(defaultCoroutineDispatcher) {
        return@withContext saveFile(body, path)
    }

    private fun saveFile(body: ResponseBody?, path: String?): InputStream? {
        if (body == null) {
            return null
        }

        var input: InputStream? = null
        try {
            input = body.byteStream()
            val byteOutputStream = ByteArrayOutputStream()
            val fos = FileOutputStream(path ?: (context.filesDir.path + "/issues.csv"))
            fos.use { output ->
                val inputStream = BufferedInputStream(input)
                inputStream.use { bufferedInputStream ->
                    byteOutputStream.use { output ->
                        bufferedInputStream.copyTo(output)
                    }
                }
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
            return ByteArrayInputStream(byteOutputStream.toByteArray())
        } catch (e: Exception) {
            Log.e("Error saving file", e.toString())
        } finally {
            input?.close()
        }
        return null
    }

    private fun createByteArrayInputStream(stream: InputStream): ByteArrayInputStream {
        val inputStream = BufferedInputStream(stream)
        val byteOutputStream = ByteArrayOutputStream()
        inputStream.use { input ->
            byteOutputStream.use { output ->
                input.copyTo(output)
            }
        }
        return ByteArrayInputStream(byteOutputStream.toByteArray())
    }
}