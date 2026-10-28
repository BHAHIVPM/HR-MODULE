package com.bhahi.hrmodule.model.auth;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "client_master")
@Data
public class ClientMaster {

    @Id
    @Column(name = "db_client_id", nullable = false)
    private Integer clientId;

    @Column(name = "db_ledger_account_id", nullable = false, length = 20)
    private String ledgerAccountId;

    @Column(name = "db_organization_name", nullable = false, unique = true, length = 100)
    private String organizationName;

    @Column(name = "db_trade_name", nullable = false, length = 50)
    private String tradeName;

    @Column(name = "db_client_code", nullable = false, unique = true, length = 10)
    private String clientCode;

    @Column(name = "db_industry", nullable = false)
    private Integer industry;

    @Column(name = "db_region", nullable = false)
    private Integer region;

    @Column(name = "db_operation_type")
    private Integer operationType;

    @Column(name = "db_lead_id")
    private Integer leadId;

    @Column(name = "db_registration_no", nullable = false, length = 20)
    private String registrationNo;

    @Column(name = "db_date_of_incorporation", nullable = false)
    private LocalDate dateOfIncorporation;

    @Column(name = "db_business_entity_types", nullable = false, length = 50)
    private String businessEntityType;

    @Column(name = "db_pan_number", length = 20)
    private String panNumber;

    @Column(name = "db_gst_number", unique = true, length = 20)
    private String gstNumber;

    @Column(name = "db_tan_number", length = 20)
    private String tanNumber;

    @Column(name = "db_contact_person_name", nullable = false, length = 100)
    private String contactPersonName;

    @Column(name = "db_designation", length = 100)
    private String designation;

    @Column(name = "db_primary_contact_number", nullable = false, length = 10)
    private String primaryContactNumber;

    @Column(name = "db_alternate_contact_number", length = 10)
    private String alternateContactNumber;

    @Column(name = "db_email_address", nullable = false, length = 30)
    private String emailAddress;

    @Column(name = "db_roa_address_line_1", nullable = false, length = 255)
    private String roaAddressLine1;

    @Column(name = "db_roa_address_line_2", length = 255)
    private String roaAddressLine2;

    @Column(name = "db_roa_city", nullable = false, length = 100)
    private String roaCity;

    @Column(name = "db_roa_state", nullable = false)
    private Integer roaState;

    @Column(name = "db_roa_pin_code", nullable = false, length = 10)
    private String roaPinCode;

    @Column(name = "db_same_as_roa", nullable = false)
    private boolean sameAsRoa = false;

    @Column(name = "db_ca_address_line_1", length = 255)
    private String caAddressLine1;

    @Column(name = "db_ca_address_line_2", length = 255)
    private String caAddressLine2;

    @Column(name = "db_ca_city", length = 100)
    private String caCity;

    @Column(name = "db_ca_state")
    private Integer caState;

    @Column(name = "db_ca_pin_code", length = 10)
    private String caPinCode;

    @Column(name = "db_last_financial_year", length = 5)
    private String lastFinancialYear;

    @Column(name = "db_created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "db_updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "db_entry_date", updatable = false)
    private LocalDateTime entryDate;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (entryDate == null) entryDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
