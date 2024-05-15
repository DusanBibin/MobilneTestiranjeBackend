package com.example.mobilnetestiranjebackend.model;


import com.example.mobilnetestiranjebackend.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
@ToString
@Inheritance(strategy = InheritanceType.JOINED)
public class User implements UserDetails{
    @Id
    @GeneratedValue
    private Long id;
    private String firstName;
    private String lastname;
    private String email;
    private String password;
    private String phoneNumber;
    private String address;
    private Boolean emailConfirmed;
    private Boolean blocked;
    @OneToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    private Verification verification;
    @OneToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    private VerificationEmailChange emailChangeVerification;
    @Enumerated(EnumType.STRING)
    private Role role;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword(){
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() { return true; }
}
