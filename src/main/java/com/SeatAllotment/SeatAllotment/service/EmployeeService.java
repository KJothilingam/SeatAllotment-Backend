package com.SeatAllotment.SeatAllotment.service;

import com.SeatAllotment.SeatAllotment.Enum.SeatStatus;
import com.SeatAllotment.SeatAllotment.Model.Employee;
import com.SeatAllotment.SeatAllotment.Model.Seat;
import com.SeatAllotment.SeatAllotment.Repository.EmployeeRepository;
import com.SeatAllotment.SeatAllotment.Repository.SeatRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
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


    public Employee addEmployee(Employee employee) {
        // Check if the employee ID is provided
        if (employee.getId() == null) {
            throw new RuntimeException("Employee ID must be provided");
        }

        // Check if the ID already exists to prevent duplicate primary key errors
        if (employeeRepository.existsById(employee.getId())) {
            throw new RuntimeException("Employee ID already exists");
        }

        // Check if the seat is already assigned, but allow "Unassigned" or null
        if (employee.getSeatId() != null && !"Unassigned".equalsIgnoreCase(employee.getSeatId())) {
            if (employeeRepository.existsBySeatId(employee.getSeatId())) {
                throw new RuntimeException("Seat is already assigned to another employee.");
            }
        }

        return employeeRepository.save(employee);
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
