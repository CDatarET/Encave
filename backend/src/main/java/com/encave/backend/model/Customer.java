package com.encave.backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Customer")
public class Customer {

    @Id
    @Column(name = "prn")
    private int prn;

    @Column(name = "firstName", nullable = false, length = 20)
    private String firstName;

    @Column(name = "lastName", nullable = false, length = 20)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phone", nullable = false, unique = true)
    private int phone;

    @Column(name = "password", nullable = false, length = 20)
    private String password;

    @Column(name = "creation", insertable = false, updatable = false)
    private LocalDateTime creation;

    public Customer() {}

    public Customer(int prn, String firstName, String lastName, String email, int phone, String password){
        this.prn = prn;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }
}