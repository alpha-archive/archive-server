package com.alpha.archive.publicdata.controller

import com.alpha.archive.publicdata.service.PublicDataIngestionResult
import com.alpha.archive.publicdata.service.PublicDataIngestionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/public-data")
class PublicDataController(
    private val publicDataIngestionService: PublicDataIngestionService
) {
    
    /**
     * 공공 데이터를 수집합니다.
     */
    @PostMapping("/ingest")
    suspend fun ingestPublicData(
        @RequestBody(required = false) params: Map<String, Any>?
    ): ResponseEntity<PublicDataIngestionResult> {
        val result = publicDataIngestionService.ingestAllPublicData(params ?: emptyMap())
        return ResponseEntity.ok(result)
    }
    
    /**
     * 문화 데이터 포털에서 데이터를 수집합니다. (XML 기반)
     */
    @PostMapping("/ingest/culture")
    suspend fun ingestCultureData(
        @RequestParam(required = false) pageNo: Int?,
        @RequestParam(required = false) numOfRows: Int?,
        @RequestParam(required = false) from: String?,
        @RequestParam(required = false) to: String?,
        @RequestParam(required = false) serviceTp: String?,
        @RequestParam(required = false) sigungu: String?
    ): ResponseEntity<PublicDataIngestionResult> {
        val params = buildMap<String, Any> {
            pageNo?.let { put("pageNo", it) }
            numOfRows?.let { put("numOfRows", it) }
            from?.let { put("from", it) }
            to?.let { put("to", it) }
            serviceTp?.let { put("serviceTp", it) }
            sigungu?.let { put("sigungu", it) }
        }
        
        val result = publicDataIngestionService.ingestAllPublicData(params)
        return ResponseEntity.ok(result)
    }
    
    /**
     * 문화공공데이터에서 데이터를 수집합니다. (JSON 기반)
     * 2025년 이후까지 진행되는 행사만 저장됩니다.
     */
    @PostMapping("/ingest/cultural")
    suspend fun ingestCulturalData(
        @RequestParam(required = false) pageNo: Int?,
        @RequestParam(required = false) numOfRows: Int?
    ): ResponseEntity<PublicDataIngestionResult> {
        val params = buildMap<String, Any> {
            pageNo?.let { put("pageNo", it) }
            numOfRows?.let { put("numOfRows", it) }
        }
        
        val result = publicDataIngestionService.ingestAllPublicData(params)
        return ResponseEntity.ok(result)
    }
}
