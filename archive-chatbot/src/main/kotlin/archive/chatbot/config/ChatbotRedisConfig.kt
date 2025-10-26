package com.alpha.archive.chatbot.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class ChatbotRedisConfig {

    /**
     * 챗봇 모듈 전용 RedisTemplate Bean 생성.
     * Value를 JSON으로 직렬화하여 List<Message> 같은 객체를 저장.
     * * @param connectionFactory - 이 Bean은 'auth' 모듈의 RedisConfig가
     * 이미 만들어 둔 것을 Spring이 찾아서 자동으로 주입해 줍니다.
     */
    @Bean
    fun chatbotRedisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = connectionFactory

        // Key는 String을 사용
        template.keySerializer = StringRedisSerializer()

        // Value는 객체를 JSON으로 변환하는 Serializer를 사용
        template.valueSerializer = GenericJackson2JsonRedisSerializer()

        // Hash용 Serializer도 동일하게 설정
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = GenericJackson2JsonRedisSerializer()

        return template
    }
}