package com.bhahi.hrmodule.service.ui;

import com.bhahi.hrmodule.model.ui.UiThemeMaster;
import com.bhahi.hrmodule.repository.ui.UiThemeMasterRepo;
import com.bhahi.hrmodule.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UiThemeMasterService {

    private final UiThemeMasterRepo uiThemeMasterRepo;

    public ResponseMessage<UiThemeMaster> save(UiThemeMaster theme) {
        ResponseMessage<UiThemeMaster> response = new ResponseMessage<>();
        try {
            UiThemeMaster saved = uiThemeMasterRepo.save(theme);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Theme saved successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Save failed");
            response.setMessage("Could not save the theme. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<UiThemeMaster> findById(int themeDocumentId) {
        ResponseMessage<UiThemeMaster> response = new ResponseMessage<>();
        try {
            Optional<UiThemeMaster> theme = uiThemeMasterRepo.findById(themeDocumentId);
            if (theme.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No theme found for this id.");
                response.setStatusCode(404);
                return response;
            }
            response.setResponseOutput(theme.get());
            response.setHeader("Success");
            response.setMessage("Theme fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch the theme. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<UiThemeMaster>> findAll() {
        ResponseMessage<List<UiThemeMaster>> response = new ResponseMessage<>();
        try {
            List<UiThemeMaster> themes = uiThemeMasterRepo.findAll();
            response.setResponseOutput(themes);
            response.setHeader("Success");
            response.setMessage("Themes fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch themes. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<UiThemeMaster> update(int themeDocumentId, UiThemeMaster updates) {
        ResponseMessage<UiThemeMaster> response = new ResponseMessage<>();
        try {
            Optional<UiThemeMaster> existingOpt = uiThemeMasterRepo.findById(themeDocumentId);
            if (existingOpt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No theme found for this id.");
                response.setStatusCode(404);
                return response;
            }

            UiThemeMaster existing = existingOpt.get();
            existing.setPrimaryColor(updates.getPrimaryColor());
            existing.setSecondaryColor(updates.getSecondaryColor());
            existing.setThirdColor(updates.getThirdColor());

            UiThemeMaster saved = uiThemeMasterRepo.save(existing);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Theme updated successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Update failed");
            response.setMessage("Could not update the theme. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<String> delete(int themeDocumentId) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            if (!uiThemeMasterRepo.existsById(themeDocumentId)) {
                response.setHeader("Not found");
                response.setMessage("No theme found for this id.");
                response.setStatusCode(404);
                return response;
            }
            uiThemeMasterRepo.deleteById(themeDocumentId);
            response.setResponseOutput("Deleted.");
            response.setHeader("Success");
            response.setMessage("Theme deleted successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Delete failed");
            response.setMessage("Could not delete the theme. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }
}