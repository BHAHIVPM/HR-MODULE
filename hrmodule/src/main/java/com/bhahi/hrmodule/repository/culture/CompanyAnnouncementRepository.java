package com.bhahi.hrmodule.repository.culture;

import com.bhahi.hrmodule.model.culture.CompanyAnnouncement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyAnnouncementRepository extends JpaRepository<CompanyAnnouncement, Integer> {
    List<CompanyAnnouncement> findByTargetDepartment(String targetDepartment);
}
