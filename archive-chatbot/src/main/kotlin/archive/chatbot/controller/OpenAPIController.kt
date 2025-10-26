package com.alpha.archive.chatbot.controller

import com.alpha.archive.chatbot.dto.OpenAPIResponse
import com.alpha.archive.chatbot.dto.QuestionRequest
import com.alpha.archive.chatbot.service.OpenAPIService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


// 테스트용 컨트롤러 추후 삭제
@RestController
@RequestMapping("/openapi")
class OpenAPIController(
    private val openAPIService: OpenAPIService
) {
    @PostMapping("/question")
    suspend fun sendQuestion(@RequestBody requestDto: QuestionRequest): OpenAPIResponse? {
        return openAPIService.askQuestion(requestDto)
    }
}