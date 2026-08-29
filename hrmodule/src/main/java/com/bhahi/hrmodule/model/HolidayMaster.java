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

@Entity
@Table(name = "holiday_master")
@Data
public class HolidayMaster {

    public enum HolidayType {NATIONAL, FESTIVAL, OPTIONAL, RESTRICTED}

    public enum HolidayStatus {ACTIVE, INACTIVE}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int holidayId;

    @NotNull
    @Column(nullable = false)
    private String holidayName;

    @NotNull
    @Column(nullable = false)
    private LocalDate holidayDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 15, nullable = false)
    private HolidayType holidayType;

    // Scopes a holiday to a branch/location, e.g. "Chennai". "ALL" = applies everywhere.
    @Column(length = 50)
    private String location = "ALL";

    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private HolidayStatus status;
}
