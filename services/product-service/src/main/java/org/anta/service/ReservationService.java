package org.anta.service;


import lombok.RequiredArgsConstructor;
import org.anta.dto.request.CreateReservationRequest;
import org.anta.entity.Reservation;
import org.anta.entity.ReservationItem;
import org.anta.exception.ReservationException;
import org.anta.repository.ProductVariantRepository;
import org.anta.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepo;
    private final ProductVariantRepository variantRepo;

    // 15p
    private final int DEFAULT_TTL_SECONDS = 15 * 60;

    @Transactional
    public Reservation createReservation(CreateReservationRequest req) {
        String requestId = req.getRequestId();
        if (requestId != null) {
            var existing = reservationRepo.findByRequestId(requestId);
            if (existing.isPresent()) {
                return existing.get(); // idempotent
            }
        }

        int ttl = req.getTtlSeconds() != null ? req.getTtlSeconds() : DEFAULT_TTL_SECONDS;
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(ttl);

        Reservation res = Reservation.builder()
                .requestId(requestId)
                .status("PENDING")
                .expiresAt(expiresAt)
                .build();

        List<ReservationItem> items = new ArrayList<>();
        for (CreateReservationRequest.ReservationLine line : req.getItems()) {
            int updated = variantRepo.reduceStockIfAvailable(line.getVariantId(), line.getQuantity());
            if (updated == 0) {
                // rollback
                throw new ReservationException("Not enough stock for variant " + line.getVariantId());
            }
            ReservationItem ri = ReservationItem.builder()
                    .reservation(res)
                    .variantId(line.getVariantId())
                    .quantity(line.getQuantity())
                    .build();
            items.add(ri);
        }
        res.setItems(items);
        Reservation saved = reservationRepo.save(res);
        return saved;
    }

    @Transactional
    public void confirmReservation(Long reservationId) {
        Reservation res = reservationRepo.findById(reservationId).orElseThrow(()
                -> new ReservationException("Reservation not found"));
        if (!"PENDING".equals(res.getStatus())) {
            throw new ReservationException("Cannot confirm reservation in state: " + res.getStatus());
        }
        res.setStatus("CONFIRMED");
        reservationRepo.save(res);
    }

    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation res = reservationRepo.findById(reservationId).orElseThrow(()
                -> new ReservationException("Reservation not found"));
        if ("CANCELLED".equals(res.getStatus()) || "EXPIRED".equals(res.getStatus())){
            return;
        }
        // release stock
        res.getItems().forEach(item -> variantRepo.increaseStock(item.getVariantId(), item.getQuantity()));
        res.setStatus("CANCELLED");
        reservationRepo.save(res);
    }
}
