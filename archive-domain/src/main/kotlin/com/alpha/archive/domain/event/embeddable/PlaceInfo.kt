package com.alpha.archive.domain.event.embeddable

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class PlaceInfo {

    @Column(name = "place_name", length = 200)
    var placeName: String? = null

    @Column(name = "place_address", length = 300)
    var placeAdress: String? = null

    @Column(name = "place_city", length = 50)
    var placeCity: String? = null

    @Column(name = "place_district", length = 50)
    var placeDistrict: String? = null

    @Column(name = "place_latitude")
    var placeLatitude: Double? = null

    @Column(name = "place_longitude")
    var placeLongitude: Double? = null

    @Column(name = "place_phone", length = 50)
    var placePhone: String? = null

    @Column(name = "place_homepage", length = 400)
    var placeHomepage: String? = null
}