package com.bhahi.hrmodule.model.ui;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "ui_theme_master")
@Data
public class UiThemeMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int themeDocumentId;

    @Column(length = 50)
    private String primaryColor;

    @Column(length = 50)
    private String secondaryColor;

    @Column(length = 50)
    private String thirdColor;
}