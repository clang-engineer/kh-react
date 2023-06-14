package com.hhkbdev.khreact.service.dto

import com.hhkbdev.khreact.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PointDTOTest {

    @Test
    fun dtoEqualsVerifier() {
        equalsVerifier(PointDTO::class)
        val pointDTO1 = PointDTO()
        pointDTO1.id = 1L
        val pointDTO2 = PointDTO()
        assertThat(pointDTO1).isNotEqualTo(pointDTO2)
        pointDTO2.id = pointDTO1.id
        assertThat(pointDTO1).isEqualTo(pointDTO2)
        pointDTO2.id = 2L
        assertThat(pointDTO1).isNotEqualTo(pointDTO2)
        pointDTO1.id = null
        assertThat(pointDTO1).isNotEqualTo(pointDTO2)
    }
}
