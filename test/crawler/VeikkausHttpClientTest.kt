package com.example.crawler

import com.example.store.Draw
import com.github.kittinunf.fuel.core.Client
import com.github.kittinunf.fuel.core.FuelManager
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.ByteArrayInputStream
import java.io.File
import java.time.LocalDate


@DisplayName("VeikkausHttpClient")
class VeikkausHttpClientTest {

    private val mockResponseWithOneDraw = File("test-resources/veikkaus-response-one-draw.json").readText()
    private val mockResponseWithTwoDraws = File("test-resources/veikkaus-response-two-draws.json").readText()
    private val mockResponseWithoutDraws = File("test-resources/veikkaus-response-no-draws.json").readText()
    private val mockResponseFail = File("test-resources/veikkaus-response-fail.json").readText()

    @Test
    @DisplayName("should get one draw when Veikkaus API returns one draw")
    fun shouldGetOne() {
        mockApiResponses(200, mockResponseWithOneDraw)


        val draws = VeikkausHttpClient()
            .getLottoDrawsBetweenDates(
                LocalDate.of(2018, 12, 23),
                LocalDate.of(2018, 12, 30)
            )

        assertEquals(
            listOf(
                Draw(
                    LocalDate.of(2018, 12, 29),
                    setOf(4, 6, 8, 11, 14, 25, 29),
                    setOf(21),
                    setOf(30)
                )
            ),
            draws
        )
    }

    @Test
    @DisplayName("should get multiple draws if Veikkaus API returns multiple draws")
    fun shouldGetMultiple() {
        mockApiResponses(200, mockResponseWithTwoDraws)

        val draws = VeikkausHttpClient()
            .getLottoDrawsBetweenDates(
                LocalDate.of(2018, 12, 23),
                LocalDate.of(2018, 12, 30)
            )

        assertEquals(2, draws.size)
    }

    @Test
    @DisplayName("shouldn't get any draws when Veikkaus API doesn't return any")
    fun shouldGetNothing() {
        mockApiResponses(200, mockResponseWithoutDraws)

        val draws = VeikkausHttpClient()
            .getLottoDrawsBetweenDates(
                LocalDate.of(1970, 12, 23),
                LocalDate.of(1970, 12, 30)
            )

        assertTrue(draws.isEmpty())
    }

    @Test
    @DisplayName("should throw when Veikkaus API call fails")
    fun shouldFail() {
        mockApiResponses(400, mockResponseFail)

        assertThrows<Exception> {
            VeikkausHttpClient()
                .getLottoDrawsBetweenDates(
                    LocalDate.of(2018, 12, 1),
                    LocalDate.of(2018, 12, 30)
                )
        }
    }
}

fun mockApiResponses(mockStatus: Int, mockResponse: String) {
    val client = mockk<Client>()

    every { client.executeRequest(any()).statusCode } returns mockStatus
    every { client.executeRequest(any()).responseMessage } returns if (mockStatus in 200..299) "OK" else "Fail"
    every { client.executeRequest(any()).dataStream } returns ByteArrayInputStream(mockResponse.toByteArray())
    every { client.executeRequest(any()).data } returns mockResponse.toByteArray()

    FuelManager.instance.client = client
}
