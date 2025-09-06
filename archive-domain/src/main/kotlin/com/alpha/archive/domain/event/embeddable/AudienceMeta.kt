package com.alpha.archive.domain.event.embeddable

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class AudienceMeta {

    @Column(name = "price_text", length = 200)
    var priceText: String? = null

    @Column(name = "audience", length = 80)
    var audience: String? = null

    @Column(name = "contact", length = 100)
    var contact: String? = null

    @Column(name = "url", length = 400)
    var url: String? = null

    @Column(name = "image_url", length = 400)
    var imageUrl: String? = null
}