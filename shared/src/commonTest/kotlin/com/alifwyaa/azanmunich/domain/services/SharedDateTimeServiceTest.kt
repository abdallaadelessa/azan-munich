package com.alifwyaa.azanmunich.domain.services

import com.alifwyaa.azanmunich.domain.model.SharedDateModel
import com.alifwyaa.azanmunich.domain.model.SharedTimeModel
import com.alifwyaa.azanmunich.domain.model.SharedTimeUntil
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.spyk
import kotlinx.datetime.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @author Created by Abdullah Essa on 15.06.21.
 */
class SharedDateTimeServiceTest {

    //region Fields

    @MockK
    lateinit var localizationService: SharedLocalizationService

    @MockK
    lateinit var settingsService: SharedSettingsService

    private lateinit var serviceUnderTest: SharedDateTimeService

    //endregion

    //region Setup

    @BeforeTest
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        serviceUnderTest = spyk(SharedDateTimeService(settingsService, localizationService))
    }

    //endregion

    //region Test getTimeUntil

    @Test
    fun `test getTimeUntil with 1h 17m 30s difference`() {
        val nowDate = "2021-01-01"
        val nowTime = "21:33:30"
        val dateModel = SharedDateModel(1, 1, 2021)
        val timeModel = SharedTimeModel(22, 51)

        every { serviceUnderTest.todayInstant } returns mockInstant(nowDate, nowTime)

        val expected = SharedTimeUntil(
            totalNumberOfSeconds = 4650, hours = 1, minutes = 17, seconds = 30
        )

        val actual = serviceUnderTest.getTimeUntil(
            dateModel = dateModel,
            timeModel = timeModel,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `test getTimeUntil with 29m 30s difference`() {
        val nowDate = "2021-01-01"
        val nowTime = "21:33:30"
        val dateModel = SharedDateModel(1, 1, 2021)
        val timeModel = SharedTimeModel(22, 3)

        every { serviceUnderTest.todayInstant } returns mockInstant(nowDate, nowTime)

        val expected = SharedTimeUntil(
            totalNumberOfSeconds = 1770, hours = 0, minutes = 29, seconds = 30
        )

        val actual = serviceUnderTest.getTimeUntil(
            dateModel = dateModel,
            timeModel = timeModel,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `test getTimeUntil with 30s difference`() {
        val nowDate = "2021-01-01"
        val nowTime = "21:32:30"
        val dateModel = SharedDateModel(1, 1, 2021)
        val timeModel = SharedTimeModel(21, 33)

        every { serviceUnderTest.todayInstant } returns mockInstant(nowDate, nowTime)

        val expected = SharedTimeUntil(
            totalNumberOfSeconds = 30, hours = 0, minutes = 0, seconds = 30
        )

        val actual = serviceUnderTest.getTimeUntil(
            dateModel = dateModel,
            timeModel = timeModel,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `test getTimeUntil with 0 difference`() {
        val nowDate = "2021-01-01"
        val nowTime = "21:32:00"
        val dateModel = SharedDateModel(1, 1, 2021)
        val timeModel = SharedTimeModel(21, 32)

        every { serviceUnderTest.todayInstant } returns mockInstant(nowDate, nowTime)

        val expected = SharedTimeUntil(
            totalNumberOfSeconds = 0, hours = 0, minutes = 0, seconds = 0
        )

        val actual = serviceUnderTest.getTimeUntil(
            dateModel = dateModel,
            timeModel = timeModel,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `test getTimeUntil with 1h difference`() {
        val nowTime = "21:00:00"
        val nowDate = "2021-01-01"
        val nextTime = SharedTimeModel(22, 0)
        val nextDate = SharedDateModel(1, 1, 2021)

        every { serviceUnderTest.todayInstant } returns mockInstant(nowDate, nowTime)

        val expected = SharedTimeUntil(
            totalNumberOfSeconds = 3600, hours = 1, minutes = 0, seconds = 0
        )

        val actual = serviceUnderTest.getTimeUntil(
            dateModel = nextDate,
            timeModel = nextTime,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `test getTimeUntil with negative duration`() {
        val nowDate = "2021-01-01"
        val nowTime = "21:33:30"
        val dateModel = SharedDateModel(1, 1, 2021)
        val timeModel = SharedTimeModel(20, 51)

        every { serviceUnderTest.todayInstant } returns mockInstant(nowDate, nowTime)

        val expected = SharedTimeUntil(
            totalNumberOfSeconds = -2550, hours = 0, minutes = -42, seconds = -30
        )

        val actual = serviceUnderTest.getTimeUntil(
            dateModel = dateModel,
            timeModel = timeModel,
        )

        assertEquals(expected, actual)
    }

    //endregion

    //region Helpers

    private fun mockInstant(nowDate: String, nowTime: String) =
        Instant.parse("${nowDate}T$nowTime+01:00")

    //endregion
}
