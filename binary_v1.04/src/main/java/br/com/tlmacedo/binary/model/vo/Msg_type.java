package br.com.tlmacedo.binary.model.vo;

import br.com.tlmacedo.binary.model.enums.MSG_TYPE;

import java.io.Serializable;

public class Msg_type implements Serializable {
    public static final long serialVersionUID = 1L;

    MSG_TYPE msgType;

    public Msg_type() {
    }

    public Msg_type(MSG_TYPE msgType) {
        this.msgType = msgType;
    }

    public Msg_type(String strMsgType) {
        this.msgType = MSG_TYPE.valueOf(strMsgType.toUpperCase());
    }

    public MSG_TYPE getMsgType() {
        return msgType;
    }

    public void setMsgType(MSG_TYPE msgType) {
        this.msgType = msgType;
    }

    @Override
    public String toString() {
        return "Msg_type{" +
                "msgType=" + msgType +
                '}';
    }
}
