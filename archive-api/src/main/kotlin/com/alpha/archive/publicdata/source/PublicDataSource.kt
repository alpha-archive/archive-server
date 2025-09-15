package com.alpha.archive.publicdata.source

/**
 * 공공 데이터 소스를 나타내는 인터페이스
 * 다양한 공공 데이터 API를 추상화합니다.
 */
interface PublicDataSource<T> {
    /**
     * 데이터 소스의 고유 식별자
     */
    val sourceName: String
    
    /**
     * 공공 데이터를 가져옵니다.
     * @param params 검색 파라미터
     * @return 공공 데이터 목록
     */
    suspend fun fetchData(params: Map<String, Any>): List<T>
    
    /**
     * 데이터 소스가 활성화되어 있는지 확인
     */
    fun isEnabled(): Boolean = true
}
