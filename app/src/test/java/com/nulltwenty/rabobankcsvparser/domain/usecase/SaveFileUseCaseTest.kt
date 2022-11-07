package com.nulltwenty.rabobankcsvparser.domain.usecase

import android.content.Context
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import java.io.File

@ExperimentalCoroutinesApi
class SaveFileUseCaseTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var sut: SaveFileUseCase
    private val mockContext: Context = mock()

    @Test
    fun `given a context, when saving the file, it should save it with the given path`() = runTest {
        sut = SaveFileUseCase(mockContext, testDispatcher)
        sut.invoke("".toResponseBody(), path = "file.csv")
        assertTrue(File("file.csv").exists())
    }

    @After
    fun tearDown() {
        val file = File("file.csv")
        if (file.exists()) file.delete()
    }
}