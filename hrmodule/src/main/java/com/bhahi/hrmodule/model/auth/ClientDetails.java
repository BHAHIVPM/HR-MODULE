package com.bhahi.hrmodule.model.auth;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "client_details")
@Data
public class ClientDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", unique = true, nullable = false)
    private String clientId;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Column(name = "url")
    private String url;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "status")
    private String status;
}