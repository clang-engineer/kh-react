package com.hhkbdev.khreact.repository

import com.hhkbdev.khreact.domain.Point
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data JPA repository for the Point entity.
 */
@Suppress("unused")
@Repository
interface PointRepository : JpaRepository<Point, Long>
