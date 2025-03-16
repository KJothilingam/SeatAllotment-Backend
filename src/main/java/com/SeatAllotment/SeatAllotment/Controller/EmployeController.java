package com.SeatAllotment.SeatAllotment.Controller;

import com.SeatAllotment.SeatAllotment.Model.Employee;
import com.SeatAllotment.SeatAllotment.Model.Seat;
import com.SeatAllotment.SeatAllotment.service.EmployeeService;
import com.SeatAllotment.SeatAllotment.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/employees")
@CrossOrigin("*")
public class EmployeController{

    @Autowired
    private EmployeeService         employeeService;

    @Autowired
    private SeatService  seatService;

    @GetMapping("/list")
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);

        return employee.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-seat/{seatId}")
    public ResponseEntity<Employee> getEmployeeBySeat(@PathVariable String seatId) {
        Optional<Employee> employee = employeeService.getEmployeeBySeat(seatId);
        return employee.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }



    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> addEmployee(@RequestBody Map<String, Object> requestBody) {
        System.out.println("🔍 Raw Request Data: " + requestBody);

        Employee employee = new Employee();

        // Convert employeeid to Long (Fix Type Issue)
        try {
            employee.setId(Long.parseLong(requestBody.get("employeeid").toString()));
        } catch (NumberFormatException | NullPointerException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "❌ Invalid Employee ID format. It must be a number."));
        }

        // Set other fields
        employee.setName(requestBody.get("name").toString());
        employee.setDepartment(requestBody.get("department").toString());
        employee.setRole(requestBody.get("role").toString());

        // Fix Seat ID Issue
        String seatId = requestBody.get("seat_id") != null ? requestBody.get("seat_id").toString() : "Unassigned";
        employee.setSeatId(seatId);

        System.out.println("🔍 Processed Employee Object: " + employee.toString());

        return employeeService.addEmployee(employee);
    }



    @PutMapping("/update/{id}")
        public ResponseEntity<Map<String, Object>> updateEmployee(
                @PathVariable Long id, @RequestBody Employee updatedEmployee) {
            return employeeService.updateEmployee(id, updatedEmployee);
        }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);

        if (employee.isPresent()) {
            // Reset the seat if assigned
            seatService.resetSeatByEmployeeId(id);

            // Delete the employee
            employeeService.deleteEmployee(id);

            return ResponseEntity.ok(
                    Map.of("message", "Employee '" + employee.get().getName() + "' (ID: " + id + ") deleted successfully.")
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("error", "Employee Not Found", "message", "Employee with ID " + id + " not found.")
            );
        }
    }





}