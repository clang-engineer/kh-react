package com.hhkbdev.khreact.service.impl

import com.hhkbdev.khreact.domain.Point
import com.hhkbdev.khreact.repository.PointRepository
import com.hhkbdev.khreact.service.PointService
import com.hhkbdev.khreact.service.dto.PointDTO
import com.hhkbdev.khreact.service.mapper.PointMapper
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

/**
 * Service Implementation for managing [Point].
 */
@Service
@Transactional
class PointServiceImpl(
    private val pointRepository: PointRepository,
    private val pointMapper: PointMapper,
) : PointService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun save(pointDTO: PointDTO): PointDTO {
        log.debug("Request to save Point : $pointDTO")
        var point = pointMapper.toEntity(pointDTO)
        point = pointRepository.save(point)
        return pointMapper.toDto(point)
    }

    override fun update(pointDTO: PointDTO): PointDTO {
        log.debug("Request to update Point : {}", pointDTO)
        var point = pointMapper.toEntity(pointDTO)
        point = pointRepository.save(point)
        return pointMapper.toDto(point)
    }

    override fun partialUpdate(pointDTO: PointDTO): Optional<PointDTO> {
        log.debug("Request to partially update Point : {}", pointDTO)

        return pointRepository.findById(pointDTO.id)
            .map {
                pointMapper.partialUpdate(it, pointDTO)
                it
            }
            .map { pointRepository.save(it) }
            .map { pointMapper.toDto(it) }
    }

    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<PointDTO> {
        log.debug("Request to get all Points")
        return pointRepository.findAll(pageable)
            .map(pointMapper::toDto)
    }

    @Transactional(readOnly = true)
    override fun findOne(id: Long): Optional<PointDTO> {
        log.debug("Request to get Point : $id")
        return pointRepository.findById(id)
            .map(pointMapper::toDto)
    }

    override fun delete(id: Long) {
        log.debug("Request to delete Point : $id")

        pointRepository.deleteById(id)
    }
}
