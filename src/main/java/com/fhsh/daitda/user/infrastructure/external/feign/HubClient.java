package com.fhsh.daitda.user.infrastructure.external.feign;

import com.fhsh.daitda.response.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "hub-service")
public interface HubClient {
    @GetMapping("/internal/v1/hubs/{hubId}")
    CommonResponse<Object> getHubById(@PathVariable("hubId") UUID hubId);
}
