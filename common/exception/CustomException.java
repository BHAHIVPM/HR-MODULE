package exception;

import lombok.Getter;

public class CustomException extends RuntimeException {
    @Getter
    private final String header;
    private final String message;
    @Getter
    private final int statusCode;

    @Override
    public String getMessage() {
        return message;
    }

    CustomException(String header, String message, int statusCode){
        this.header=header;
        this.message=message;
        this.statusCode=statusCode;
    };
    CustomException(){
        header="Something went wrong.";
        message="Unknown error, please try again.";
        statusCode=500;
    }
}
