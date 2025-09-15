package com.alpha.archive.publicdata.client

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import feign.codec.Decoder
import org.springframework.beans.factory.ObjectFactory
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder
import org.springframework.cloud.openfeign.support.SpringDecoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter

@Configuration
class CultureDataClientConfig {
    
    @Bean
    fun xmlDecoder(): Decoder {
        val xmlMapper = XmlMapper().apply {
            // XML 파싱 설정
            findAndRegisterModules()
        }
        
        val xmlConverter = MappingJackson2XmlHttpMessageConverter(xmlMapper)
        val messageConverters = HttpMessageConverters(xmlConverter)
        
        return ResponseEntityDecoder(
            SpringDecoder(ObjectFactory { messageConverters })
        )
    }
}
