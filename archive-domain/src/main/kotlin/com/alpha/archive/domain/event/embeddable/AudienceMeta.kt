package com.alpha.archive.domain.event.embeddable

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class AudienceMeta(
    @Column(name = "price_text", length = 200)
    val priceText: String? = null,

    @Column(name = "audience", length = 80)
    val audience: String? = null,

    @Column(name = "contact", length = 100)
    val contact: String? = null,

    @Column(name = "url", length = 400)
    val url: String? = null,

    @Column(name = "image_url", length = 400)
    val imageUrl: String? = null
)