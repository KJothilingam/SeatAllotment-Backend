package com.SeatAllotment.SeatAllotment.service;

import com.SeatAllotment.SeatAllotment.Enum.SeatStatus;
import com.SeatAllotment.SeatAllotment.Model.Employee;
import com.SeatAllotment.SeatAllotment.Model.Seat;
import com.SeatAllotment.SeatAllotment.Repository.EmployeeRepository;
import com.SeatAllotment.SeatAllotment.Repository.SeatRepository;
import com.SeatAllotment.SeatAllotment.exception.SeatUnavailableException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.SeatAllotment.SeatAllotment.Enum.SeatStatus.OCCUPIED;
import static com.SeatAllotment.SeatAllotment.Enum.SeatStatus.VACANT;

@Service
public class EmployeeService {


    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private SeatRepository seatRepository;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(Long id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        employee.ifPresent(emp -> System.out.println("Employee Data: " + emp));
        return employee;
    }

    public Optional<Employee> getEmployeeBySeat(String seatId) {
        return employeeRepository.findBySeatId(seatId);
    }

    public void resetSeatByEmployeeId(Long employeeId) {
    Optional<Seat> seat = seatRepository.findByEmployeeId(employeeId);
    if (seat.isPresent()) {
        Seat updatedSeat = seat.get();
        updatedSeat.setEmployeeId(null);
        updatedSeat.setStatus(SeatStatus.VACANT);
        seatRepository.save(updatedSeat);
    }
}

    public ResponseEntity<Map<String, Object>> updateEmployee(Long id, Employee updatedEmployee) {
        Map<String, Object> response = new HashMap<>();

        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Employee not found"));

        String newSeatId = updatedEmployee.getSeatId();
        String currentSeatId = existingEmployee.getSeatId();

        if (!newSeatId.equalsIgnoreCase(currentSeatId)) {
            if (newSeatId.equalsIgnoreCase("Work From Home") || newSeatId.equalsIgnoreCase("Unassigned")) {
                resetSeatByEmployeeId(existingEmployee.getEmployeeid());
                existingEmployee.setSeatId(newSeatId);
            } else {
                Seat newSeat = seatRepository.findById(newSeatId)
                        .orElseThrow(() -> new RuntimeException("❌ New seat not found"));

                if (newSeat.getStatus() == SeatStatus.OCCUPIED) {
                    response.put("message", "❌ Seat " + newSeatId + " is already occupied!");
                    return ResponseEntity.badRequest().body(response);
                }

                resetSeatByEmployeeId(existingEmployee.getEmployeeid());
                newSeat.setStatus(SeatStatus.OCCUPIED);
                newSeat.setEmployeeId(id);
                seatRepository.save(newSeat);
                existingEmployee.setSeatId(newSeatId);
            }
        }

        existingEmployee.setName(updatedEmployee.getName());
        existingEmployee.setRole(updatedEmployee.getRole());
        existingEmployee.setDepartment(updatedEmployee.getDepartment());

        Employee savedEmployee = employeeRepository.save(existingEmployee);
        response.put("message", "✅ Employee updated successfully");
        response.put("employee", savedEmployee);

        return ResponseEntity.ok(response);
    }


    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }


    public ResponseEntity<Map<String, Object>> addEmployee(Employee employee) {
        Map<String, Object> response = new HashMap<>();

        // Validate Employee ID
        if (employee.getId() == null) {
            response.put("message", "❌ Employee ID must be provided");
            return ResponseEntity.badRequest().body(response);
        }

        System.out.println("🔍 Seat ID Received: " + employee.getSeatId());

        // ✅ Allow "Work From Home" and "Unassigned" without checking the database
        if ("Work From Home".equalsIgnoreCase(employee.getSeatId()) || "Unassigned".equalsIgnoreCase(employee.getSeatId())) {
            response.put("message", "✅ Employee assigned to " + employee.getSeatId());
            return saveEmployee(employee, response);
        }

        // Validate manual seat assignment (Only check real seats)
        Optional<Seat> seatOpt = seatRepository.findById(employee.getSeatId());

        if (seatOpt.isPresent()) {
            Seat seat = seatOpt.get();

            if (seat.getStatus() != SeatStatus.VACANT) {
                response.put("message", "❌ Seat " + seat.getId() + " is already occupied! Please select a different seat.");
                return ResponseEntity.badRequest().body(response);
            }

            // Assign seat and update status
            seat.setStatus(SeatStatus.OCCUPIED);
            seat.setEmployeeId(employee.getId());
            seatRepository.save(seat); // Update seat table

            employee.setSeatId(String.valueOf(seat.getId())); // Ensure seat ID is stored correctly
            response.put("message", "✅ Employee assigned to seat " + seat.getId());
        } else {
            response.put("message", "❌ Seat does not exist.");
            return ResponseEntity.badRequest().body(response);
        }

        return saveEmployee(employee, response);
    }

    private ResponseEntity<Map<String, Object>> saveEmployee(Employee employee, Map<String, Object> response) {
        Employee savedEmployee = employeeRepository.save(employee);
        response.put("employee", savedEmployee);
        return ResponseEntity.ok(response);
    }

}
