package com.carPooling.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component

@NoArgsConstructor
public class GenricDTO<T> {
    @JsonProperty("status")
    private boolean status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("error")
    private String error;

    @JsonProperty("data")
    private T data;

    public GenricDTO(boolean status, String message ,T data) {
        this.status = status;
        this.message = message;
        this.error = error;
        this.data = data;
    }

    public GenricDTO(boolean status, String message, String error, T data) {
        this.status = status;
        this.message = message;
        this.error = error;
        this.data = data;
    }


    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
