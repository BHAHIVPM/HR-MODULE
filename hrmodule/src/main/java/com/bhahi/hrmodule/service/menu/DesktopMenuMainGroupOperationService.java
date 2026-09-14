package com.bhahi.hrmodule.service.menu;

import com.bhahi.hrmodule.dto.menu.DesktopMenuMainGroupOperationDto;
import com.bhahi.hrmodule.dto.menu.MainGroupOperationOrderDto;
import com.bhahi.hrmodule.model.menu.DesktopMenuMainGroupMasterOperation;
import com.bhahi.hrmodule.response.ResponseMessage;

import java.util.List;

public interface DesktopMenuMainGroupOperationService {

ResponseMessage<String> addMainGroup(DesktopMenuMainGroupMasterOperation mainGroup);

ResponseMessage<List<DesktopMenuMainGroupOperationDto>> getAllMainGroups();

ResponseMessage<DesktopMenuMainGroupOperationDto> getMainGroupById(int id);

ResponseMessage<String> updateMainGroup(int id, DesktopMenuMainGroupMasterOperation mainGroup);

ResponseMessage<String> deleteMainGroup(int id);

ResponseMessage<String> updateMainGroupOrder(List<MainGroupOperationOrderDto> orderDtos);
}