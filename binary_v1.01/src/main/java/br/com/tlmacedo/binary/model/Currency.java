package br.com.tlmacedo.binary.model;

public class Currency {

    private Integer fractional_digits;

    public Currency() {
    }

    public Integer getFractional_digits() {
        return fractional_digits;
    }

    public void setFractional_digits(Integer fractional_digits) {
        this.fractional_digits = fractional_digits;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "fractional_digits=" + fractional_digits +
                '}';
    }
}
