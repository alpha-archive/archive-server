package com.alpha.archive.domain.event

import com.alpha.archive.exception.ApiException
import com.alpha.archive.exception.ErrorTitle
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.OffsetDateTime


interface PublicEventService{
    fun getById(id: String): PublicEvent
    fun getBySourceAndEventId(source: String, sourceEventId: String): PublicEvent?
    fun upsertBySourceEventId(e: PublicEvent): PublicEvent
    fun save(e: PublicEvent): PublicEvent
    fun listByPeriod(start: OffsetDateTime, end: OffsetDateTime): List<PublicEvent>
}

@Service
class PublicEventServiceImpl(
    private val eventRepository: PublicEventRepository
) : PublicEventService {

    override fun getById(id: String): PublicEvent {
        return eventRepository.findByIdOrNull(id) ?: throw ApiException(ErrorTitle.NotFoundEndpoint)
    }

    override fun getBySourceAndEventId(source: String, sourceEventId: String): PublicEvent? {
        return eventRepository.findBySourceAndSourceEventId(source, sourceEventId)
    }

    override fun save(e: PublicEvent): PublicEvent {
        return eventRepository.save(e)
    }

    override fun upsertBySourceEventId(e: PublicEvent): PublicEvent {
        val existingEvent = eventRepository.findBySourceAndSourceEventId(e.source, e.sourceEventId)
        return if (existingEvent != null) {
            existingEvent.refreshFromIngestion(
                title = e.title,
                description = e.description,
                category = e.category,
                startAt = e.startAt,
                endAt = e.endAt,
                placeName = e.placeName,
                placeAddress = e.placeAddress,
                placeCity = e.placeCity,
                placeDistrict = e.placeDistrict,
                placeLatitude = e.placeLatitude,
                placeLongitude = e.placeLongitude,
                placePhone = e.placePhone,
                placeHomepage = e.placeHomepage,
                priceText = e.priceText,
                audience = e.audience,
                contact = e.contact,
                url = e.url,
                imageUrl = e.imageUrl,
                status = e.status,
                rawPayload = e.rawPayload,
                ingestedAt = OffsetDateTime.now(),
            )
            eventRepository.save(existingEvent)
        } else {
            eventRepository.save(e)
        }
    }

    override fun listByPeriod(start: OffsetDateTime, end: OffsetDateTime): List<PublicEvent> {
        return eventRepository.findAllByStartAtBetween(start, end)
    }
}