package br.com.tlmacedo.binary.model.vo;

import java.io.Serializable;

public class BRL implements Serializable {
    public static final long serialVersionUID = 1L;

    Integer fractional_digits;

    public BRL() {
    }

    public Integer getFractional_digits() {
        return fractional_digits;
    }

    public void setFractional_digits(Integer fractional_digits) {
        this.fractional_digits = fractional_digits;
    }

    @Override
    public String toString() {
        return "BRL{" +
                "fractional_digits=" + fractional_digits +
                '}';
    }
}
