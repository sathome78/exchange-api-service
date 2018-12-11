package me.exrates.model.dto.merchants.qtum;

import java.util.List;

public class QtumJsonRpcRequest {
    private final Integer id = 1;
    private final String jsonrpc = "2.0";
    private String method;
    private List<Object> params;

    public Integer getId() {
        return id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public String getMethod() {
        return method;
    }

    public List<Object> getParams() {
        return params;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }
}
