package com.motologr

import com.motologr.ui.data.objects.insurance.Insurance
import org.junit.Test

import org.junit.Assert.*
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class InsuranceBillUnitTests {
    @Test
    fun generateInsuranceBills_Fortnightly_StartOfMonth() {
        val localDate = LocalDate.of(2023, 1, 1)
        val startDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

        val insurance = Insurance("AA", startDate, 0, 0, 15.30.toBigDecimal(), startDate, 0)
        insurance.generateInsuranceBills()

        assertEquals(26, insurance.insuranceBillLog.returnInsuranceBillLog().count())
    }

    @Test
    fun generateInsuranceBills_Monthly_StartOfMonth() {
        val localDate = LocalDate.of(2023, 1, 1)
        val startDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

        val insurance = Insurance("AA", startDate, 0, 1, 15.30.toBigDecimal(), startDate, 0)
        insurance.generateInsuranceBills()

        assertEquals(12, insurance.insuranceBillLog.returnInsuranceBillLog().count())
    }

    @Test
    fun generateInsuranceBills_Annually_StartOfMonth() {
        val localDate = LocalDate.of(2023, 1, 1)
        val startDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

        val insurance = Insurance("AA", startDate, 0, 2, 15.30.toBigDecimal(), startDate, 0)
        insurance.generateInsuranceBills()

        assertEquals(1, insurance.insuranceBillLog.returnInsuranceBillLog().count())
    }

    @Test
    fun generateInsuranceBills_Fortnightly_EndOfMonth() {
        val localDate = LocalDate.of(2023, 1, 31)
        val startDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

        val insurance = Insurance("AA", startDate, 0, 0, 15.30.toBigDecimal(), startDate, 0)
        insurance.generateInsuranceBills()

        assertEquals(26, insurance.insuranceBillLog.returnInsuranceBillLog().count())
    }

    @Test
    fun generateInsuranceBills_Monthly_EndOfMonth() {
        val localDate = LocalDate.of(2023, 1, 31)
        val startDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

        val insurance = Insurance("AA", startDate, 0, 1, 15.30.toBigDecimal(), startDate, 0)
        insurance.generateInsuranceBills()

        assertEquals(12, insurance.insuranceBillLog.returnInsuranceBillLog().count())
    }

    @Test
    fun generateInsuranceBills_Annually_EndOfMonth() {
        val localDate = LocalDate.of(2023, 1, 31)
        val startDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

        val insurance = Insurance("AA", startDate, 0, 2, 15.30.toBigDecimal(), startDate, 0)
        insurance.generateInsuranceBills()

        assertEquals(1, insurance.insuranceBillLog.returnInsuranceBillLog().count())
    }

    @Test
    fun generateInsuranceBills_Fortnightly_LeapYear() {
        val localDate = LocalDate.of(2024, 1, 1)
        val startDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

        val insurance = Insurance("AA", startDate, 0, 0, 15.30.toBigDecimal(), startDate, 0)
        insurance.generateInsuranceBills()

        assertEquals(26, insurance.insuranceBillLog.returnInsuranceBillLog().count())
    }
}