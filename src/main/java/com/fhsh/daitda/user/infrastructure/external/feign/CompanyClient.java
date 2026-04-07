package com.fhsh.daitda.user.infrastructure.external.feign;

import com.fhsh.daitda.response.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "company-service")
public interface CompanyClient {
    @GetMapping("/internal/v1/companies/{companyId}")
    CommonResponse<Object> getCompanyById(@PathVariable("companyId") UUID companyId);
}
