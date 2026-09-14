package com.bhahi.hrmodule.service.asset;

import com.bhahi.hrmodule.model.asset.AssetMaster;
import com.bhahi.hrmodule.repository.asset.AssetMasterRepo;
import com.bhahi.hrmodule.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssetMasterService {

    private final AssetMasterRepo assetMasterRepo;

    public ResponseMessage<AssetMaster> save(AssetMaster asset) {
        ResponseMessage<AssetMaster> response = new ResponseMessage<>();
        try {
            if (asset.getStatus() == null) {
                asset.setStatus(asset.getIssuedToEmployeeId() == null
                        ? AssetMaster.AssetStatus.AVAILABLE
                        : AssetMaster.AssetStatus.ISSUED);
            }
            if (asset.getAssetCondition() == null) {
                asset.setAssetCondition(AssetMaster.AssetCondition.NEW);
            }
            AssetMaster saved = assetMasterRepo.save(asset);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Asset saved successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Save failed");
            response.setMessage("Could not save the asset. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<AssetMaster> findById(int assetId) {
        ResponseMessage<AssetMaster> response = new ResponseMessage<>();
        try {
            Optional<AssetMaster> asset = assetMasterRepo.findById(assetId);
            if (asset.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No asset found for this id.");
                response.setStatusCode(404);
                return response;
            }
            response.setResponseOutput(asset.get());
            response.setHeader("Success");
            response.setMessage("Asset fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch the asset. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<AssetMaster>> findByEmployee(int employeeId) {
        ResponseMessage<List<AssetMaster>> response = new ResponseMessage<>();
        try {
            List<AssetMaster> assets = assetMasterRepo.findByIssuedToEmployeeId(employeeId);
            response.setResponseOutput(assets);
            response.setHeader("Success");
            response.setMessage("Assets fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch assets. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<AssetMaster>> findAvailable() {
        ResponseMessage<List<AssetMaster>> response = new ResponseMessage<>();
        try {
            List<AssetMaster> assets = assetMasterRepo.findByStatus(AssetMaster.AssetStatus.AVAILABLE);
            response.setResponseOutput(assets);
            response.setHeader("Success");
            response.setMessage("Available assets fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch available assets. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<AssetMaster>> findAll() {
        ResponseMessage<List<AssetMaster>> response = new ResponseMessage<>();
        try {
            List<AssetMaster> assets = assetMasterRepo.findAll();
            response.setResponseOutput(assets);
            response.setHeader("Success");
            response.setMessage("Assets fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch assets. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    // Issues an in-stock asset to an employee.
    public ResponseMessage<AssetMaster> issueTo(int assetId, int employeeId) {
        ResponseMessage<AssetMaster> response = new ResponseMessage<>();
        try {
            Optional<AssetMaster> existingOpt = assetMasterRepo.findById(assetId);
            if (existingOpt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No asset found for this id.");
                response.setStatusCode(404);
                return response;
            }
            AssetMaster existing = existingOpt.get();
            if (existing.getStatus() != AssetMaster.AssetStatus.AVAILABLE) {
                response.setHeader("Invalid state");
                response.setMessage("Asset is not available to issue. Current status: " + existing.getStatus());
                response.setStatusCode(409);
                return response;
            }
            existing.setIssuedToEmployeeId(employeeId);
            existing.setIssuedDate(LocalDate.now());
            existing.setReturnDate(null);
            existing.setStatus(AssetMaster.AssetStatus.ISSUED);

            AssetMaster saved = assetMasterRepo.save(existing);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Asset issued successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Update failed");
            response.setMessage("Could not issue the asset. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    // Marks an issued asset as returned and puts it back in the available pool.
    public ResponseMessage<AssetMaster> returnAsset(int assetId, AssetMaster.AssetCondition conditionOnReturn, String remarks) {
        ResponseMessage<AssetMaster> response = new ResponseMessage<>();
        try {
            Optional<AssetMaster> existingOpt = assetMasterRepo.findById(assetId);
            if (existingOpt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No asset found for this id.");
                response.setStatusCode(404);
                return response;
            }
            AssetMaster existing = existingOpt.get();
            existing.setReturnDate(LocalDate.now());
            existing.setAssetCondition(conditionOnReturn);
            existing.setRemarks(remarks);
            existing.setStatus(conditionOnReturn == AssetMaster.AssetCondition.DAMAGED
                    ? AssetMaster.AssetStatus.DAMAGED
                    : AssetMaster.AssetStatus.AVAILABLE);
            existing.setIssuedToEmployeeId(null);

            AssetMaster saved = assetMasterRepo.save(existing);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Asset returned successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Update failed");
            response.setMessage("Could not return the asset. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<AssetMaster> update(int assetId, AssetMaster updates) {
        ResponseMessage<AssetMaster> response = new ResponseMessage<>();
        try {
            Optional<AssetMaster> existingOpt = assetMasterRepo.findById(assetId);
            if (existingOpt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No asset found for this id.");
                response.setStatusCode(404);
                return response;
            }

            AssetMaster existing = existingOpt.get();
            existing.setAssetName(updates.getAssetName());
            existing.setAssetType(updates.getAssetType());
            existing.setSerialNumber(updates.getSerialNumber());
            existing.setAssetCondition(updates.getAssetCondition());
            existing.setStatus(updates.getStatus());
            existing.setRemarks(updates.getRemarks());

            AssetMaster saved = assetMasterRepo.save(existing);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Asset updated successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Update failed");
            response.setMessage("Could not update the asset. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<String> delete(int assetId) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            if (!assetMasterRepo.existsById(assetId)) {
                response.setHeader("Not found");
                response.setMessage("No asset found for this id.");
                response.setStatusCode(404);
                return response;
            }
            assetMasterRepo.deleteById(assetId);
            response.setResponseOutput("Deleted.");
            response.setHeader("Success");
            response.setMessage("Asset deleted successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Delete failed");
            response.setMessage("Could not delete the asset. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }
}
