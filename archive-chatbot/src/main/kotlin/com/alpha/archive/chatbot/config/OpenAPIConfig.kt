package com.alpha.archive.chatbot.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class OpenAPIConfig {

    @Value("\${openai.api.key}")
    lateinit var apiKey: String

    companion object {
        const val AUTHORIZATION = "Authorization"
        const val BEARER = "Bearer"
        const val MODEL = "gpt-4o"
        const val MEDIA_TYPE = "application/json; charset=UTF-8"
        const val URL = "https://api.openai.com/v1/chat/completions"
        const val MAX_TOKENS = 300
        const val TEMPERATURE = 0.7
        const val TOP_P = 1.0
    }
}