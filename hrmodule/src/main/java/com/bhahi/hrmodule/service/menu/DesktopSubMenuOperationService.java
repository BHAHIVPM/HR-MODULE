package com.bhahi.hrmodule.service.menu;

import com.bhahi.hrmodule.model.menu.DesktopMenuSubGroupMasterOperation;
import com.bhahi.hrmodule.response.ResponseMessage;

import java.util.List;

public interface DesktopSubMenuOperationService {

ResponseMessage<String> addSubMenu(DesktopMenuSubGroupMasterOperation subMenu);

ResponseMessage<List<DesktopMenuSubGroupMasterOperation>> getAllSubMenu();

ResponseMessage<DesktopMenuSubGroupMasterOperation> getById(int subMenuId);
}