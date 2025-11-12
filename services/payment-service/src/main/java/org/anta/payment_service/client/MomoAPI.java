package org.anta.payment_service.client;

import org.anta.payment_service.dto.request.CreateMomoRequest;
import org.anta.payment_service.dto.response.CreateMomoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// không cần vieet code rest template thủ công nữa khi dùng FeignClient
@FeignClient(name = "momo", url = "${momo.end-point}")
public interface MomoAPI {

    @PostMapping("/create")
    CreateMomoResponse createMomoQR(@RequestBody CreateMomoRequest request);

//    @GetMapping("/ipn-handler")
//    String handleIPN();
}
