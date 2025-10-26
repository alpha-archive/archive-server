package com.alpha.archive.chatbot

import com.alpha.archive.auth.security.service.ArchiveUserDetails
import com.alpha.archive.chatbot.dto.ChatbotMessageRequest
import com.alpha.archive.chatbot.dto.ChatbotMessageResponse
import com.alpha.archive.chatbot.service.ChatbotService
import com.alpha.archive.common.annotations.ArchivePostMapping
import com.alpha.archive.exception.ErrorTitle
import com.alpha.archive.exception.annotation.CustomFailResponseAnnotation
import com.alpha.archive.util.ResponseUtil
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/chatbot")
@Tag(name = "챗봇 관련 API", description = "사용자 경험 기록을 돕는 챗봇 API 입니다.")
@SecurityRequirement(name = "bearerAuth")
class ChatbotController(
    private val chatbotService: ChatbotService
) {
    @ArchivePostMapping("/message", authenticated = true)
    @ApiResponse(responseCode = "200", description = "챗봇 메시지 처리 성공")
    @Operation(summary = "챗봇과 대화하는 API", description = "사용자의 메시지를 받아 챗봇의 응답을 반환합니다. 대화를 통해 활동 기록을 완성합니다.")
    @CustomFailResponseAnnotation(ErrorTitle.BadRequest)
    @CustomFailResponseAnnotation(ErrorTitle.NotFoundUser)
    suspend fun handleMessage(
        @AuthenticationPrincipal userDetails: ArchiveUserDetails,
        @RequestBody @Valid request: ChatbotMessageRequest
    ): ResponseEntity<com.alpha.archive.common.dto.ApiResponse.Success<ChatbotMessageResponse>> {
        // 인증된 사용자 정보와 메시지를 서비스에 전달.
        val replyMessage = chatbotService.continueConversation(userDetails.getUserId(), request.message)

        // 서비스의 응답을 DTO에 저장.
        val response = ChatbotMessageResponse(reply = replyMessage)

        // 기존 프로젝트의 응답 형식에 맞춰 성공 응답 반환.
        return ResponseUtil.success("메시지가 성공적으로 처리되었습니다.", response)
    }
}