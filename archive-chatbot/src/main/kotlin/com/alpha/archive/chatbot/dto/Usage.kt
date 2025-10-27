package com.alpha.archive.chatbot.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class Usage(
    @JsonProperty("prompt_tokens")
    val promptTokens: Int = 0,

    @JsonProperty("completion_tokens")
    val completionTokens: Int = 0,

    @JsonProperty("total_tokens")
    val totalTokens: Int = 0,
)
