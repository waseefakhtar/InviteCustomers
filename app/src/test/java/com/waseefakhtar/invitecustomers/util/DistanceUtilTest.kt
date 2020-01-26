package com.waseefakhtar.invitecustomers.util

import junit.framework.Assert.*
import org.junit.Test

class DistanceUtilTest {

    @Test
    fun `Should assert equal when output is correct`()  {
        assertEquals(DistanceUtil.getDistance(124.5, 234.6), 6863.496357303651)
        assertEquals(DistanceUtil.getDistance(12.25, -12.621), 7569.981193626974)
        assertEquals(DistanceUtil.getDistance(1242.11, 123.1), 12350.497937835235)
    }

    @Test
    fun `Should assert not same when output is not correct`() {
        assertNotSame(DistanceUtil.getDistance(124.5, 234.6), 6863.49)
        assertNotSame(DistanceUtil.getDistance(124.5, 234.6), 6863.49635730365112324)
        assertNotSame(DistanceUtil.getDistance(124.5, 234.6), -6863.49635730365112324)
    }
}