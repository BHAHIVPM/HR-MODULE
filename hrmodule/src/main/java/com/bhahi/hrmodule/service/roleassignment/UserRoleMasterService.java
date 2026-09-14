package com.bhahi.hrmodule.service.roleassignment;

import com.bhahi.hrmodule.model.roleassignment.UserRoleMaster;
import com.bhahi.hrmodule.repository.roleassignment.UserRoleMasterRepo;
import com.bhahi.hrmodule.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRoleMasterService {

    private final UserRoleMasterRepo userRoleMasterRepo;

    private String getUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private String getUserCategory(String loginId) {
        String code = loginId.substring(4, 6);
        return switch (code) {
            case "10" -> null;
            case "11" -> "ADMIN";
            case "12" -> "USER";
            default -> throw new RuntimeException("Forbidden: You don't have permission to access this data.");
        };
    }

    public ResponseMessage<UserRoleMaster> save(UserRoleMaster userRoleMaster) {
        ResponseMessage<UserRoleMaster> response = new ResponseMessage<>();
        try {
            UserRoleMaster saved = userRoleMasterRepo.save(userRoleMaster);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("The role has been saved successfully.");
            response.setStatusCode(200);
            return response;
        } catch (DataIntegrityViolationException e) {
            response.setHeader("Duplicate entry");
            response.setMessage("A role with this name already exists.");
            response.setStatusCode(409);
            return response;
        } catch (Exception e) {
            response.setHeader("Save failed");
            response.setMessage("Could not save the role. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<UserRoleMaster>> findAll() {
        ResponseMessage<List<UserRoleMaster>> response = new ResponseMessage<>();
        try {
            String userId = getUserId();
            String category = getUserCategory(userId);
            List<UserRoleMaster> roles;
            if (category == null) {
                roles = userRoleMasterRepo.findAll();
            } else {
                roles = userRoleMasterRepo.findByRoleCategory(category);
                List<UserRoleMaster> nullCategoryRoles = userRoleMasterRepo.findByRoleCategoryIsNull();
                roles.addAll(nullCategoryRoles);
            }
            response.setResponseOutput(roles);
            response.setHeader("Success");
            response.setMessage("Roles fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch roles. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<UserRoleMaster>> findAllWithDate() {
        ResponseMessage<List<UserRoleMaster>> response = new ResponseMessage<>();
        try {
            List<UserRoleMaster> roles = userRoleMasterRepo.findAll();
            response.setResponseOutput(roles);
            response.setHeader("Success");
            response.setMessage("Roles fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch roles. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<UserRoleMaster> findById(int roleId) {
        ResponseMessage<UserRoleMaster> response = new ResponseMessage<>();
        try {
            Optional<UserRoleMaster> role = userRoleMasterRepo.findById(roleId);
            if (role.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No role found for this id.");
                response.setStatusCode(404);
                return response;
            }
            response.setResponseOutput(role.get());
            response.setHeader("Success");
            response.setMessage("Role fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch the role. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<UserRoleMaster> update(int roleId, UserRoleMaster updates) {
        ResponseMessage<UserRoleMaster> response = new ResponseMessage<>();
        try {
            Optional<UserRoleMaster> existingOpt = userRoleMasterRepo.findById(roleId);
            if (existingOpt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No role found for this id.");
                response.setStatusCode(404);
                return response;
            }
            UserRoleMaster existing = existingOpt.get();
            if (!existing.isEditable()) {
                response.setHeader("Forbidden");
                response.setMessage("This role cannot be edited after it has been assigned.");
                response.setStatusCode(403);
                return response;
            }
            existing.setRoleName(updates.getRoleName());
            existing.setRemarks(updates.getRemarks());
            existing.setSystem(updates.isSystem());
            existing.setRoleCategory(updates.getRoleCategory());
            UserRoleMaster saved = userRoleMasterRepo.save(existing);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Role updated successfully.");
            response.setStatusCode(200);
            return response;
        } catch (DataIntegrityViolationException e) {
            response.setHeader("Duplicate entry");
            response.setMessage("A role with this name already exists.");
            response.setStatusCode(409);
            return response;
        } catch (Exception e) {
            response.setHeader("Update failed");
            response.setMessage("Could not update the role. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<String> delete(int roleId) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            Optional<UserRoleMaster> existingOpt = userRoleMasterRepo.findById(roleId);
            if (existingOpt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No role found for this id.");
                response.setStatusCode(404);
                return response;
            }
            UserRoleMaster existing = existingOpt.get();
            if (!existing.isEditable()) {
                response.setHeader("Forbidden");
                response.setMessage("The role is already in use and cannot be removed.");
                response.setStatusCode(403);
                return response;
            }
            userRoleMasterRepo.deleteById(roleId);
            response.setResponseOutput("Deleted.");
            response.setHeader("Success");
            response.setMessage("Role deleted successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Delete failed");
            response.setMessage("Could not delete the role. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public void setEditableFlag(int roleId, boolean editable) {
        userRoleMasterRepo.findById(roleId).ifPresent(role -> {
            role.setEditable(editable);
            userRoleMasterRepo.save(role);
        });
    }

    public void setAssignedFlag(int roleId, boolean assigned) {
        userRoleMasterRepo.findById(roleId).ifPresent(role -> {
            role.setAssignment(assigned);
            userRoleMasterRepo.save(role);
        });
    }
}
