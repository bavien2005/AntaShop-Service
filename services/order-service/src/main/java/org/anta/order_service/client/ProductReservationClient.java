package org.anta.order_service.client;

import org.anta.order_service.dto.external.CreateReservationRequest;
import org.anta.order_service.dto.external.CreateReservationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="product-reservation", url="${product.reservation-base}")
public interface ProductReservationClient {

    @PostMapping("/create")
    CreateReservationResponse create(@RequestBody CreateReservationRequest req);

    @PostMapping("/id/{id}/confirm")
    void confirm(@PathVariable("id") Long reservationId);

    @PostMapping("/id/{id}/cancel")
    void cancel(@PathVariable("id") Long reservationId);

}