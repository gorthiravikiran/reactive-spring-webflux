package com.reactivespring.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private String statusCode;
    private String reason;
    private List<String> errorMsgs;

}
