package com.tech.motjip.Dto.RequestDto;

public class StatusUpdateRequestDto {

    private Integer statusCode;

    public StatusUpdateRequestDto(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
}