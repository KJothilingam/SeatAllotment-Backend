package com.SeatAllotment.SeatAllotment.Repository;

import com.SeatAllotment.SeatAllotment.Model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findBySeatId(String seatId);

    boolean existsBySeatId(String seatId);
}