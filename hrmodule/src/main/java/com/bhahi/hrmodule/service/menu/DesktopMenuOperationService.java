package com.bhahi.hrmodule.service.menu;

import com.bhahi.hrmodule.dto.menu.DesktopMenuNameMasterOperationDto;
import com.bhahi.hrmodule.model.menu.DesktopMenuNameMasterOperation;
import com.bhahi.hrmodule.response.ResponseMessage;

import java.util.List;

public interface DesktopMenuOperationService {

ResponseMessage<String> addMenu(DesktopMenuNameMasterOperation menuMaster);

ResponseMessage<List<DesktopMenuNameMasterOperationDto>> getAllMenus();

ResponseMessage<DesktopMenuNameMasterOperationDto> getMenuById(int id);

ResponseMessage<String> updateMenu(int id, DesktopMenuNameMasterOperation menuMaster);

ResponseMessage<String> deleteMenu(int id);

ResponseMessage<String> updateMenuHierarchyOrder(int dbMainGroupId, List<DesktopMenuNameMasterOperationDto> orderDtos);
}