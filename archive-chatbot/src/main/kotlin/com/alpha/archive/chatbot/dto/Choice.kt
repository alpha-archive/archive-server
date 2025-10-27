package com.alpha.archive.chatbot.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class Choice(
    val index: Int? = null,
    val message: Message? = null,

    @JsonProperty("finish_reason")
    val finishReason: String? = null
)