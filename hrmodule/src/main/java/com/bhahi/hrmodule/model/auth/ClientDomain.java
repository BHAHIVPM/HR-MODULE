package com.bhahi.hrmodule.model.auth;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "client_domain")
@Data
public class ClientDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "db_domain_id")
    private Integer domainId;

    @Column(name = "db_client_id", nullable = false, unique = true)
    private Integer clientId;

    @Column(name = "db_domain_url", nullable = false, unique = true, length = 255)
    private String domainUrl;

    @Column(name = "db_user_name", nullable = false, length = 100)
    private String userName;

    @Column(name = "db_database_password", nullable = false, length = 255)
    private String databasePassword;

    @Column(name = "db_provider", length = 20)
    private String provider;

    @Column(name = "db_domain_host_name", unique = true, length = 255)
    private String domainHostName;

    @Column(name = "db_domain_host_ip", unique = true, length = 50)
    private String domainHostIp;

    @Column(name = "db_aws_region", length = 100)
    private String awsRegion;

    @Column(name = "db_aws_bucket_name", length = 255)
    private String awsBucketName;

    @Column(name = "db_aws_access_key", length = 255)
    private String awsAccessKey;

    @Column(name = "db_aws_secret_key", length = 255)
    private String awsSecretKey;

    @Column(name = "db_entry_date", updatable = false)
    private LocalDateTime entryDate;

    @PrePersist
    protected void onCreate() {
        if (entryDate == null) {
            entryDate = LocalDateTime.now();
        }
    }
}
