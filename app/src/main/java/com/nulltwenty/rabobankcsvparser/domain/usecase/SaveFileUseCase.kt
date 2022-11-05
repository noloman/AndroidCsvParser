package com.nulltwenty.rabobankcsvparser.domain.usecase

import android.content.Context
import android.util.Log
import com.nulltwenty.rabobankcsvparser.data.di.DefaultCoroutineDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

class SaveFileUseCase @Inject constructor(
    private val context: Context,
    @DefaultCoroutineDispatcher private val defaultCoroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(body: ResponseBody): Unit =
        withContext(defaultCoroutineDispatcher) {
            saveFile(body)
        }

    private fun saveFile(body: ResponseBody?) {
        if (body == null) {
            return
        }

        var input: InputStream? = null
        try {
            input = body.byteStream()
            val path = context.filesDir.path + "/issues.csv"
            val fos = FileOutputStream(path)
            fos.use { output ->
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
        } catch (e: Exception) {
            Log.e("Error saving file", e.toString())
        } finally {
            input?.close()
        }
    }
}