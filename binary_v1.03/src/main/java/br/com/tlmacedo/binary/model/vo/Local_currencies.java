package br.com.tlmacedo.binary.model.vo;

import java.io.Serializable;

public class Local_currencies implements Serializable {
    public static final long serialVersionUID = 1L;

    BRL brl;

    public Local_currencies() {
    }

    public BRL getBrl() {
        return brl;
    }

    public void setBrl(BRL brl) {
        this.brl = brl;
    }

    @Override
    public String toString() {
        return "Local_currencies{" +
                "brl=" + brl +
                '}';
    }
}
