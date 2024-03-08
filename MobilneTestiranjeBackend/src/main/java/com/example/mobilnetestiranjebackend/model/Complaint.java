package com.example.mobilnetestiranjebackend.model;


import com.example.mobilnetestiranjebackend.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "complaints")
@Inheritance(strategy = InheritanceType.JOINED)
public class Complaint {


    @Id
    @GeneratedValue
    private Long id;

    private String reason;

    private RequestStatus status;

    private String response;
}
