package org.anta.service;


import lombok.RequiredArgsConstructor;
import org.anta.entity.Reservation;
import org.anta.repository.ReservationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationCleanupService {

    private final ReservationRepository reservationRepo;
    private final ReservationService reservationService;

    @Scheduled(fixedDelayString = "${app.reservation.cleanup-ms:60000}")
    @Transactional
    public void cleanupExpired() {
        List<Reservation> expired = reservationRepo.findByStatusAndExpiresAtBefore(
                "PENDING", LocalDateTime.now());
        for (Reservation r : expired) {
            reservationService.cancelReservation(r.getId());
            r.setStatus("EXPIRED");
            reservationRepo.save(r);
        }
    }
}

