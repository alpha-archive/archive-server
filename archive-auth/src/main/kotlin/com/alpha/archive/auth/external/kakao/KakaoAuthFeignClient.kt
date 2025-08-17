package com.alpha.archive.auth.external.kakao

import com.alpha.archive.auth.external.kakao.dto.KakaoTokenResponse
import com.alpha.archive.config.FeignConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "kakao-auth",
    url = "https://kauth.kakao.com",
    configuration = [FeignConfig::class]
)
interface KakaoAuthFeignClient {

    @PostMapping(
        value = ["/oauth/token"],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE]
    )
    fun getAccessToken(
        @RequestParam("grant_type") grantType: String,
        @RequestParam("client_id") clientId: String,
        @RequestParam("client_secret") clientSecret: String,
        @RequestParam("redirect_uri") redirectUri: String,
        @RequestParam("code") code: String
    ): KakaoTokenResponse
}
