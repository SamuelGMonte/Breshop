package br.com.breshop.dto;

public class ApiErrorResponseDto {

    private int code;
    private String message;

    public ApiErrorResponseDto(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}