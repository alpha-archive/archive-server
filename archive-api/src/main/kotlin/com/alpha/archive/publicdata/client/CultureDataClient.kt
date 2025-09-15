package com.alpha.archive.publicdata.client

import com.alpha.archive.publicdata.dto.CultureDataResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "culture-data-client",
    url = "https://apis.data.go.kr",
    configuration = [CultureDataClientConfig::class]
)
interface CultureDataClient {
    
    @GetMapping("/B553457/cultureinfo/area2", consumes = ["application/xml"])
    fun getCultureEvents(
        @RequestParam("serviceKey") serviceKey: String,
        @RequestParam("PageNo") pageNo: Int = 1,
        @RequestParam("numOfrows") numOfRows: Int = 1000,
        @RequestParam("from") from: String? = null,
        @RequestParam("to") to: String? = null,
        @RequestParam("serviceTp") serviceTp: String? = null,
        @RequestParam("sigungu") sigungu: String? = null
    ): CultureDataResponse
}
