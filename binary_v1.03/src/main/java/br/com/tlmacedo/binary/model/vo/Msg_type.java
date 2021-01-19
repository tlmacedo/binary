package br.com.tlmacedo.binary.model.vo;

import br.com.tlmacedo.binary.model.enums.MSG_TYPE;

public class Msg_type {

    MSG_TYPE msgType;

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
