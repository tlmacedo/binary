package br.com.tlmacedo.binary.model.Enums;

public class Error {

    private String code;
    private String message;
    private CONTRAC_TYPE contrac_type;
    private Integer barrier;
    private String buyId;

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

    public CONTRAC_TYPE getContrac_type() {
        return contrac_type;
    }

    public void setContrac_type(CONTRAC_TYPE contrac_type) {
        this.contrac_type = contrac_type;
    }

    public Integer getBarrier() {
        return barrier;
    }

    public void setBarrier(Integer barrier) {
        this.barrier = barrier;
    }

    public String getBuyId() {
        return buyId;
    }

    public void setBuyId(String buyId) {
        this.buyId = buyId;
    }

    @Override
    public String toString() {
        return "Error{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", contrac_type=" + contrac_type +
                ", barrier=" + barrier +
                ", buyId='" + buyId + '\'' +
                '}';
    }
}
