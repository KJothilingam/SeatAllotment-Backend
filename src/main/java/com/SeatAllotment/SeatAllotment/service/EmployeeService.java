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



//    public Employee addEmployee(Employee employee) {
//        // Check if the employee ID is provided
//        if (employee.getId() == null) {
//            throw new RuntimeException("Employee ID must be provided");
//        }
//
//        // Check if the ID already exists to prevent duplicate primary key errors
//        if (employeeRepository.existsById(employee.getId())) {
//            throw new RuntimeException("Employee ID already exists");
//        }
//
//
//        // Check if a seat is assigned
//        if (employee.getSeatId() != null && !"Unassigned".equalsIgnoreCase(employee.getSeatId())) {
//            Optional<Seat> seatOpt = seatRepository.findById(employee.getSeatId());
//
//            // Verify seat existence
//            if (seatOpt.isEmpty()) {
//                throw new RuntimeException("Seat does not exist.");
//            }
//
//            Seat seat = seatOpt.get();
//
//            if (seat.getStatus() != SeatStatus.VACANT) {
//                throw new SeatUnavailableException("Seat is not available. It is currently " + seat.getStatus());
//            }
//
//
//            // Assign the seat to the employee
//            seat.setStatus(OCCUPIED);
//            seat.setEmployeeId(employee.getId());
//            seatRepository.save(seat); // Update seat table
//        }
//
//        return employeeRepository.save(employee); // Save employee with seatId
//    }
public ResponseEntity<Map<String, Object>> addEmployee(Employee employee) {
    Map<String, Object> response = new HashMap<>();

    if (employee.getId() == null) {
        response.put("message", "❌ Employee ID must be provided");
        response.put("employee", new Employee()); // Empty object
        return ResponseEntity.badRequest().body(response);
    }

    if (employeeRepository.existsById(employee.getId())) {
        response.put("message", "❌ Employee ID already exists");
        response.put("employee", new Employee());
        return ResponseEntity.badRequest().body(response);
    }

    // Handling Work From Home condition
    if ("Work From Home".equalsIgnoreCase(employee.getSeatId())) {
        employee.setSeatId("Work From Home - " + employee.getId());
        response.put("message", "✅ Employee assigned to Work From Home");
    }
    // Handling seat assignment
    else if (employee.getSeatId() != null && !"Unassigned".equalsIgnoreCase(employee.getSeatId())) {
        Optional<Seat> seatOpt = seatRepository.findById(employee.getSeatId());

        if (seatOpt.isEmpty()) {
            response.put("message", "❌ Seat does not exist.");
            response.put("employee", new Employee());
            return ResponseEntity.badRequest().body(response);
        }

        Seat seat = seatOpt.get();

        if (seat.getStatus() != SeatStatus.VACANT) {
            employee.setSeatId("Unassigned");
            response.put("message", "❌ Seat already occupied! Assigned as Unassigned.");
        } else {
            seat.setStatus(SeatStatus.OCCUPIED);
            seat.setEmployeeId(employee.getId());
            seatRepository.save(seat);
            response.put("message", "✅ Employee assigned to seat " + employee.getSeatId());
        }
    } else {
        employee.setSeatId("Unassigned");
        response.put("message", "⚠️ No seat assigned, defaulting to Unassigned.");
    }

    Employee savedEmployee = employeeRepository.save(employee);
    response.put("employee", savedEmployee);

    return ResponseEntity.ok(response);
}

    public Employee updateEmployee(Long id, Employee updatedEmployee) {
        return employeeRepository.findById(id).map(emp -> {
            emp.setName(updatedEmployee.getName());
            emp.setDepartment(updatedEmployee.getDepartment());
            emp.setRole(updatedEmployee.getRole());
            emp.setSeatId(updatedEmployee.getSeatId()); // Allow "Unassigned"
            return employeeRepository.save(emp);

        }).orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }



}
