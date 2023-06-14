package com.hhkbdev.khreact.service.mapper

import com.hhkbdev.khreact.domain.Point
import com.hhkbdev.khreact.service.dto.PointDTO
import org.mapstruct.*

/**
 * Mapper for the entity [Point] and its DTO [PointDTO].
 */
@Mapper(componentModel = "spring")
interface PointMapper :
    EntityMapper<PointDTO, Point>
