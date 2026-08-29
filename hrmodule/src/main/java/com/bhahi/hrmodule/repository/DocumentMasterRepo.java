package com.bhahi.hrmodule.repository;

import com.bhahi.hrmodule.model.DocumentMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentMasterRepo extends JpaRepository<DocumentMaster, Integer> {

    List<DocumentMaster> findByEmployeeId(Integer employeeId);

    List<DocumentMaster> findByEmployeeIdAndDocumentType(Integer employeeId, DocumentMaster.DocumentType documentType);
}
