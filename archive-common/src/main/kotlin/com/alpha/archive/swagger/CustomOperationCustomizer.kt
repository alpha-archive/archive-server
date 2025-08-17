package com.alpha.archive.swagger

import com.alpha.archive.common.annotations.ArchiveDeleteMapping
import com.alpha.archive.common.annotations.ArchiveGetMapping
import com.alpha.archive.common.annotations.ArchivePatchMapping
import com.alpha.archive.common.annotations.ArchivePostMapping
import com.alpha.archive.common.annotations.ArchivePutMapping
import com.alpha.archive.common.dto.ApiResponse
import com.alpha.archive.exception.ErrorTitle
import com.alpha.archive.exception.annotation.CustomFailResponseAnnotation
import com.alpha.archive.exception.annotation.CustomFailResponseAnnotations
import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import io.swagger.v3.oas.models.security.SecurityRequirement
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod

@Component
class CustomOperationCustomizer : OperationCustomizer {
    override fun customize(operation: Operation, handlerMethod: HandlerMethod): Operation {
        val methodAnnotations = handlerMethod.method.declaredAnnotations
        val responses = operation.responses

        if (methodAnnotations.find { it is Hidden } != null) {
            return operation
        }

        for (annotation in methodAnnotations) {
            when (annotation) {
                is CustomFailResponseAnnotations -> {
                    for (j in annotation.value) {
                        val message = if (j.message == "") j.exception.message else j.message
                        handleCustomFailResponse(j.exception, message, responses)
                    }
                }
                is CustomFailResponseAnnotation -> {
                    val message = if (annotation.message == "") annotation.exception.message else annotation.message
                    handleCustomFailResponse(annotation.exception, message, responses)
                }
                is ArchiveGetMapping -> {
                    if (annotation.authenticated || annotation.hasRole.isNotEmpty()) {
                        operation.addSecurityItem(SecurityRequirement().addList("bearerAuth"))
                    }
                }
                is ArchiveDeleteMapping -> {
                    if (annotation.authenticated || annotation.hasRole.isNotEmpty()) {
                        operation.addSecurityItem(SecurityRequirement().addList("bearerAuth"))
                    }
                }
                is ArchivePostMapping -> {
                    if (annotation.authenticated || annotation.hasRole.isNotEmpty()) {
                        operation.addSecurityItem(SecurityRequirement().addList("bearerAuth"))
                    }
                }
                is ArchivePatchMapping -> {
                    if (annotation.authenticated || annotation.hasRole.isNotEmpty()) {
                        operation.addSecurityItem(SecurityRequirement().addList("bearerAuth"))
                    }
                }
                is ArchivePutMapping -> {
                    if (annotation.authenticated || annotation.hasRole.isNotEmpty()) {
                        operation.addSecurityItem(SecurityRequirement().addList("bearerAuth"))
                    }
                }
            }
        }

        operation.responses(responses)
        return operation
    }

    private fun handleCustomFailResponse(
        exception: ErrorTitle,
        message: String?,
        responses: ApiResponses
    ) {
        val statusCode = exception.status.value().toString()
        val response = responses.computeIfAbsent(statusCode) { SwaggerApiResponse() }
        val content = response.content ?: Content()
        val schema = Schema<Any>().`$ref`("#/components/schemas/ApiFailureResponse")
        val errorResponse = ApiResponse.Failure(
            message = message ?: exception.message,
            errorTitle = exception.name,
            errorCode = exception.status.value()
        )
        val mediaType = content.getOrPut("application/json") { MediaType().schema(schema) }
        val example = Example().value(errorResponse)
        mediaType.addExamples(errorResponse.message, example)
        content["application/json"] = mediaType
        response.content(content)
        responses.addApiResponse(statusCode, response)
    }
}
