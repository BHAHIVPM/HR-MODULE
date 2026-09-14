package com.bhahi.hrmodule.service.menu;

import com.bhahi.hrmodule.model.menu.DesktopMenuSubGroupMasterOperation;
import com.bhahi.hrmodule.repository.menu.DesktopSubMenuOperationRepository;
import com.bhahi.hrmodule.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class DesktopSubMenuOperationServiceImpl implements DesktopSubMenuOperationService {

    private final DesktopSubMenuOperationRepository subMenuRepo;

    private static final Logger logger = LoggerFactory.getLogger(DesktopSubMenuOperationServiceImpl.class);
    private static final String LOG_MESSAGE = "Exception logging: ";
    private static final String SUCCESS_MSG = "Success.";
    private static final String ERROR_NO_DATA = "No Data Exist.";
    private static final Pattern NAME_VALIDATION_PATTERN = Pattern.compile("^[a-zA-Z\\s]+$");

    private void validateSubGroupName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Sub Menu Name cannot be empty. Please provide a valid name.");
        }
        if (!NAME_VALIDATION_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("Sub Menu Name can only contain letters and spaces. Please correct it and try again.");
        }
    }

    @Override
    @Transactional
    public ResponseMessage<String> addSubMenu(DesktopMenuSubGroupMasterOperation subMenu) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            String subGroupName = subMenu.getSubGroupName().trim();
            validateSubGroupName(subGroupName);
            if (subMenu.getSubGroupName() == null || subMenu.getSubGroupName().isBlank()) {
                response.setHeader("Invalid Input");
                response.setMessage("Sub Menu Name cannot be empty. Please provide a valid name.");
                response.setStatusCode(400);
                return response;
            }
            if (subMenuRepo.existsBySubGroupNameIgnoreCase(subGroupName)) {
                response.setHeader("Duplicate Entry");
                response.setMessage("A sub menu with this name already exists. Please choose a different name.");
                response.setStatusCode(409);
                return response;
            }
            Integer maxId = subMenuRepo.findMaxId();
            int newId = (maxId != null ? maxId : 100) + 1;
            if (newId > 999) {
                response.setHeader("Limit Reached");
                response.setMessage("The maximum number of Sub Menus has been reached. You cannot add more.");
                response.setStatusCode(400);
                return response;
            }
            subMenu.setSubGroupId(newId);
            subMenu.setSubGroupName(subGroupName);
            subMenuRepo.save(subMenu);
            response.setHeader(SUCCESS_MSG);
            response.setMessage("Sub Menu added successfully");
            response.setStatusCode(200);
            return response;
        } catch (IllegalArgumentException e) {
            response.setHeader("Invalid Input");
            response.setMessage(e.getMessage());
            response.setStatusCode(400);
            return response;
        } catch (Exception e) {
            logger.error(LOG_MESSAGE, e);
            response.setHeader("Error");
            response.setMessage("An unexpected error occurred.");
            response.setStatusCode(500);
            return response;
        }
    }

    @Override
    @Transactional
    public ResponseMessage<List<DesktopMenuSubGroupMasterOperation>> getAllSubMenu() {
        ResponseMessage<List<DesktopMenuSubGroupMasterOperation>> response = new ResponseMessage<>();
        try {
            List<DesktopMenuSubGroupMasterOperation> list = subMenuRepo.findAll();
            if (list == null || list.isEmpty()) {
                response.setHeader("No Data Found");
                response.setMessage("No sub groups found. Please add a sub group first.");
                response.setStatusCode(200);
                response.setResponseOutput(List.of());
                return response;
            }
            response.setHeader(SUCCESS_MSG);
            response.setMessage("Sub groups fetched successfully. Found " + list.size() + " sub group(s).");
            response.setStatusCode(200);
            response.setResponseOutput(list);
            return response;
        } catch (Exception exception) {
            logger.error(LOG_MESSAGE, exception);
            response.setHeader("Error");
            response.setMessage("An unexpected error occurred while fetching sub groups.");
            response.setStatusCode(500);
            return response;
        }
    }

    @Override
    @Transactional
    public ResponseMessage<DesktopMenuSubGroupMasterOperation> getById(int subMenuId) {
        ResponseMessage<DesktopMenuSubGroupMasterOperation> response = new ResponseMessage<>();
        try {
            Optional<DesktopMenuSubGroupMasterOperation> optional = subMenuRepo.findById(subMenuId);
            if (optional.isPresent()) {
                response.setHeader(SUCCESS_MSG);
                response.setMessage("Sub Menu found.");
                response.setStatusCode(200);
                response.setResponseOutput(optional.get());
            } else {
                response.setHeader(ERROR_NO_DATA);
                response.setMessage("The requested sub menu could not be found. Please check your selection and try again.");
                response.setStatusCode(404);
            }
            return response;
        } catch (Exception e) {
            logger.error(LOG_MESSAGE, e);
            response.setHeader("Error");
            response.setMessage("An unexpected error occurred.");
            response.setStatusCode(500);
            return response;
        }
    }
}
