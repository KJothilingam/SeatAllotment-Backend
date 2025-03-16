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

//    @PostMapping("/create")
//    public ResponseEntity<Employee> addEmployee(@RequestBody Employee employee) {
//        Employee newEmployee = employeeService.addEmployee(employee);
//        return ResponseEntity.ok(newEmployee);
//    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> addEmployee(@RequestBody Employee employee) {
        return employeeService.addEmployee(employee);
    }



    @PutMapping("/update/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        try {
            Employee updatedEmployee = employeeService.updateEmployee(id, employee);
            return ResponseEntity.ok(updatedEmployee);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
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


//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
//        Optional<Employee> employee = employeeService.getEmployeeById(id);
//        if (employee.isPresent()) {
//            employeeService.deleteEmployee(id);
//            return ResponseEntity.ok(
//                    Map.of("message", "Employee '" + employee.get().getName() + "' (ID: " + id + ") deleted successfully.")
//            );
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                    Map.of("error", "Employee Not Found", "message", "Employee with ID " + id + " not found.")
//            );
//        }
//    }





}