package com.example.mobilnetestiranjebackend.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "verification_code_email")
public class VerificationEmailChange extends Verification{
    private String newEmail;
    private String oldEmail;
}
