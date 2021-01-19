package br.com.tlmacedo.binary.model;

import java.util.List;

public class Times {

    private List<Integer> times;

    public Times() {
    }

    public List<Integer> getTimes() {
        return times;
    }

    public void setTimes(List<Integer> times) {
        this.times = times;
    }

    @Override
    public String toString() {
        return "History_Times{" +
                "times=" + times +
                '}';
    }
}
