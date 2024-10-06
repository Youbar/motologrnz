package com.motologr

import com.motologr.data.DataHelper
import org.junit.Assert
import org.junit.Test

class DataHelperUnitTests : UnitTestBase() {
    private val inputName = ""

    @Test
    fun validateIntegerInput_emptyString() {
        val input = ""
        Assert.assertEquals(false, DataHelper.isValidIntegerInput(input, inputName) { })
    }

    @Test
    fun validateIntegerInput_spaceString() {
        val input = " "
        Assert.assertEquals(false, DataHelper.isValidIntegerInput(input, inputName) { })
    }

    @Test
    fun validateIntegerInput_textString() {
        val input = "MOTOLOGR.NZ"
        Assert.assertEquals(false, DataHelper.isValidIntegerInput(input, inputName) { })
    }

    @Test
    fun validateIntegerInput_nullString() {
        val input = "MOTOLOGR.NZ"
        Assert.assertEquals(false, DataHelper.isValidIntegerInput(input, inputName) { })
    }

    @Test
    fun validateIntegerInput_zeroString() {
        val input = "0"
        Assert.assertEquals(false, DataHelper.isValidIntegerInput(input, inputName) { })
    }

    @Test
    fun validateIntegerInput_negativeString() {
        val input = "-1"
        Assert.assertEquals(false, DataHelper.isValidIntegerInput(input, inputName) { })
    }

    @Test
    fun validateIntegerInput_nonsenseString() {
        val input = "-1.5000.20"
        Assert.assertEquals(false, DataHelper.isValidIntegerInput(input, inputName) { })
    }

    @Test
    fun validateIntegerInput_overflowString() {
        val input = "123515024959023409509249"
        Assert.assertEquals(false, DataHelper.isValidIntegerInput(input, inputName) { })
    }

    @Test
    fun validateIntegerInput_correctString() {
        val input = "150"
        Assert.assertEquals(true, DataHelper.isValidIntegerInput(input, inputName) { })
    }
}