package com.hhkbdev.khreact.domain

import com.hhkbdev.khreact.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PointTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Point::class)
        val point1 = Point()
        point1.id = 1L
        val point2 = Point()
        point2.id = point1.id
        assertThat(point1).isEqualTo(point2)
        point2.id = 2L
        assertThat(point1).isNotEqualTo(point2)
        point1.id = null
        assertThat(point1).isNotEqualTo(point2)
    }
}
