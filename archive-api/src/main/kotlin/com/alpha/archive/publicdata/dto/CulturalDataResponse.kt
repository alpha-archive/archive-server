package com.alpha.archive.publicdata.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CulturalDataResponse(
    @JsonProperty("response")
    val response: CulturalResponse
)

data class CulturalResponse(
    @JsonProperty("header")
    val header: CulturalHeader,
    
    @JsonProperty("body")
    val body: CulturalBody
)

data class CulturalHeader(
    @JsonProperty("resultCode")
    val resultCode: String,
    
    @JsonProperty("resultMsg")
    val resultMsg: String
)

data class CulturalBody(
    @JsonProperty("items")
    val items: CulturalItems
)

data class CulturalItems(
    @JsonProperty("item")
    val items: List<CulturalItem> = emptyList()
)

data class CulturalItem(
    @JsonProperty("TITLE")
    val title: String? = null,
    
    @JsonProperty("CNTC_INSTT_NM")
    val contactInstitutionName: String? = null,
    
    @JsonProperty("COLLECTED_DATE")
    val collectedDate: String? = null,
    
    @JsonProperty("ISSUED_DATE")
    val issuedDate: String? = null,
    
    @JsonProperty("DESCRIPTION")
    val description: String? = null,
    
    @JsonProperty("IMAGE_OBJECT")
    val imageObject: String? = null,
    
    @JsonProperty("LOCAL_ID")
    val localId: String? = null,
    
    @JsonProperty("URL")
    val url: String? = null,
    
    @JsonProperty("VIEW_COUNT")
    val viewCount: String? = null,
    
    @JsonProperty("SUB_DESCRIPTION")
    val subDescription: String? = null,
    
    @JsonProperty("SPATIAL_COVERAGE")
    val spatialCoverage: String? = null,
    
    @JsonProperty("EVENT_SITE")
    val eventSite: String? = null,
    
    @JsonProperty("GENRE")
    val genre: String? = null,
    
    @JsonProperty("DURATION")
    val duration: String? = null,
    
    @JsonProperty("NUMBER_PAGES")
    val numberPages: String? = null,
    
    @JsonProperty("TABLE_OF_CONTENTS")
    val tableOfContents: String? = null,
    
    @JsonProperty("AUTHOR")
    val author: String? = null,
    
    @JsonProperty("CONTACT_POINT")
    val contactPoint: String? = null,
    
    @JsonProperty("ACTOR")
    val actor: String? = null,
    
    @JsonProperty("CONTRIBUTOR")
    val contributor: String? = null,
    
    @JsonProperty("AUDIENCE")
    val audience: String? = null,
    
    @JsonProperty("CHARGE")
    val charge: String? = null,
    
    @JsonProperty("PERIOD")
    val period: String? = null,
    
    @JsonProperty("EVENT_PERIOD")
    val eventPeriod: String? = null
)
