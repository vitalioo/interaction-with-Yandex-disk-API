package com.example.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public class GetUrlSuccessDTO {
    @JsonAlias({"operation_id"})
    private String operationID;
    private String href;
    private String method;
    private boolean templated;

    public String getOperationID() {
        return operationID;
    }

    public void setOperationID(String operationID) {
        this.operationID = operationID;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public boolean isTemplated() {
        return templated;
    }

    public void setTemplated(boolean templated) {
        this.templated = templated;
    }
}
