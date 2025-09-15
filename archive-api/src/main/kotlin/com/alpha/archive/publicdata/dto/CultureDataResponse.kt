package com.alpha.archive.publicdata.dto

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "response")
data class CultureDataResponse(
    @JacksonXmlProperty(localName = "header")
    val header: ResponseHeader,
    
    @JacksonXmlProperty(localName = "body")
    val body: ResponseBody
)

data class ResponseHeader(
    @JacksonXmlProperty(localName = "resultCode")
    val resultCode: String,
    
    @JacksonXmlProperty(localName = "resultMsg")
    val resultMsg: String
)

data class ResponseBody(
    @JacksonXmlProperty(localName = "totalCount")
    val totalCount: Int,
    
    @JacksonXmlProperty(localName = "PageNo")
    val pageNo: Int,
    
    @JacksonXmlProperty(localName = "numOfrows")
    val numOfRows: Int,
    
    @JacksonXmlProperty(localName = "items")
    val items: CultureItems
)

data class CultureItems(
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "item")
    val items: List<CultureItem> = emptyList()
)

data class CultureItem(
    @JacksonXmlProperty(localName = "serviceName")
    val serviceName: String? = null,
    
    @JacksonXmlProperty(localName = "seq")
    val seq: String? = null,
    
    @JacksonXmlProperty(localName = "title")
    val title: String? = null,
    
    @JacksonXmlProperty(localName = "startDate")
    val startDate: String? = null,
    
    @JacksonXmlProperty(localName = "endDate")
    val endDate: String? = null,
    
    @JacksonXmlProperty(localName = "place")
    val place: String? = null,
    
    @JacksonXmlProperty(localName = "realmName")
    val realmName: String? = null,
    
    @JacksonXmlProperty(localName = "area")
    val area: String? = null,
    
    @JacksonXmlProperty(localName = "sigungu")
    val sigungu: String? = null,
    
    @JacksonXmlProperty(localName = "thumbnail")
    val thumbnail: String? = null,
    
    @JacksonXmlProperty(localName = "gpsX")
    val gpsX: String? = null,
    
    @JacksonXmlProperty(localName = "gpsY")
    val gpsY: String? = null
)
