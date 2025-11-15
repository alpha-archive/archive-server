package com.alpha.archive.storage

import org.springframework.web.multipart.MultipartFile

/**
 * Object Storage 공통 인터페이스
 * NCP Object Storage와 AWS S3 모두를 지원
 */
interface ObjectStorageService {
    
    /**
     * 파일을 Object Storage에 업로드하고 URL 반환
     */
    fun uploadFile(file: MultipartFile, objectKey: String): String
    
    /**
     * Object Storage에서 파일 삭제
     */
    fun deleteFile(objectKey: String)
    
    /**
     * 공개 URL 생성
     */
    fun buildPublicUrl(objectKey: String): String
    
    /**
     * URL에서 object key 추출
     */
    fun extractObjectKey(url: String): String
}
