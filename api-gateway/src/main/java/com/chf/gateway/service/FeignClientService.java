package com.chf.gateway.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "service")
public interface FeignClientService {

    @GetMapping("/openapi/hello")
    String hello();

}
