package com.alpha.archive.chatbot.dto

import com.alpha.archive.domain.event.enums.EventCategory
import java.time.LocalDateTime

data class EventDataDto(
    val activityDate: LocalDateTime?,
    val customTitle: String,
    val customCategory: EventCategory,
    val customLocation: String? = null,
    val rating: Int? = null,
    val memo: String? = null
)
