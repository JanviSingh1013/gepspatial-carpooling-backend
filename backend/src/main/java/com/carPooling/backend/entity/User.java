package com.carPooling.backend.entity;

import com.carPooling.backend.enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Table(name = "users",
        indexes = {
                @Index(name = "idx_user_phone", columnList = "phoneNumber"),
                @Index(name = "idx_user_email", columnList = "email")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // BASIC INFO
    @Column(length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(unique = true, length = 20)
    private String phoneNumber;

    private String profilePicture;

    private LocalDate dob;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    // AUTH
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    private boolean isPhoneVerified = false;

    // PROFILE EXTRA (optional but useful)
    private String collegeCompanyName;

    private String emergencyContactName;

    @Column(length = 20)
    private String emergencyContactNumber;

    private String city;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", gender=" + gender +
                ", city='" + city + '\'' +
                '}';
    }
}