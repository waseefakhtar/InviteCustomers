package com.waseefakhtar.invitecustomers.util

import junit.framework.Assert.*
import org.junit.Test

class DistanceUtilTest {

    @Test
    fun `Should assert equal when output is correct`()  {
        assertEquals(DistanceUtil.getDistance(124.5, 234.6), 6863.496357303651)
    }

    @Test
    fun `Should assert not same when output is not correct`() {
        assertNotSame(DistanceUtil.getDistance(124.5, 234.6), 6863.49)
        assertNotSame(DistanceUtil.getDistance(124.5, 234.6), 6863.49635730365112324)
        assertNotSame(DistanceUtil.getDistance(124.5, 234.6), -6863.49635730365112324)
    }
}