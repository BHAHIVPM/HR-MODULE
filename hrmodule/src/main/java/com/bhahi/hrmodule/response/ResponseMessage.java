package com.bhahi.hrmodule.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class  ResponseMessage<C> extends HeaderMessage {
    private C responseOutput;
}
