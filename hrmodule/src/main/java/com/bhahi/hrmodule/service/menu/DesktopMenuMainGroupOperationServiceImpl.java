package com.bhahi.hrmodule.service.menu;

import com.bhahi.hrmodule.dto.menu.DesktopMenuMainGroupOperationDto;
import com.bhahi.hrmodule.dto.menu.MainGroupOperationOrderDto;
import com.bhahi.hrmodule.model.menu.DesktopMenuMainGroupMasterOperation;
import com.bhahi.hrmodule.repository.menu.DesktopMenuMainGroupOperationRepository;
import com.bhahi.hrmodule.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class DesktopMenuMainGroupOperationServiceImpl implements DesktopMenuMainGroupOperationService {

    private final DesktopMenuMainGroupOperationRepository menuMainGroupRepo;

    private static final Logger logger = LoggerFactory.getLogger(DesktopMenuMainGroupOperationServiceImpl.class);
    private static final String LOG_MESSAGE = "Exception logging: ";
    private static final String SUCCESS_MSG = "Success.";
    private static final String ERROR_NO_DATA = "No Data Exist.";
    private static final String ERROR_NO_MAIN_GROUP = "The requested main group could not be found. Please verify your selection and try again.";
    private static final Pattern NAME_VALIDATION_PATTERN = Pattern.compile("^[a-zA-Z\\s]+$");

    private void validateMainGroupName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Main Group name is required. Please provide a valid name.");
        }
        if (!NAME_VALIDATION_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("Main Group name can only contain letters and spaces. Please correct it and try again.");
        }
    }

    @Override
    @Transactional
    public ResponseMessage<String> addMainGroup(DesktopMenuMainGroupMasterOperation menuMainGroupMaster) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            String mainGroupName = menuMainGroupMaster.getMainGroupName().trim();
            validateMainGroupName(mainGroupName);
            if (menuMainGroupRepo.existsByMainGroupNameIgnoreCase(mainGroupName)) {
                response.setHeader("Duplicate Entry");
                response.setMessage("A Main Group with this name already exists. Please choose a different name.");
                response.setStatusCode(409);
                return response;
            }
            Integer maxId = menuMainGroupRepo.findMaxId();
            int newId = (maxId != null ? maxId : 10) + 1;
            if (newId > 99) {
                response.setHeader("Limit Reached");
                response.setMessage("The maximum number of Main Groups (99) has been reached. You cannot add more records.");
                response.setStatusCode(403);
                return response;
            }
            Integer maxHierarchyId = menuMainGroupRepo.findMaxHierarchyId();
            int newHierarchyId = (maxHierarchyId != null ? maxHierarchyId : 0) + 1;
            menuMainGroupMaster.setMainGroupId(newId);
            menuMainGroupMaster.setMainGroupName(mainGroupName);
            menuMainGroupMaster.setHierarchyId(newHierarchyId);
            menuMainGroupRepo.save(menuMainGroupMaster);
            response.setHeader(SUCCESS_MSG);
            response.setMessage("Main Group added successfully.");
            response.setStatusCode(200);
            return response;
        } catch (IllegalArgumentException e) {
            response.setHeader("Invalid Input");
            response.setMessage(e.getMessage());
            response.setStatusCode(400);
            return response;
        } catch (Exception exception) {
            logger.error(LOG_MESSAGE, exception);
            response.setHeader("Error");
            response.setMessage("An unexpected error occurred.");
            response.setStatusCode(500);
            return response;
        }
    }

    @Override
    @Transactional
    public ResponseMessage<List<DesktopMenuMainGroupOperationDto>> getAllMainGroups() {
        ResponseMessage<List<DesktopMenuMainGroupOperationDto>> response = new ResponseMessage<>();
        try {
            List<DesktopMenuMainGroupMasterOperation> list = menuMainGroupRepo.findAllByOrderByHierarchyIdAsc();
            if (list == null || list.isEmpty()) {
                response.setHeader("No Data Found");
                response.setMessage("No main groups found. Please add a main group first.");
                response.setStatusCode(200);
                response.setResponseOutput(new ArrayList<>());
                return response;
            }
            List<DesktopMenuMainGroupOperationDto> dtoList = new ArrayList<>();
            for (DesktopMenuMainGroupMasterOperation entity : list) {
                DesktopMenuMainGroupOperationDto dto = new DesktopMenuMainGroupOperationDto();
                dto.setMainGroupId(entity.getMainGroupId());
                dto.setHierarchyId(entity.getHierarchyId());
                dto.setMainGroupName(entity.getMainGroupName());
                dto.setIconPath(entity.getIconPath());
                dtoList.add(dto);
            }
            response.setHeader(SUCCESS_MSG);
            response.setMessage("Main groups fetched successfully. Found " + dtoList.size() + " main group(s).");
            response.setStatusCode(200);
            response.setResponseOutput(dtoList);
            return response;
        } catch (Exception e) {
            logger.error(LOG_MESSAGE, e);
            response.setHeader("Error");
            response.setMessage("An unexpected error occurred while fetching main groups.");
            response.setStatusCode(500);
            return response;
        }
    }

    @Override
    @Transactional
    public ResponseMessage<DesktopMenuMainGroupOperationDto> getMainGroupById(int id) {
        ResponseMessage<DesktopMenuMainGroupOperationDto> response = new ResponseMessage<>();
        try {
            Optional<DesktopMenuMainGroupMasterOperation> optional = menuMainGroupRepo.findById(id);
            if (optional.isPresent()) {
                DesktopMenuMainGroupMasterOperation entity = optional.get();
                DesktopMenuMainGroupOperationDto dto = new DesktopMenuMainGroupOperationDto();
                dto.setMainGroupId(entity.getMainGroupId());
                dto.setHierarchyId(entity.getHierarchyId());
                dto.setMainGroupName(entity.getMainGroupName());
                dto.setIconPath(entity.getIconPath());
                response.setHeader(SUCCESS_MSG);
                response.setMessage("Main Group found.");
                response.setStatusCode(200);
                response.setResponseOutput(dto);
            } else {
                response.setHeader(ERROR_NO_DATA);
                response.setMessage(ERROR_NO_MAIN_GROUP);
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

    @Override
    @Transactional
    public ResponseMessage<String> updateMainGroup(int id, DesktopMenuMainGroupMasterOperation inputGroup) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            Optional<DesktopMenuMainGroupMasterOperation> optional = menuMainGroupRepo.findById(id);
            if (optional.isPresent()) {
                DesktopMenuMainGroupMasterOperation existingGroup = optional.get();
                String newMainGroupName = inputGroup.getMainGroupName();
                if (newMainGroupName != null) {
                    newMainGroupName = newMainGroupName.trim();
                    validateMainGroupName(newMainGroupName);
                    if (!newMainGroupName.equalsIgnoreCase(existingGroup.getMainGroupName()) &&
                            menuMainGroupRepo.existsByMainGroupNameIgnoreCaseAndMainGroupIdNot(newMainGroupName, existingGroup.getMainGroupId())) {
                        response.setHeader("Duplicate Entry");
                        response.setMessage("A Main Group with this name already exists. Please choose a different name.");
                        response.setStatusCode(409);
                        return response;
                    }
                    existingGroup.setMainGroupName(newMainGroupName);
                }
                if (inputGroup.getIconPath() != null) {
                    existingGroup.setIconPath(inputGroup.getIconPath());
                }
                menuMainGroupRepo.save(existingGroup);
                response.setHeader(SUCCESS_MSG);
                response.setMessage("Main Group updated successfully.");
                response.setStatusCode(200);
            } else {
                response.setHeader(ERROR_NO_DATA);
                response.setMessage(ERROR_NO_MAIN_GROUP);
                response.setStatusCode(404);
            }
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
    public ResponseMessage<String> deleteMainGroup(int id) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            Optional<DesktopMenuMainGroupMasterOperation> optional = menuMainGroupRepo.findById(id);
            if (optional.isPresent()) {
                menuMainGroupRepo.deleteById(id);
                response.setHeader(SUCCESS_MSG);
                response.setMessage("Main Group deleted successfully.");
                response.setStatusCode(200);
            } else {
                response.setHeader(ERROR_NO_DATA);
                response.setMessage(ERROR_NO_MAIN_GROUP);
                response.setStatusCode(404);
            }
            return response;
        } catch (Exception exception) {
            logger.error(LOG_MESSAGE, exception);
            response.setHeader("Error");
            response.setMessage("An unexpected error occurred.");
            response.setStatusCode(500);
            return response;
        }
    }

    @Override
    public ResponseMessage<String> updateMainGroupOrder(List<MainGroupOperationOrderDto> orderDtos) {
        ResponseMessage<String> response = new ResponseMessage<>();
        response.setHeader("Not Implemented");
        response.setMessage("Method not implemented yet.");
        response.setStatusCode(501);
        return response;
    }
}
