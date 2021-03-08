package com.aliexpress.task;

import java.util.List;

public class AnswerData {
    private String contextId;
    private boolean success;
    private String code;
    private int pageSize;
    private String postback;
    private List<Product> results;

    public String getContextId() {
        return contextId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getCode() {
        return code;
    }

    public List<Product> getResults() {
        return results;
    }

    public int getPageSize() {
        return pageSize;
    }

    public String getPostback() {
        return postback;
    }
}
