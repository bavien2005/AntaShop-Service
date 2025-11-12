package org.anta.controller;

import lombok.RequiredArgsConstructor;
import org.anta.dto.request.CreateReservationRequest;
import org.anta.dto.response.CreateReservationResponse;
import org.anta.entity.Reservation;
import org.anta.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/create")
    public ResponseEntity<CreateReservationResponse> create(@Valid @RequestBody CreateReservationRequest req) {
        Reservation res = reservationService.createReservation(req);
        var dto = new CreateReservationResponse();
        dto.setReservationId(res.getId());
        dto.setStatus(res.getStatus());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/id/{id}/confirm")
    public ResponseEntity<?> confirm(@PathVariable("id") Long id) {
        reservationService.confirmReservation(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/id/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable("id") Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.ok().build();
    }
}

