package com.SeatAllotment.SeatAllotment.service;

import com.SeatAllotment.SeatAllotment.Model.Employee;
import com.SeatAllotment.SeatAllotment.Model.Seat;
import com.SeatAllotment.SeatAllotment.Repository.EmployeeRepository;
import com.SeatAllotment.SeatAllotment.Repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.SeatAllotment.SeatAllotment.Enum.SeatStatus.VACANT;

@Service
public class SeatService {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Fetch all seats
     */
    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }

    /**
     * Fetch seat details by ID
     */
    public Optional<Seat> getSeatById(String id) {
        return seatRepository.findById(id);
    }

    /**
     * Fetch Employee details by Seat ID
     */

    public Optional<Employee>   getEmployeeBySeat(String seatId) {
        Optional<Seat> seatOptional = seatRepository.findById(seatId);

        if (seatOptional.isPresent()) {
            Seat seat = seatOptional.get();
            if (seat.getEmployeeId() != null) {
                return employeeRepository.findById(seat.getEmployeeId());
            }
        }

        return Optional.empty();
    }


}
