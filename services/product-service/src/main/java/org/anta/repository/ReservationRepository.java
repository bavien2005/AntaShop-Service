package org.anta.repository;

import org.anta.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation , Long> {

    Optional<Reservation> findByRequestId(String requestId);

    List<Reservation> findByStatusAndExpiresAtBefore (String status , LocalDateTime time);

}
