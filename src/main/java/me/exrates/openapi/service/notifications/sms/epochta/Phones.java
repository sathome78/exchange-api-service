package me.exrates.openapi.service.notifications.sms.epochta;

import lombok.Data;

@Data
public class Phones {

    private String idMessage;
    private String varaibles;
    private String phone;

    public Phones(String idMessage, String variables, String phone) {
        this.phone = phone;
        this.varaibles = variables;
        this.idMessage = idMessage;
    }
}
