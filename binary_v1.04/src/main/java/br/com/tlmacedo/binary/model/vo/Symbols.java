package br.com.tlmacedo.binary.model.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Symbols implements Serializable {
    public static final long serialVersionUID = 1L;

    List<Symbol> active_symbols = new ArrayList<>();

    public Symbols() {
    }

    public List<Symbol> getActive_symbols() {
        return active_symbols;
    }

    public void setActive_symbols(List<Symbol> active_symbols) {
        this.active_symbols = active_symbols;
    }

    @Override
    public String toString() {
        return "Symbols{" +
                "active_symbols=" + active_symbols +
                '}';
    }
}
