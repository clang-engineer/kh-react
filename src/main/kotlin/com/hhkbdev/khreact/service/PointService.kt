package com.hhkbdev.khreact.service
import com.hhkbdev.khreact.service.dto.PointDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.Optional

/**
 * Service Interface for managing [com.hhkbdev.khreact.domain.Point].
 */
interface PointService {

    /**
     * Save a point.
     *
     * @param pointDTO the entity to save.
     * @return the persisted entity.
     */
    fun save(pointDTO: PointDTO): PointDTO

    /**
     * Updates a point.
     *
     * @param pointDTO the entity to update.
     * @return the persisted entity.
     */
    fun update(pointDTO: PointDTO): PointDTO

    /**
     * Partially updates a point.
     *
     * @param pointDTO the entity to update partially.
     * @return the persisted entity.
     */
    fun partialUpdate(pointDTO: PointDTO): Optional<PointDTO>

    /**
     * Get all the points.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    fun findAll(pageable: Pageable): Page<PointDTO>

    /**
     * Get the "id" point.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: Long): Optional<PointDTO>

    /**
     * Delete the "id" point.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long)
}
