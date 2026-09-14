package com.bhahi.hrmodule.repository.ui;

import com.bhahi.hrmodule.model.ui.UiThemeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UiThemeMasterRepo extends JpaRepository<UiThemeMaster, Integer> {
}