package com.nulltwenty.rabobankcsvparser.domain

import com.nulltwenty.rabobankcsvparser.malformedDateCsvFileModelList
import com.nulltwenty.rabobankcsvparser.malformedDateString
import com.nulltwenty.rabobankcsvparser.missingOneComaInHeaderString
import com.nulltwenty.rabobankcsvparser.missingOneQuoteInHeaderString
import com.nulltwenty.rabobankcsvparser.originalCsvFileModelList
import com.nulltwenty.rabobankcsvparser.originalCsvString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

@ExperimentalCoroutinesApi
class CsvParserTest {
    private lateinit var sut: CsvParser

    @Test
    fun `given a CSV file with only a header, the list of issues should be empty`() = runTest {
        sut = CsvParser
        val fakeString = """"First name","Sur name","Issue count","Date of birth","avatar""""
        val result = sut.parse(fakeString.byteInputStream())
        val csvFileModels = result.first()
        assertTrue(csvFileModels.isEmpty())
    }

    @Test(expected = Exception::class)
    fun `given a CSV file with completely wrong data, then it should throw an exception`() =
        runTest {
            val fakeString = "fake data"
            val fakeInputStream = fakeString.toResponseBody()
            sut = CsvParser
            sut.parse(fakeInputStream.byteStream())
        }

    @Test(expected = Exception::class)
    fun `given a CSV file with missing coma in header, then it should throw an exception`() =
        runTest {
            val fakeInputStream = missingOneQuoteInHeaderString.toResponseBody()
            sut = CsvParser
            sut.parse(fakeInputStream.byteStream())
        }

    @Test(expected = Exception::class)
    fun `given a CSV file with missing comma to separate fields in header, then it should throw an exception`() =
        runTest {
            val fakeInputStream = missingOneComaInHeaderString.toResponseBody()
            sut = CsvParser
            sut.parse(fakeInputStream.byteStream())
        }

    @Test
    fun `given a CSV file with a malformed date, it should still return a list of CsvModel`() = runTest {
        val fakeInputStream = malformedDateString.toResponseBody()
        sut = CsvParser
        val result = sut.parse(fakeInputStream.byteStream())
        val csvFileModels = result.first()
        assertNotNull(csvFileModels)
        assertFalse(csvFileModels.isEmpty())
        assertEquals(csvFileModels, malformedDateCsvFileModelList)
    }

    @Test
    fun `given a CSV file with CORRECT data, then it should return a correct list`() = runTest {
        val fakeInputStream = originalCsvString.toResponseBody()
        sut = CsvParser
        val result = sut.parse(fakeInputStream.byteStream())
        val csvFileModels = result.first()
        assertNotNull(csvFileModels)
        assertFalse(csvFileModels.isEmpty())
        assertEquals(csvFileModels, originalCsvFileModelList)
    }
}