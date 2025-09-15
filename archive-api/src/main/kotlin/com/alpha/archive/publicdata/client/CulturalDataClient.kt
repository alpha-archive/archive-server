package com.alpha.archive.publicdata.client

import com.alpha.archive.publicdata.dto.CulturalDataResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "cultural-data-client",
    url = "https://api.kcisa.kr"
)
interface CulturalDataClient {
    
    @GetMapping("/openapi/API_CCA_145/request")
    fun getCulturalEvents(
        @RequestParam("serviceKey") serviceKey: String,
        @RequestParam("pageNo") pageNo: Int = 1,
        @RequestParam("numOfRows") numOfRows: Int = 9129
    ): CulturalDataResponse
}
