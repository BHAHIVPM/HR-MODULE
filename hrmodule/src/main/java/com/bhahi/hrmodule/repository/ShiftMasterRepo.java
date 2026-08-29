package com.bhahi.hrmodule.repository;

import com.bhahi.hrmodule.model.ShiftMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftMasterRepo extends JpaRepository<ShiftMaster, Integer> {

    Optional<ShiftMaster> findByShiftCode(String shiftCode);

    List<ShiftMaster> findByStatus(ShiftMaster.ShiftStatus status);
}
