package com.bhahi.hrmodule.service.menu;

import com.bhahi.hrmodule.dto.menu.DesktopMenuNameMasterOperationDto;
import com.bhahi.hrmodule.model.menu.DesktopMenuNameMasterOperation;
import com.bhahi.hrmodule.repository.menu.DesktopMenuMasterOperationRepository;
import com.bhahi.hrmodule.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class DesktopMenuOperationServiceImpl implements DesktopMenuOperationService {

    private final DesktopMenuMasterOperationRepository menuRepo;

    private static final Logger logger = LoggerFactory.getLogger(DesktopMenuOperationServiceImpl.class);
    private static final String LOG_MESSAGE = "Exception logging: ";
    private static final String SUCCESS_MSG = "Success.";
    private static final String ERROR_NO_DATA_HEADER = "No Data Exist.";
    private static final String ERROR_MENU_NOT_FOUND = "The requested menu could not be found. Please verify the menu ID and try again.";
    private static final Pattern NAME_VALIDATION_PATTERN = Pattern.compile("^[a-zA-Z\\s]+$");

    private void validateMenuName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Menu Name cannot be empty. Please provide a valid name.");
        }
        if (!NAME_VALIDATION_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("Menu name must contain only alphabet letters and spaces.");
        }
    }

    private String validateYesNoInput(String input, String fieldName) {
        if (input == null || (!"YES".equalsIgnoreCase(input) && !"NO".equalsIgnoreCase(input))) {
            throw new IllegalArgumentException(fieldName + " must be either YES or NO.");
        }
        return input.toUpperCase();
    }

    @Override
    @Transactional
    public ResponseMessage<String> addMenu(DesktopMenuNameMasterOperation menuMaster) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            String menuName = menuMaster.getMenuName().trim();
            validateMenuName(menuName);
            if (menuMaster.getMainGroupId() == 0) {
                response.setHeader("Missing Required Field");
                response.setMessage("Main Group ID must be provided to create a new menu.");
                response.setStatusCode(400);
                return response;
            }
            if (menuRepo.existsByMenuNameIgnoreCaseAndMainGroupId(menuName, menuMaster.getMainGroupId())) {
                response.setHeader("Duplicate Entry");
                response.setMessage("A menu with the same name already exists in the selected main group.");
                response.setStatusCode(409);
                return response;
            }
            Integer maxId = menuRepo.findMaxId();
            int newId = (maxId != null ? maxId : 1000) + 1;
            if (newId > 9999) {
                response.setHeader("ID Limit Reached");
                response.setMessage("The system cannot generate a new Menu ID as the maximum limit (9999) has been reached.");
                response.setStatusCode(403);
                return response;
            }
            menuMaster.setMenuNameId(newId);
            menuMaster.setMenuName(menuName);
            menuMaster.setEditOption(validateYesNoInput(menuMaster.getEditOption(), "Edit Option"));
            menuMaster.setIsPrivilege(validateYesNoInput(menuMaster.getIsPrivilege(), "IsPrivilege"));
            menuMaster.setHierarchyId(Integer.MAX_VALUE);
            menuRepo.save(menuMaster);
            List<DesktopMenuNameMasterOperation> allMenus = menuRepo.findAllOrderedByHierarchy();
            allMenus.sort(Comparator.comparing(DesktopMenuNameMasterOperation::getMainGroupId).thenComparing(DesktopMenuNameMasterOperation::getHierarchyId));
            int globalHierarchyIdCounter = 1;
            for (DesktopMenuNameMasterOperation menu : allMenus) {
                menu.setHierarchyId(globalHierarchyIdCounter++);
            }
            if (!allMenus.isEmpty()) {
                menuRepo.saveAll(allMenus);
            }
            response.setHeader(SUCCESS_MSG);
            response.setMessage("Menu added successfully.");
            response.setStatusCode(200);
            return response;
        } catch (IllegalArgumentException e) {
            response.setHeader("Invalid Input");
            response.setMessage(e.getMessage());
            response.setStatusCode(400);
            return response;
        } catch (DataIntegrityViolationException e) {
            response.setHeader("Duplicate Entry");
            response.setMessage("Component path already exists.");
            response.setStatusCode(409);
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
    public ResponseMessage<List<DesktopMenuNameMasterOperationDto>> getAllMenus() {
        ResponseMessage<List<DesktopMenuNameMasterOperationDto>> response = new ResponseMessage<>();
        try {
            List<DesktopMenuNameMasterOperation> list = menuRepo.findAll();
            if (list == null || list.isEmpty()) {
                response.setHeader("No Data Found");
                response.setMessage("No menu items found. Please add a menu item first.");
                response.setStatusCode(200);
                response.setResponseOutput(new ArrayList<>());
                return response;
            }
            List<DesktopMenuNameMasterOperationDto> dtoList = new ArrayList<>();
            for (DesktopMenuNameMasterOperation entity : list) {
                DesktopMenuNameMasterOperationDto dto = new DesktopMenuNameMasterOperationDto();
                dto.setMenuNameId(entity.getMenuNameId());
                dto.setMenuName(entity.getMenuName());
                dto.setComponentPath(entity.getComponentPath());
                dto.setHierarchyId(entity.getHierarchyId());
                dto.setMainGroupId(entity.getMainGroupId());
                dto.setAddOption(entity.getAddOption());
                dto.setEditOption(entity.getEditOption());
                dto.setDeleteOption(entity.getDeleteOption());
                dto.setIsPrivilege(entity.getIsPrivilege());
                dto.setSubGroupId(entity.getSubGroupId());
                dtoList.add(dto);
            }
            response.setHeader(SUCCESS_MSG);
            response.setMessage("Menu items fetched successfully. Found " + dtoList.size() + " menu item(s).");
            response.setStatusCode(200);
            response.setResponseOutput(dtoList);
            return response;
        } catch (Exception e) {
            logger.error(LOG_MESSAGE, e);
            response.setHeader("Error");
            response.setMessage("An unexpected error occurred while fetching menu items.");
            response.setStatusCode(500);
            return response;
        }
    }

    @Override
    @Transactional
    public ResponseMessage<DesktopMenuNameMasterOperationDto> getMenuById(int id) {
        ResponseMessage<DesktopMenuNameMasterOperationDto> response = new ResponseMessage<>();
        try {
            Optional<DesktopMenuNameMasterOperation> optional = menuRepo.findById(id);
            if (optional.isPresent()) {
                DesktopMenuNameMasterOperation entity = optional.get();
                DesktopMenuNameMasterOperationDto dto = new DesktopMenuNameMasterOperationDto();
                dto.setMenuNameId(entity.getMenuNameId());
                dto.setMenuName(entity.getMenuName());
                dto.setComponentPath(entity.getComponentPath());
                dto.setHierarchyId(entity.getHierarchyId());
                dto.setMainGroupId(entity.getMainGroupId());
                dto.setAddOption(entity.getAddOption());
                dto.setEditOption(entity.getEditOption());
                dto.setDeleteOption(entity.getDeleteOption());
                dto.setIsPrivilege(entity.getIsPrivilege());
                dto.setSubGroupId(entity.getSubGroupId());
                response.setHeader(SUCCESS_MSG);
                response.setMessage("Menu found.");
                response.setStatusCode(200);
                response.setResponseOutput(dto);
            } else {
                response.setHeader(ERROR_NO_DATA_HEADER);
                response.setMessage(ERROR_MENU_NOT_FOUND);
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
    public ResponseMessage<String> updateMenu(int id, DesktopMenuNameMasterOperation menuMaster) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            Optional<DesktopMenuNameMasterOperation> optional = menuRepo.findById(id);
            if (optional.isEmpty()) {
                response.setHeader(ERROR_NO_DATA_HEADER);
                response.setMessage(ERROR_MENU_NOT_FOUND);
                response.setStatusCode(404);
                return response;
            }
            DesktopMenuNameMasterOperation existingMenu = optional.get();
            String newMenuName = menuMaster.getMenuName();
            if (newMenuName != null) {
                newMenuName = newMenuName.trim();
                validateMenuName(newMenuName);
                if (menuRepo.existsByMenuNameIgnoreCaseAndMainGroupIdAndMenuNameIdNot(newMenuName, menuMaster.getMainGroupId(), id)) {
                    response.setHeader("Duplicate Entry");
                    response.setMessage("A menu with the same name already exists in the selected main group.");
                    response.setStatusCode(409);
                    return response;
                }
                existingMenu.setMenuName(newMenuName);
            }
            updateOptionalFields(existingMenu, menuMaster);
            menuRepo.save(existingMenu);
            response.setHeader(SUCCESS_MSG);
            response.setMessage("Menu updated successfully.");
            response.setStatusCode(200);
            return response;
        } catch (IllegalArgumentException e) {
            response.setHeader("Invalid Input");
            response.setMessage(e.getMessage());
            response.setStatusCode(400);
            return response;
        } catch (DataIntegrityViolationException e) {
            response.setHeader("Duplicate Entry");
            response.setMessage("Component path already exists.");
            response.setStatusCode(409);
            return response;
        } catch (Exception e) {
            logger.error(LOG_MESSAGE, e);
            response.setHeader("Error");
            response.setMessage("An unexpected error occurred.");
            response.setStatusCode(500);
            return response;
        }
    }

    private void updateOptionalFields(DesktopMenuNameMasterOperation existingMenu, DesktopMenuNameMasterOperation menuMaster) {
        if (menuMaster.getComponentPath() != null) {
            existingMenu.setComponentPath(menuMaster.getComponentPath());
        }
        if (menuMaster.getMainGroupId() != 0) {
            existingMenu.setMainGroupId(menuMaster.getMainGroupId());
        }
        if (menuMaster.getAddOption() != null) {
            existingMenu.setAddOption(validateYesNoInput(menuMaster.getAddOption(), "Add Option"));
        }
        if (menuMaster.getEditOption() != null) {
            existingMenu.setEditOption(validateYesNoInput(menuMaster.getEditOption(), "Edit Option"));
        }
        if (menuMaster.getDeleteOption() != null) {
            existingMenu.setDeleteOption(validateYesNoInput(menuMaster.getDeleteOption(), "Delete Option"));
        }
        if (menuMaster.getIsPrivilege() != null) {
            existingMenu.setIsPrivilege(validateYesNoInput(menuMaster.getIsPrivilege(), "IsPrivilege"));
        }
    }

    @Override
    @Transactional
    public ResponseMessage<String> deleteMenu(int id) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            Optional<DesktopMenuNameMasterOperation> optional = menuRepo.findById(id);
            if (optional.isPresent()) {
                menuRepo.deleteById(id);
                response.setHeader(SUCCESS_MSG);
                response.setMessage("Menu deleted successfully.");
                response.setStatusCode(200);
            } else {
                response.setHeader(ERROR_NO_DATA_HEADER);
                response.setMessage(ERROR_MENU_NOT_FOUND);
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
    public ResponseMessage<String> updateMenuHierarchyOrder(int dbMainGroupId, List<DesktopMenuNameMasterOperationDto> orderDtos) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            if (orderDtos == null || orderDtos.isEmpty()) {
                response.setHeader("Invalid Input");
                response.setMessage("The order list cannot be empty. Please provide at least one item in the list.");
                response.setStatusCode(400);
                return response;
            }
            List<DesktopMenuNameMasterOperation> existingMenusInGroup = menuRepo.findByMainGroupIdOrderByHierarchyIdAsc(dbMainGroupId);
            List<Integer> incomingMenuIds = orderDtos.stream().map(DesktopMenuNameMasterOperationDto::getMenuNameId).toList();
            for (DesktopMenuNameMasterOperationDto dto : orderDtos) {
                validateMenuDto(dto, dbMainGroupId, existingMenusInGroup);
            }
            List<Integer> sortedTargetHierarchyIds = existingMenusInGroup.stream().filter(m -> incomingMenuIds.contains(m.getMenuNameId())).map(DesktopMenuNameMasterOperation::getHierarchyId).sorted().toList();
            if (orderDtos.size() != sortedTargetHierarchyIds.size()) {
                response.setHeader("Something went wrong!");
                response.setMessage("An error occurred. Please try again.");
                response.setStatusCode(500);
                return response;
            }
            List<DesktopMenuNameMasterOperation> menusToUpdate = new ArrayList<>();
            for (int i = 0; i < orderDtos.size(); i++) {
                DesktopMenuNameMasterOperationDto dto = orderDtos.get(i);
                Optional<DesktopMenuNameMasterOperation> menuOpt = menuRepo.findById(dto.getMenuNameId());
                if (menuOpt.isPresent()) {
                    DesktopMenuNameMasterOperation menu = menuOpt.get();
                    menu.setHierarchyId(sortedTargetHierarchyIds.get(i));
                    menusToUpdate.add(menu);
                }
            }
            if (!menusToUpdate.isEmpty()) {
                menuRepo.saveAll(menusToUpdate);
            }
            response.setHeader(SUCCESS_MSG);
            response.setMessage("Menu hierarchy updated successfully.");
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

    private void validateMenuDto(DesktopMenuNameMasterOperationDto dto, int dbMainGroupId, List<DesktopMenuNameMasterOperation> existingMenusInGroup) {
        if (dto.getMenuNameId() == null) {
            throw new IllegalArgumentException("The provided Menu ID does not contain value. Please verify your input and try again.");
        }
        boolean menuExists = existingMenusInGroup.stream().anyMatch(m -> m.getMenuNameId() == dto.getMenuNameId());
        if (!menuExists) {
            throw new IllegalArgumentException("The provided Menu ID does not match the expected Main Group. Please verify your input and try again.");
        }
    }
}
