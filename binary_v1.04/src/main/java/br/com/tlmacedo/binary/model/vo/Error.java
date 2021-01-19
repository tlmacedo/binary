package br.com.tlmacedo.binary.model.vo;

import java.io.Serializable;

public class Error implements Serializable {
    public static final long serialVersionUID = 1L;

    String code;
    String message;

    public Error() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Error{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
