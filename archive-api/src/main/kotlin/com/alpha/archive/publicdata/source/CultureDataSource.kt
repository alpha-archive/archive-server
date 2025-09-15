package com.alpha.archive.publicdata.source

import com.alpha.archive.publicdata.client.CultureDataClient
import com.alpha.archive.publicdata.dto.CultureItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class CultureDataSource(
    private val cultureDataClient: CultureDataClient,
    @Value("\${public-data.culture.service-key}") private val serviceKey: String
) : PublicDataSource<CultureItem> {
    
    override val sourceName: String = "CULTURE_DATA_PORTAL"
    
    override suspend fun fetchData(params: Map<String, Any>): List<CultureItem> {
        return withContext(Dispatchers.IO) {
            try {
                val response = cultureDataClient.getCultureEvents(
                    serviceKey = serviceKey,
                    pageNo = params["pageNo"] as? Int ?: 1,
                    numOfRows = params["numOfRows"] as? Int ?: 1000,
                    from = params["from"] as? String,
                    to = params["to"] as? String,
                    serviceTp = params["serviceTp"] as? String,
                    sigungu = params["sigungu"] as? String
                )
                
                if (response.header.resultCode == "00") {
                    response.body.items.items
                } else {
                    throw RuntimeException("API Error: ${response.header.resultMsg}")
                }
            } catch (e: Exception) {
                throw RuntimeException("Failed to fetch culture data: ${e.message}", e)
            }
        }
    }
}
