package me.exrates.openapi.service;

import me.exrates.openapi.model.Email;

public interface SendMailService {

	void sendMail(Email email);

    void sendMailMandrill(Email email);

    void sendInfoMail(Email email);

}
