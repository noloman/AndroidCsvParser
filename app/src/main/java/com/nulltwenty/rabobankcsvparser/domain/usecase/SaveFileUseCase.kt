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
    suspend operator fun invoke(
        body: ResponseBody, path: String? = null
    ) = withContext(defaultCoroutineDispatcher) {
        saveFile(body, path)
    }

    private fun saveFile(body: ResponseBody?, path: String?) {
        if (body == null) {
            return
        }

        var input: InputStream? = null
        try {
            input = body.byteStream()
            val fos = FileOutputStream(path ?: (context.filesDir.path + "/issues.csv"))
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