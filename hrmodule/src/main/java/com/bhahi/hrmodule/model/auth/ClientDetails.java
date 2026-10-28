package com.bhahi.hrmodule.model.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDetails {
    private Integer clientId;
    private String organizationName;
    private String tradeName;
    private String clientCode;
    private String businessEntityType;
    private String contactPersonName;
    private String mobileNumber;      // maps to db_primary_contact_number
    private String emailAddress;
    private String domainUrl;
    private String databaseUserName;  // maps to db_user_name
    private String databasePassword;  // maps to db_database_password
    private String domainHostName;
    private String domainHostIp;
    private LocalDateTime entryDate;
}
