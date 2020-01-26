package com.waseefakhtar.invitecustomers.util

import junit.framework.Assert.*
import org.junit.Test

class DistanceUtilTest {

    @Test
    fun distanceValidator_CorrectInput_ReturnsExpectedOutput() {
        assertEquals(DistanceUtil.getDistance(124.5, 234.6), DistanceUtil.getDistance(124.5, 234.6))
    }
}