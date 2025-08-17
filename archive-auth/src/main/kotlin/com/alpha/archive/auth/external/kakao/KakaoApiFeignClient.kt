package com.alpha.archive.auth.external.kakao

import com.alpha.archive.auth.external.kakao.dto.KakaoUserInfoResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(
    name = "kakao-api",
    url = "https://kapi.kakao.com",
    configuration = [com.alpha.archive.config.FeignConfig::class]
)
interface KakaoApiFeignClient {

    @GetMapping(
        value = ["/v2/user/me"],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE]
    )
    fun getUserInfo(
        @RequestHeader("Authorization") authorization: String
    ): KakaoUserInfoResponse
}
