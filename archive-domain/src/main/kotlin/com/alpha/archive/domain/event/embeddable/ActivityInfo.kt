package com.alpha.archive.domain.event.embeddable

import com.alpha.archive.domain.event.enums.EventCategory
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
data class ActivityInfo(
    @Column(name = "custom_title", length = 300)
    val customTitle: String? = null,

    @Column(name = "custom_category", length = 50)
    @Enumerated(EnumType.STRING)
    val customCategory: EventCategory,

    @Column(name = "custom_location", length = 200)
    val customLocation: String? = null,

    @Column(name = "rating", nullable = true)
    val rating: Int? = null, // 1-5점 평점

    @Column(name = "memo", columnDefinition = "text")
    val memo: String? = null
)
