package com.hhkbdev.khreact.web.rest

import com.hhkbdev.khreact.IntegrationTest
import com.hhkbdev.khreact.domain.Point
import com.hhkbdev.khreact.repository.PointRepository
import com.hhkbdev.khreact.service.mapper.PointMapper
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator
import java.util.Random
import java.util.concurrent.atomic.AtomicLong
import javax.persistence.EntityManager
import kotlin.test.assertNotNull

/**
 * Integration tests for the [PointResource] REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PointResourceIT {
    @Autowired
    private lateinit var pointRepository: PointRepository

    @Autowired
    private lateinit var pointMapper: PointMapper

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var validator: Validator

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var restPointMockMvc: MockMvc

    private lateinit var point: Point

    @BeforeEach
    fun initTest() {
        point = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createPoint() {
        val databaseSizeBeforeCreate = pointRepository.findAll().size
        // Create the Point
        val pointDTO = pointMapper.toDto(point)
        restPointMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(pointDTO))
        ).andExpect(status().isCreated)

        // Validate the Point in the database
        val pointList = pointRepository.findAll()
        assertThat(pointList).hasSize(databaseSizeBeforeCreate + 1)
        val testPoint = pointList[pointList.size - 1]

        assertThat(testPoint.title).isEqualTo(DEFAULT_TITLE)
        assertThat(testPoint.description).isEqualTo(DEFAULT_DESCRIPTION)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createPointWithExistingId() {
        // Create the Point with an existing ID
        point.id = 1L
        val pointDTO = pointMapper.toDto(point)

        val databaseSizeBeforeCreate = pointRepository.findAll().size
        // An entity with an existing ID cannot be created, so this API call must fail
        restPointMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(pointDTO))
        ).andExpect(status().isBadRequest)

        // Validate the Point in the database
        val pointList = pointRepository.findAll()
        assertThat(pointList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun checkTitleIsRequired() {
        val databaseSizeBeforeTest = pointRepository.findAll().size
        // set the field null
        point.title = null

        // Create the Point, which fails.
        val pointDTO = pointMapper.toDto(point)

        restPointMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(pointDTO))
        ).andExpect(status().isBadRequest)

        val pointList = pointRepository.findAll()
        assertThat(pointList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllPoints() {
        // Initialize the database
        pointRepository.saveAndFlush(point)

        // Get all the pointList
        restPointMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(point.id?.toInt())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getPoint() {
        // Initialize the database
        pointRepository.saveAndFlush(point)

        val id = point.id
        assertNotNull(id)

        // Get the point
        restPointMockMvc.perform(get(ENTITY_API_URL_ID, point.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(point.id?.toInt()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingPoint() {
        // Get the point
        restPointMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun putExistingPoint() {
        // Initialize the database
        pointRepository.saveAndFlush(point)

        val databaseSizeBeforeUpdate = pointRepository.findAll().size

        // Update the point
        val updatedPoint = pointRepository.findById(point.id).get()
        // Disconnect from session so that the updates on updatedPoint are not directly saved in db
        em.detach(updatedPoint)
        updatedPoint.title = UPDATED_TITLE
        updatedPoint.description = UPDATED_DESCRIPTION
        val pointDTO = pointMapper.toDto(updatedPoint)

        restPointMockMvc.perform(
            put(ENTITY_API_URL_ID, pointDTO.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(pointDTO))
        ).andExpect(status().isOk)

        // Validate the Point in the database
        val pointList = pointRepository.findAll()
        assertThat(pointList).hasSize(databaseSizeBeforeUpdate)
        val testPoint = pointList[pointList.size - 1]
        assertThat(testPoint.title).isEqualTo(UPDATED_TITLE)
        assertThat(testPoint.description).isEqualTo(UPDATED_DESCRIPTION)
    }

    @Test
    @Transactional
    fun putNonExistingPoint() {
        val databaseSizeBeforeUpdate = pointRepository.findAll().size
        point.id = count.incrementAndGet()

        // Create the Point
        val pointDTO = pointMapper.toDto(point)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPointMockMvc.perform(
            put(ENTITY_API_URL_ID, pointDTO.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(pointDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the Point in the database
        val pointList = pointRepository.findAll()
        assertThat(pointList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithIdMismatchPoint() {
        val databaseSizeBeforeUpdate = pointRepository.findAll().size
        point.id = count.incrementAndGet()

        // Create the Point
        val pointDTO = pointMapper.toDto(point)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPointMockMvc.perform(
            put(ENTITY_API_URL_ID, count.incrementAndGet())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(pointDTO))
        ).andExpect(status().isBadRequest)

        // Validate the Point in the database
        val pointList = pointRepository.findAll()
        assertThat(pointList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithMissingIdPathParamPoint() {
        val databaseSizeBeforeUpdate = pointRepository.findAll().size
        point.id = count.incrementAndGet()

        // Create the Point
        val pointDTO = pointMapper.toDto(point)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPointMockMvc.perform(
            put(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(pointDTO))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the Point in the database
        val pointList = pointRepository.findAll()
        assertThat(pointList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun partialUpdatePointWithPatch() {
        pointRepository.saveAndFlush(point)

        val databaseSizeBeforeUpdate = pointRepository.findAll().size

// Update the point using partial update
        val partialUpdatedPoint = Point().apply {
            id = point.id

            title = UPDATED_TITLE
            description = UPDATED_DESCRIPTION
        }

        restPointMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedPoint.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedPoint))
        )
            .andExpect(status().isOk)

// Validate the Point in the database
        val pointList = pointRepository.findAll()
        assertThat(pointList).hasSize(databaseSizeBeforeUpdate)
        val testPoint = pointList.last()
        assertThat(testPoint.title).isEqualTo(UPDATED_TITLE)
        assertThat(testPoint.description).isEqualTo(UPDATED_DESCRIPTION)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun fullUpdatePointWithPatch() {
        pointRepository.saveAndFlush(point)

        val databaseSizeBeforeUpdate = pointRepository.findAll().size

// Update the point using partial update
        val partialUpdatedPoint = Point().apply {
            id = point.id

            title = UPDATED_TITLE
            description = UPDATED_DESCRIPTION
        }

        restPointMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedPoint.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedPoint))
        )
            .andExpect(status().isOk)

// Validate the Point in the database
        val pointList = pointRepository.findAll()
        assertThat(pointList).hasSize(databaseSizeBeforeUpdate)
        val testPoint = pointList.last()
        assertThat(testPoint.title).isEqualTo(UPDATED_TITLE)
        assertThat(testPoint.description).isEqualTo(UPDATED_DESCRIPTION)
    }

    @Throws(Exception::class)
    fun patchNonExistingPoint() {
        val databaseSizeBeforeUpdate = pointRepository.findAll().size
        point.id = count.incrementAndGet()

        // Create the Point
        val pointDTO = pointMapper.toDto(point)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPointMockMvc.perform(
            patch(ENTITY_API_URL_ID, pointDTO.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(pointDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the Point in the database
        val pointList = pointRepository.findAll()
        assertThat(pointList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithIdMismatchPoint() {
        val databaseSizeBeforeUpdate = pointRepository.findAll().size
        point.id = count.incrementAndGet()

        // Create the Point
        val pointDTO = pointMapper.toDto(point)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPointMockMvc.perform(
            patch(ENTITY_API_URL_ID, count.incrementAndGet())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(pointDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the Point in the database
        val pointList = pointRepository.findAll()
        assertThat(pointList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithMissingIdPathParamPoint() {
        val databaseSizeBeforeUpdate = pointRepository.findAll().size
        point.id = count.incrementAndGet()

        // Create the Point
        val pointDTO = pointMapper.toDto(point)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPointMockMvc.perform(
            patch(ENTITY_API_URL)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(pointDTO))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the Point in the database
        val pointList = pointRepository.findAll()
        assertThat(pointList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deletePoint() {
        // Initialize the database
        pointRepository.saveAndFlush(point)
        val databaseSizeBeforeDelete = pointRepository.findAll().size
        // Delete the point
        restPointMockMvc.perform(
            delete(ENTITY_API_URL_ID, point.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val pointList = pointRepository.findAll()
        assertThat(pointList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_TITLE = "AAAAAAAAAA"
        private const val UPDATED_TITLE = "BBBBBBBBBB"

        private const val DEFAULT_DESCRIPTION = "AAAAAAAAAA"
        private const val UPDATED_DESCRIPTION = "BBBBBBBBBB"

        private val ENTITY_API_URL: String = "/api/points"
        private val ENTITY_API_URL_ID: String = ENTITY_API_URL + "/{id}"

        private val random: Random = Random()
        private val count: AtomicLong = AtomicLong(random.nextInt().toLong() + (2 * Integer.MAX_VALUE))

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Point {
            val point = Point(
                title = DEFAULT_TITLE,

                description = DEFAULT_DESCRIPTION

            )

            return point
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Point {
            val point = Point(
                title = UPDATED_TITLE,

                description = UPDATED_DESCRIPTION

            )

            return point
        }
    }
}
