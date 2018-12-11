package me.exrates.service;

import me.exrates.model.Email;

public interface SendMailService {

	void sendMail(Email email);

    void sendMailMandrill(Email email);

    void sendInfoMail(Email email);

}
