package com.SeatAllotment.SeatAllotment.Model;

import com.SeatAllotment.SeatAllotment.Enum.SeatStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "seats")
public class Seat {

    @Id
    @Column(unique = true, nullable = false)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status;

    @Column(name = "employee_id")
    private Long employeeId;

    public Seat() {}

    public Seat(String id, SeatStatus status, Long employeeId) {
        this.id = id;
        this.status = status;
        this.employeeId = employeeId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public SeatStatus getStatus() { return status; }
    public void setStatus(SeatStatus status) { this.status = status; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    @Override
    public String toString() {
        return "Seat{" +
                "id='" + id + '\'' +
                ", status=" + status +
                ", employeeId=" + (employeeId != null ? employeeId : "None") +
                '}';
    }
}