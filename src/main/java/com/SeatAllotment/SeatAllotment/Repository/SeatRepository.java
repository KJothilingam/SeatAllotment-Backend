package com.SeatAllotment.SeatAllotment.Repository;

import com.SeatAllotment.SeatAllotment.Enum.SeatStatus;
import com.SeatAllotment.SeatAllotment.Model.Seat;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat,String > {
    Optional<Seat> findById(String id);
    List<Seat> findByStatus(SeatStatus status);
    Optional<Seat> findByEmployeeId(Long employeeId);
}