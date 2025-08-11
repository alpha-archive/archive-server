package com.alpha.archive.exception

import com.alpha.archive.common.dto.ApiResponse
import com.alpha.archive.util.ResponseUtil
import com.alpha.archive.util.toResponseEntity
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import feign.FeignException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.catalina.connector.ClientAbortException
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.resource.NoResourceFoundException

/**
 * 전역 예외 처리 컨트롤러
 */
@RestControllerAdvice
class ControllerAdvice {

    companion object {
        private val logger = LoggerFactory.getLogger(ControllerAdvice::class.java)
        
        private fun logError(
            request: HttpServletRequest,
            exception: Exception,
            exceptionType: String = exception::class.simpleName ?: "Exception"
        ) {
            logger.error(
                "{}: {} {} errorMessage={}",
                exceptionType,
                request.method,
                request.requestURI,
                exception.message,
                exception
            )
        }
    }

    /**
     * 최상위 Exception 처리 (ApiException 제외한 모든 미처리 예외)
     */
    @ExceptionHandler(Exception::class)
    fun handleUnhandledException(
        exception: Exception,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<ApiResponse.Failure> {
        logError(request, exception, "UnhandledException")
        return ErrorTitle.InternalServerError.toResponseEntity()
    }

    /**
     * 존재하지 않는 리소스 예외 처리
     */
    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFoundException(
        exception: NoResourceFoundException,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<ApiResponse.Failure> {
        logError(request, exception)
        return ErrorTitle.NotFoundEndpoint.toResponseEntity()
    }

    /**
     * 지원하지 않는 HTTP 메서드 예외 처리
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupportedException(
        exception: HttpRequestMethodNotSupportedException,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<ApiResponse.Failure> {
        logError(request, exception)
        return ErrorTitle.MethodNotAllowed.toResponseEntity(exception.message)
    }

    /**
     * API 예외 처리
     */
    @ExceptionHandler(ApiException::class)
    fun handleApiException(
        exception: ApiException,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<ApiResponse.Failure> {
        logError(request, exception)
        return exception.toResponseEntity()
    }

    /**
     * 요청 DTO 검증 실패 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        exception: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse.Failure> {
        logError(request, exception)
        val errorMessage = exception.bindingResult.fieldError?.defaultMessage
        return ErrorTitle.InvalidInputValue.toResponseEntity(errorMessage)
    }

    /**
     * HTTP 메시지 읽기 실패 예외 처리 (주로 JSON 파싱 오류)
     */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(
        exception: HttpMessageNotReadableException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse.Failure> {
        logError(request, exception)
        
        return when {
            exception.message?.contains("Enum class") == true -> 
                ErrorTitle.InvalidEnumValue.toResponseEntity()
            
            exception.cause is MismatchedInputException -> {
                val cause = exception.cause as MismatchedInputException
                val fieldName = cause.path.joinToString(".") { it.fieldName }
                ErrorTitle.BadRequest.toResponseEntity("$fieldName 은(는) 필수값입니다.")
            }
            
            else -> ErrorTitle.BadRequest.toResponseEntity()
        }
    }

    /**
     * Feign 클라이언트 예외 처리
     */
    @ExceptionHandler(FeignException::class)
    fun handleFeignException(
        exception: FeignException,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<ApiResponse.Failure> {
        logError(request, exception)
        return ErrorTitle.FeignClientError.toResponseEntity()
    }

    /**
     * 메서드 인자 타입 불일치 예외 처리
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatchException(
        exception: MethodArgumentTypeMismatchException,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<ApiResponse.Failure> {
        logError(request, exception)
        return ErrorTitle.InvalidQueryParameter.toResponseEntity()
    }

    /**
     * 클라이언트 연결 중단 예외 처리 (로그 없이 무시)
     */
    @ExceptionHandler(ClientAbortException::class)
    fun handleClientAbortException(
        exception: ClientAbortException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse.Failure>? = null
}