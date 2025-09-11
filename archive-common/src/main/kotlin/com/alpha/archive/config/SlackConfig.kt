package com.alpha.archive.config

import com.slack.api.Slack
import com.slack.api.methods.MethodsClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Slack 설정
 */
@Configuration
class SlackConfig {

    @Value("\${slack.webhook.url:}")
    private lateinit var slackWebhookUrl: String

    @Bean
    fun slack(): Slack = Slack.getInstance()

    @Bean
    fun slackWebhookUrl(): String = slackWebhookUrl
}
