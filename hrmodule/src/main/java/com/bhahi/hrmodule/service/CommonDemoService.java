package com.bhahi.hrmodule.service;

import com.bhahi.hr.dto.CommonResponse;
import org.springframework.stereotype.Service;

/**
 * Dummy service used only to prove that the "hrmodule" module can import and use
 * a class from the "common" module. Delete or replace this once you wire in your
 * real shared logic.
 */
@Service
public class CommonDemoService {
    /**
     * Returns an empty instance of {@link CommonResponse} that is declared in the
     * common module.
     */
    public CommonResponse getEmptyCommonObject() {
        return new CommonResponse();
    }
}
