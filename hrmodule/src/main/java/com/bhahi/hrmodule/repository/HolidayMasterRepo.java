package com.bhahi.hrmodule.repository;

import com.bhahi.hrmodule.model.HolidayMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HolidayMasterRepo extends JpaRepository<HolidayMaster, Integer> {

    List<HolidayMaster> findByStatus(HolidayMaster.HolidayStatus status);

    List<HolidayMaster> findByHolidayDateBetween(LocalDate from, LocalDate to);

    List<HolidayMaster> findByLocationAndHolidayDateBetween(String location, LocalDate from, LocalDate to);
}
