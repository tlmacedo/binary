package br.com.tlmacedo.binary.model.vo;

import br.com.tlmacedo.binary.model.enums.MSG_TYPE;

public class Msg_type {

    private MSG_TYPE msg_type;

    public Msg_type(String strMsg_type) {
        this.setMsg_type(MSG_TYPE.valueOf(strMsg_type.toUpperCase()));
    }

    public MSG_TYPE getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(MSG_TYPE msg_type) {
        this.msg_type = msg_type;
    }

    @Override
    public String toString() {
        return "Msg_type{" +
                "msg_type=" + msg_type +
                '}';
    }
}
