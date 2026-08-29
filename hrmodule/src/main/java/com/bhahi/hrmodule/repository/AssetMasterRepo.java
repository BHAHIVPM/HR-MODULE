package com.bhahi.hrmodule.repository;

import com.bhahi.hrmodule.model.AssetMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetMasterRepo extends JpaRepository<AssetMaster, Integer> {

    Optional<AssetMaster> findByAssetCode(String assetCode);

    List<AssetMaster> findByIssuedToEmployeeId(Integer employeeId);

    List<AssetMaster> findByStatus(AssetMaster.AssetStatus status);
}
