package com.alpha.archive.domain.event.embeddable

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class PlaceInfo(
    @Column(name = "place_name", length = 200)
    val placeName: String? = null,

    @Column(name = "place_address", length = 300)
    val placeAddress: String? = null,

    @Column(name = "place_city", length = 50)
    val placeCity: String? = null,

    @Column(name = "place_district", length = 50)
    val placeDistrict: String? = null,

    @Column(name = "place_latitude")
    val placeLatitude: Double? = null,

    @Column(name = "place_longitude")
    val placeLongitude: Double? = null,

    @Column(name = "place_phone", length = 50)
    val placePhone: String? = null,

    @Column(name = "place_homepage", length = 400)
    val placeHomepage: String? = null
)