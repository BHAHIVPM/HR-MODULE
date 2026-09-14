package com.bhahi.hrmodule.model.asset;

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

// Company assets (laptop, mobile, ID card...) and who they're issued to. issuedToEmployeeId
// is nullable - null means the asset is unassigned/in stock, referencing
// employee_master.employeeId once it's checked out.
@Entity
@Table(name = "asset_master")
@Data
public class AssetMaster {

    public enum AssetType {LAPTOP, DESKTOP, MOBILE, MONITOR, ID_CARD, ACCESSORY, FURNITURE, OTHER}

    public enum AssetCondition {NEW, GOOD, DAMAGED, LOST}

    public enum AssetStatus {AVAILABLE, ISSUED, RETURNED, DAMAGED, LOST, RETIRED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int assetId;

    @NotNull
    @Column(length = 20, nullable = false, unique = true)
    private String assetCode;

    @NotNull
    @Column(nullable = false)
    private String assetName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 15, nullable = false)
    private AssetType assetType;

    @Column(length = 50)
    private String serialNumber;

    // References employee_master.employeeId. Null while the asset is unassigned/in stock.
    private Integer issuedToEmployeeId;

    private LocalDate issuedDate;

    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private AssetCondition assetCondition;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private AssetStatus status;

    private String remarks;
}
