package com.alpha.archive.ncp

import com.alpha.archive.config.NcpObjectStorageProperties
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.ObjectCannedACL
import software.amazon.awssdk.services.s3.model.PutObjectRequest

@Service
class NcpObjectStorageService(
    private val s3Client: S3Client,
    private val properties: NcpObjectStorageProperties
) {

    /**
     * 파일을 Object Storage에 업로드하고 URL 반환
     */
    fun uploadFile(file: MultipartFile, objectKey: String): String {
        val putRequest = PutObjectRequest.builder()
            .bucket(properties.bucketName)
            .key(objectKey)
            .contentType(file.contentType)
            .contentLength(file.size)
            .acl(ObjectCannedACL.PUBLIC_READ) // 파일을 public으로 설정
            .build()

        val requestBody = RequestBody.fromInputStream(file.inputStream, file.size)
        s3Client.putObject(putRequest, requestBody)

        return buildPublicUrl(objectKey)
    }

    /**
     * Object Storage에서 파일 삭제
     */
    fun deleteFile(objectKey: String) {
        val deleteRequest = DeleteObjectRequest.builder()
            .bucket(properties.bucketName)
            .key(objectKey)
            .build()

        s3Client.deleteObject(deleteRequest)
    }

    /**
     * 공개 URL 생성 (HTTPS)
     */
    fun buildPublicUrl(objectKey: String): String {
        val httpsEndpoint = properties.endpoint.replace("http://", "https://")
        return "$httpsEndpoint/${properties.bucketName}/$objectKey"
    }

    /**
     * URL에서 object key 추출 (HTTP/HTTPS 모두 지원)
     */
    fun extractObjectKey(url: String): String {
        val httpsEndpoint = properties.endpoint.replace("http://", "https://")
        val httpsPrefix = "$httpsEndpoint/${properties.bucketName}/"
        val httpPrefix = "${properties.endpoint}/${properties.bucketName}/"
        
        return when {
            url.startsWith(httpsPrefix) -> url.substring(httpsPrefix.length)
            url.startsWith(httpPrefix) -> url.substring(httpPrefix.length)
            else -> throw IllegalArgumentException("잘못된 Object Storage URL 형식입니다: $url")
        }
    }
}