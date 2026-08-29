package com.bhahi.hrmodule.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Employee document records (ID proof, offer letter, PAN, etc). Maps 1-to-many with
// employee_master via employeeId. filePath stores wherever the actual file was uploaded to
// (disk path / S3 key / URL) - this table only tracks metadata, not the file bytes.
@Entity
@Table(name = "document_master")
@Data
public class DocumentMaster {

    public enum DocumentType {
        ID_PROOF, ADDRESS_PROOF, OFFER_LETTER, APPOINTMENT_LETTER, PAN_CARD,
        AADHAR_CARD, PASSPORT, RESUME, EDUCATIONAL_CERTIFICATE, RELIEVING_LETTER, OTHER
    }

    public enum DocumentStatus {ACTIVE, INACTIVE}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int documentId;

    // References employee_master.employeeId.
    @NotNull
    @Column(nullable = false)
    private Integer employeeId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 25, nullable = false)
    private DocumentType documentType;

    @NotNull
    @Column(nullable = false)
    private String documentName;

    private String documentNumber;

    private String filePath;

    private LocalDate issuedDate;

    private LocalDate expiryDate;

    private boolean verified;

    private LocalDateTime uploadedOn;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private DocumentStatus status;
}
