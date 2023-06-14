package com.hhkbdev.khreact.service.dto

import java.io.Serializable
import java.util.Objects
import javax.validation.constraints.*

/**
 * A DTO for the [com.hhkbdev.khreact.domain.Point] entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
data class PointDTO(

    var id: Long? = null,

    @get: NotNull
    @get: Size(min = 5, max = 20)
    var title: String? = null,

    var description: String? = null
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PointDTO) return false
        val pointDTO = other
        if (this.id == null) {
            return false
        }
        return Objects.equals(this.id, pointDTO.id)
    }

    override fun hashCode() = Objects.hash(this.id)
}
