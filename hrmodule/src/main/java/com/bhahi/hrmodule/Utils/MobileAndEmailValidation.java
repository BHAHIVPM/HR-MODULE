package com.bhahi.hrmodule.Utils;
import com.bhahi.hr.exception.CustomException;


public class MobileAndEmailValidation {


    public static void mobileNumValidation(String number){

        if(number==null || number.trim().length()!=10 || !number.trim().matches("\\d+")){
            throw new CustomException("Invalid.","Invalid mobile number, please verify it.", 400);
        }

    }
    public static void pinCodeValidation(String number){
        if(number== null || number.trim().length()!=6){
            throw new CustomException("Invalid.","Invalid pin code number, please verify it.", 400);
        }
    }

    public static void emailIdValidation(String email){

        String emailValidate = "^[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*@[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*\\.[a-zA-Z]{2,}$";
        if (email==null || !email.trim().matches(emailValidate)) {

            throw new CustomException("Invalid Email Format", "The provided email address format is invalid.", 400);
        }
    }



}
