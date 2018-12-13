package me.exrates.model;

/**
 * Created by OLEG on 13.02.2017.
 */
public class ClientBank {

    private Integer id;
    private Integer currencyId;
    private String name;
    private String code;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "CurrencyInputBank{" +
                "id=" + id +
                ", currencyId=" + currencyId +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
