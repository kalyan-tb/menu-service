package com.swiggy.menu.exception;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {

    @JsonProperty("status")
    private Integer errorStatus;

    @JsonProperty("error_code")
    private String errorCode;

    @JsonProperty("message")
    private String errorMessage;

    @JsonProperty("error_type")
    private ErrorType errorType;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp = LocalDateTime.now();

    @JsonProperty("details")
    private List<String> details;

}
