package com.bhahi.hrmodule.response;

import lombok.Data;

@Data
public class HeaderMessage {
    
    public HeaderMessage(){};
    public HeaderMessage(String header, String message, int statusCode){
        this.header=header;
        this.message=message;
        this.statusCode=statusCode;
    };

    private String header ="Unknown error.";
    private String message="Something went wrong on our end, please try again after some time.";
    private int statusCode=500;
}
