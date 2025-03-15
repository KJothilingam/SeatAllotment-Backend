package com.SeatAllotment.SeatAllotment.Controller;

import com.SeatAllotment.SeatAllotment.Model.Seat;
import com.SeatAllotment.SeatAllotment.Model.Employee;
import com.SeatAllotment.SeatAllotment.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/seats")
@CrossOrigin("*")
public class SeatController {

    @Autowired
    private SeatService seatService;

    /**
     * Fetch all seats
     */
    @GetMapping
    public ResponseEntity<List<Seat>> getAllSeats() {
        List<Seat> seats = seatService.getAllSeats();
        return ResponseEntity.ok(seats);
    }

    @GetMapping("/{seatId}/employee-details")
    public ResponseEntity<?> getEmployeeBySeat(@PathVariable String seatId) {
        Optional<Seat> seatOptional = seatService.getSeatById(seatId);

        if (seatOptional.isPresent()) {
            Seat seat = seatOptional.get();
            Optional<Employee> employeeOptional = seatService.getEmployeeBySeat(seatId);

            if (employeeOptional.isPresent()) {
                Employee employee = employeeOptional.get();

                // ✅ Return proper JSON response
                return ResponseEntity.ok().body(new Object() {
                    public final String seatId = seat.getId();
                    public final String status = seat.getStatus().toString();
                    public final String employeeName = employee.getName();
                    public final String role = employee.getRole();
                    public final String department = employee.getDepartment();
                });
            }

            // ✅ Return JSON when seat is vacant
            return ResponseEntity.ok().body(new Object() {
                public final String seatId = seat.getId();
                public final String status = "VACANT";
                public final String message = "Seat is vacant";
            });
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Object() {
            public final String message = "Seat not found";
        });
    }


//    @GetMapping("/{seatId}/employee-details")
//    public ResponseEntity<?> getEmployeeBySeat(@PathVariable String seatId) {
//        Optional<Seat> seatOptional = seatService.getSeatById(seatId);
//
//        if (seatOptional.isPresent()) {
//            Seat seat = seatOptional.get();
//            Optional<Employee> employeeOptional = seatService.getEmployeeBySeat(seatId);
//
//            if (employeeOptional.isPresent()) {
//                Employee employee = employeeOptional.get();
//
//                // Send only necessary details
//                return ResponseEntity.ok().body(new Object() {
//                    public final String seatId = seat.getId();
//                    public final String status = seat.getStatus().toString();
//                    public final String employeeName = employee.getName();
//                    public final String role = employee.getRole();
//                    public final String department = employee.getDepartment();
//                });
//            }
//            return ResponseEntity.ok().body("Seat is vacant");
//        }
//
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Seat not found");
//    }




}