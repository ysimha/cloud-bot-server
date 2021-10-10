package ys.cloud.sbot.exceptions;

import lombok.Data;

@Data
public class ExceptionResponse {

    private String errorCode;
    private String errorMessage;

}

