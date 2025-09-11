package com.alpha.archive.config

import com.slack.api.Slack
import com.slack.api.methods.MethodsClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SlackConfig {

    @Value("\${slack.bot.token}")
    private lateinit var slackBotToken: String

    @Value("\${slack.bot.channel:#백엔드_에러알림}")
    private lateinit var slackChannel: String

    @Bean
    fun slackMethodsClient(): MethodsClient {
        if (slackBotToken.isBlank()) {
            throw IllegalStateException("Slack Bot Token이 설정되지 않았습니다. slack.bot.token 속성을 확인해주세요.")
        }
        return Slack.getInstance().methods(slackBotToken)
    }

    @Bean
    fun slackBotToken(): String = slackBotToken

    @Bean
    fun slackChannel(): String = slackChannel
}
