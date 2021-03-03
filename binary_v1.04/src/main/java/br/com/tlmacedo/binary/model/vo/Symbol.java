package br.com.tlmacedo.binary.model.vo;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity(name = "Symbol")
@Table(name = "symbol")
public class Symbol implements Serializable {
    public static final long serialVersionUID = 1L;

    Long id;
    Boolean allow_forward_starting;
    String display_name;
    Boolean exchange_is_open;
    Boolean is_trading_suspended;
    String market;
    String market_display_name;
    BigDecimal pip;
    String submarket;
    String submarket_display_name;
    String symbol;
    String symbol_type;
    Integer tickTime;

    public Symbol() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(length = 1, nullable = false)
    public Boolean getAllow_forward_starting() {
        return allow_forward_starting;
    }

    public void setAllow_forward_starting(Boolean allow_forward_starting) {
        this.allow_forward_starting = allow_forward_starting;
    }

    @Column(length = 120, unique = true, nullable = false)
    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    @Column(length = 1, nullable = false)
    public Boolean getExchange_is_open() {
        return exchange_is_open;
    }

    public void setExchange_is_open(Boolean exchange_is_open) {
        this.exchange_is_open = exchange_is_open;
    }

    @Column(length = 1, nullable = false)
    public Boolean getIs_trading_suspended() {
        return is_trading_suspended;
    }

    public void setIs_trading_suspended(Boolean is_trading_suspended) {
        this.is_trading_suspended = is_trading_suspended;
    }

    @Column(length = 80, nullable = false)
    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    @Column(length = 80, nullable = false)
    public String getMarket_display_name() {
        return market_display_name;
    }

    public void setMarket_display_name(String market_display_name) {
        this.market_display_name = market_display_name;
    }

    @Column(length = 19, scale = 5, nullable = false)
    public BigDecimal getPip() {
        return pip;
    }

    public void setPip(BigDecimal pip) {
        this.pip = pip;
    }

    @Column(length = 120, nullable = false)
    public String getSubmarket() {
        return submarket;
    }

    public void setSubmarket(String submarket) {
        this.submarket = submarket;
    }

    @Column(length = 120, nullable = false)
    public String getSubmarket_display_name() {
        return submarket_display_name;
    }

    public void setSubmarket_display_name(String submarket_display_name) {
        this.submarket_display_name = submarket_display_name;
    }

    @Column(length = 60, nullable = false, unique = true)
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Column(length = 60, nullable = false)
    public String getSymbol_type() {
        return symbol_type;
    }

    public void setSymbol_type(String symbol_type) {
        this.symbol_type = symbol_type;
    }

    @Column(length = 1, nullable = false)
    public Integer getTickTime() {
        return tickTime;
    }

    public void setTickTime(Integer tickTime) {
        this.tickTime = tickTime;
    }

    @Override
    public String toString() {
        return String.format("%s", getDisplay_name().replace(" Index", ""));
    }

    public String transictionToString() {
        return String.format("%s", getSymbol());
    }

    //    @Override
//    public String toString() {
//        return "ActiveSymbol{" +
//                "id=" + id +
//                ", allow_forward_starting=" + allow_forward_starting +
//                ", display_name='" + display_name + '\'' +
//                ", exchange_is_open=" + exchange_is_open +
//                ", is_trading_suspended=" + is_trading_suspended +
//                ", market='" + market + '\'' +
//                ", market_display_name='" + market_display_name + '\'' +
//                ", pip=" + pip +
//                ", submarket='" + submarket + '\'' +
//                ", submarket_display_name='" + submarket_display_name + '\'' +
//                ", symbol='" + symbol + '\'' +
//                ", symbol_type='" + symbol_type + '\'' +
//                '}';
//    }
}
