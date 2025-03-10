package com.motologr.insurance

import com.motologr.UnitTestBase
import com.motologr.data.objects.insurance.Insurance
import org.junit.Test

import org.junit.Assert.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Date

class InsuranceBillUnitTests : UnitTestBase() {
    @Test
    fun generateInsuranceBills_Fortnightly_StartOfMonth() {
        val localDate = LocalDate.of(2023, 1, 1)
        val startDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

        val insurance = Insurance(0, "AA", startDate, 0, 0, 15.30.toBigDecimal(), startDate, 0)
        insurance.generateInsuranceBills()

        val insuranceBills = insurance.insuranceBillLog.returnInsuranceBillLog()
        insuranceBills.sortByDescending { x -> x.billingDate.time }

        assertEquals(26, insuranceBills.count())
        assert(insuranceBills.last().billingDate.time >= startDate.time)
    }

    @Test
    fun generateInsuranceBills_Monthly_StartOfMonth() {
        val localDate = LocalDate.of(2023, 1, 1)
        val startDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

        val insurance = Insurance(0, "AA", startDate, 0, 1, 15.30.toBigDecimal(), startDate, 0)
        insurance.generateInsuranceBills()

        val insuranceBills = insurance.insuranceBillLog.returnInsuranceBillLog()
        insuranceBills.sortByDescending { x -> x.billingDate.time }

        assertEquals(12, insuranceBills.count())
        assert(insuranceBills.last().billingDate.time >= startDate.time)
    }

    @Test
    fun generateInsuranceBills_Annually_StartOfMonth() {
        val localDate = LocalDate.of(2023, 1, 1)
        val startDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

        val insurance = Insurance(0, "AA", startDate, 0, 2, 15.30.toBigDecimal(), startDate, 0)
        insurance.generateInsuranceBills()

        val insuranceBills = insurance.insuranceBillLog.returnInsuranceBillLog()
        insuranceBills.sortByDescending { x -> x.billingDate.time }

        assertEquals(1, insuranceBills.count())
        assert(insuranceBills.last().billingDate.time >= startDate.time)
    }

    @Test
    fun generateInsuranceBills_Fortnightly_EndOfMonth() {
        val localDate = LocalDate.of(2023, 1, 31)
        val startDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

        val insurance = Insurance(0, "AA", startDate, 0, 0, 15.30.toBigDecimal(), startDate, 0)
        insurance.generateInsuranceBills()

        val insuranceBills = insurance.insuranceBillLog.returnInsuranceBillLog()
        insuranceBills.sortByDescending { x -> x.billingDate.time }

        assertEquals(26, insuranceBills.count())
        assert(insuranceBills.last().billingDate.time >= startDate.time)
    }

    @Test
    fun generateInsuranceBills_Monthly_EndOfMonth() {
        val localDate = LocalDate.of(2023, 1, 31)
        val startDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

        val insurance = Insurance(0, "AA", startDate, 0, 1, 15.30.toBigDecimal(), startDate, 0)
        insurance.generateInsuranceBills()

        val insuranceBills = insurance.insuranceBillLog.returnInsuranceBillLog()
        insuranceBills.sortByDescending { x -> x.billingDate.time }

        assertEquals(12, insuranceBills.count())
        assert(insuranceBills.last().billingDate.time >= startDate.time)
    }

    @Test
    fun generateInsuranceBills_Annually_EndOfMonth() {
        val localDate = LocalDate.of(2023, 1, 31)
        val startDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

        val insurance = Insurance(0, "AA", startDate, 0, 2, 15.30.toBigDecimal(), startDate, 0)
        insurance.generateInsuranceBills()

        val insuranceBills = insurance.insuranceBillLog.returnInsuranceBillLog()
        insuranceBills.sortByDescending { x -> x.billingDate.time }

        assertEquals(1, insuranceBills.count())
        assert(insuranceBills.last().billingDate.time >= startDate.time)
    }

    @Test
    fun generateInsuranceBills_Fortnightly_LeapYear() {
        val localDate = LocalDate.of(2024, 1, 1)
        val startDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

        val insurance = Insurance(0, "AA", startDate, 0, 0, 15.30.toBigDecimal(), startDate, 0)
        insurance.generateInsuranceBills()

        val insuranceBills = insurance.insuranceBillLog.returnInsuranceBillLog()
        insuranceBills.sortByDescending { x -> x.billingDate.time }

        assertEquals(26, insuranceBills.count())
        assert(insuranceBills.last().billingDate.time >= startDate.time)
    }

    @Test
    fun generateInsuranceBills_Fortnightly_TimeOffset() {
        var localDate = LocalDate.of(2023, 10, 25)
        val startDate = Date.from(localDate.atTime(20, 40).toInstant(ZoneOffset.ofHours(12)))

        localDate = LocalDate.of(2024, 7, 25)
        val lastBillDate = Date.from(localDate.atTime(20, 40).toInstant(ZoneOffset.ofHours(12)))

        val insurance = Insurance(0, "AA", startDate, 0, 0, 15.30.toBigDecimal(), lastBillDate, 0)
        insurance.generateInsuranceBills()

        val insuranceBills = insurance.insuranceBillLog.returnInsuranceBillLog()
        insuranceBills.sortByDescending { x -> x.billingDate.time }

        assertEquals(26, insuranceBills.count())
        assert(insuranceBills.last().billingDate.time >= startDate.time)
    }

    @Test
    fun generateInsuranceBills_Monthly_TimeOffset() {
        var localDate = LocalDate.of(2023, 10, 25)
        val startDate = Date.from(localDate.atTime(20, 40).toInstant(ZoneOffset.ofHours(12)))
        val expectedStartDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

        localDate = LocalDate.of(2024, 7, 25)
        val lastBillDate = Date.from(localDate.atTime(20, 40).toInstant(ZoneOffset.ofHours(12)))

        val insurance = Insurance(0, "AA", startDate, 0, 1, 15.30.toBigDecimal(), lastBillDate, 0)
        insurance.generateInsuranceBills()

        val insuranceBills = insurance.insuranceBillLog.returnInsuranceBillLog()
        insuranceBills.sortByDescending { x -> x.billingDate.time }

        assertEquals(12, insuranceBills.count())
        assert(insuranceBills.last().billingDate.time >= expectedStartDate.time)
    }

    @Test
    fun generateInsuranceBills_Yearly_TimeOffset() {
        var localDate = LocalDate.of(2023, 10, 25)
        val startDate = Date.from(localDate.atTime(20, 40).toInstant(ZoneOffset.ofHours(12)))
        val expectedStartDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

        val insurance = Insurance(0, "AA", startDate, 0, 2, 15.30.toBigDecimal(), startDate, 0)
        insurance.generateInsuranceBills()

        val insuranceBills = insurance.insuranceBillLog.returnInsuranceBillLog()
        insuranceBills.sortByDescending { x -> x.billingDate.time }

        assertEquals(1, insuranceBills.count())
        assert(insuranceBills.last().billingDate.time >= expectedStartDate.time)
    }
}