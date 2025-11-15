package com.alpha.archive.aws

import com.alpha.archive.config.AwsS3Properties
import com.alpha.archive.storage.ObjectStorageService
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest

/**
 * AWS S3 서비스 (Elastic Beanstalk 환경용)
 */
@Service
class AwsS3Service(
    private val s3Client: S3Client,
    private val properties: AwsS3Properties
) : ObjectStorageService {

    /**
     * 파일을 S3에 업로드하고 URL 반환
     */
    override fun uploadFile(file: MultipartFile, objectKey: String): String {
        val putRequest = PutObjectRequest.builder()
            .bucket(properties.bucketName)
            .key(objectKey)
            .contentType(file.contentType)
            .contentLength(file.size)
            .build()

        val requestBody = RequestBody.fromInputStream(file.inputStream, file.size)
        s3Client.putObject(putRequest, requestBody)

        return buildPublicUrl(objectKey)
    }

    /**
     * S3에서 파일 삭제
     */
    override fun deleteFile(objectKey: String) {
        val deleteRequest = DeleteObjectRequest.builder()
            .bucket(properties.bucketName)
            .key(objectKey)
            .build()

        s3Client.deleteObject(deleteRequest)
    }

    /**
     * 공개 URL 생성
     */
    override fun buildPublicUrl(objectKey: String): String {
        // S3 표준 URL 형식
        return "https://${properties.bucketName}.s3.${properties.region}.amazonaws.com/$objectKey"
    }

    /**
     * URL에서 object key 추출
     */
    override fun extractObjectKey(url: String): String {
        val prefix = "https://${properties.bucketName}.s3.${properties.region}.amazonaws.com/"
        
        return when {
            url.startsWith(prefix) -> url.substring(prefix.length)
            else -> throw IllegalArgumentException("잘못된 S3 URL 형식입니다: $url")
        }
    }
}
