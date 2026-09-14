package com.bhahi.hrmodule.model.roleassignment;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "login_access_master")
@Data
public class LoginAccessMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "table_id")
    private int tableId;

    @Column(name = "db_login_id", length = 12, nullable = false, unique = true)
    private String loginId;

    @Column(name = "db_user_name")
    private String userName;

    @Column(name = "db_mobile_no", length = 15)
    private String mobileNo;

    @Column(name = "db_email_id")
    private String emailId;

    @Column(name = "db_user_type", length = 20)
    private String userType;
}