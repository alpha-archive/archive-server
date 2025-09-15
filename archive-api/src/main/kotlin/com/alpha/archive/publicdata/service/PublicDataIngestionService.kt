package com.alpha.archive.publicdata.service

import com.alpha.archive.publicdata.dto.CultureItem
import com.alpha.archive.publicdata.dto.CulturalItem
import com.alpha.archive.publicdata.mapper.CultureDataMapper
import com.alpha.archive.publicdata.mapper.CulturalDataMapper
import com.alpha.archive.publicdata.source.PublicDataSource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PublicDataIngestionService(
    private val dataSources: List<PublicDataSource<*>>,
    private val cultureDataMapper: CultureDataMapper,
    private val culturalDataMapper: CulturalDataMapper,
    private val publicEventService: PublicEventService
) {
    
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    /**
     * 모든 활성화된 데이터 소스에서 공공 데이터를 수집합니다.
     */
    @Transactional
    suspend fun ingestAllPublicData(params: Map<String, Any> = emptyMap()): PublicDataIngestionResult {
        logger.info("Starting public data ingestion with params: $params")
        
        val results = coroutineScope {
            dataSources
                .filter { it.isEnabled() }
                .map { dataSource ->
                    async {
                        try {
                            ingestFromSource(dataSource, params)
                        } catch (e: Exception) {
                            logger.error("Failed to ingest data from source: ${dataSource.sourceName}", e)
                            SourceIngestionResult(dataSource.sourceName, 0, 0, listOf(e.message ?: "Unknown error"))
                        }
                    }
                }
                .awaitAll()
        }
        
        val totalProcessed = results.sumOf { it.processed }
        val totalSaved = results.sumOf { it.saved }
        val allErrors = results.flatMap { it.errors }
        
        logger.info("Public data ingestion completed. Processed: $totalProcessed, Saved: $totalSaved, Errors: ${allErrors.size}")
        
        return PublicDataIngestionResult(totalProcessed, totalSaved, results, allErrors)
    }
    
    /**
     * 특정 데이터 소스에서 데이터를 수집합니다.
     */
    private suspend fun ingestFromSource(
        dataSource: PublicDataSource<*>,
        params: Map<String, Any>
    ): SourceIngestionResult {
        logger.info("Ingesting data from source: ${dataSource.sourceName}")
        
        val errors = mutableListOf<String>()
        var processed = 0
        var saved = 0
        
        try {
            @Suppress("UNCHECKED_CAST")
            when (dataSource.sourceName) {
                "CULTURE_DATA_PORTAL" -> {
                    val cultureDataSource = dataSource as PublicDataSource<CultureItem>
                    val items = cultureDataSource.fetchData(params)
                    processed = items.size
                    
                    val publicEvents = items.mapNotNull { item ->
                        try {
                            cultureDataMapper.mapToPublicEvent(item, dataSource.sourceName)
                        } catch (e: Exception) {
                            errors.add("Failed to map item ${item.seq}: ${e.message}")
                            null
                        }
                    }
                    
                    saved = publicEventService.savePublicEvents(publicEvents)
                }
                "CULTURAL_DATA_PORTAL" -> {
                    val culturalDataSource = dataSource as PublicDataSource<CulturalItem>
                    val items = culturalDataSource.fetchData(params)
                    processed = items.size
                    
                    val publicEvents = items.mapNotNull { item ->
                        try {
                            culturalDataMapper.mapToPublicEvent(item, dataSource.sourceName)
                        } catch (e: Exception) {
                            errors.add("Failed to map item ${item.localId}: ${e.message}")
                            null
                        }
                    }
                    
                    saved = publicEventService.savePublicEvents(publicEvents)
                }
                else -> {
                    errors.add("Unknown data source: ${dataSource.sourceName}")
                }
            }
        } catch (e: Exception) {
            errors.add("Failed to fetch data from source ${dataSource.sourceName}: ${e.message}")
        }
        
        return SourceIngestionResult(dataSource.sourceName, processed, saved, errors)
    }
}

data class PublicDataIngestionResult(
    val totalProcessed: Int,
    val totalSaved: Int,
    val sourceResults: List<SourceIngestionResult>,
    val errors: List<String>
)

data class SourceIngestionResult(
    val sourceName: String,
    val processed: Int,
    val saved: Int,
    val errors: List<String>
)
