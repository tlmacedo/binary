package br.com.tlmacedo.binary.model.vo;

import java.io.Serializable;

public class ActiveSymbols implements Serializable {
    public static final long serialVersionUID = 1L;

    String active_symbols;
    String product_type;

    public ActiveSymbols() {
        this.active_symbols = "brief";
        this.product_type = "basic";
    }

    public String getActive_symbols() {
        return active_symbols;
    }

    public void setActive_symbols(String active_symbols) {
        this.active_symbols = active_symbols;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    @Override
    public String toString() {
        return "ActiveSymbols{" +
                "active_symbols='" + active_symbols + '\'' +
                ", product_type='" + product_type + '\'' +
                '}';
    }

}
