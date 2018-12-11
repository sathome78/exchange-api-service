package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Email;
import me.exrates.model.enums.EmailSenderType;
import me.exrates.service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PreDestroy;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@Log4j2(topic = "email_log")
@Service
@PropertySource(value = {"classpath:/mail.properties"})
public class SendMailServiceImpl implements SendMailService {

	@Autowired
	@Qualifier("SupportMailSender")
	private JavaMailSender supportMailSender;

	@Autowired
	@Qualifier("MandrillMailSender")
	private JavaMailSender mandrillMailSender;

	@Autowired
	@Qualifier("InfoMailSender")
	private JavaMailSender infoMailSender;
	
	@Value("${mail_info.allowedOnly}")
	private Boolean allowedOnly;
	
	@Value("${mail_info.allowedEmails}")
	private String allowedEmailsList;

	@Value("${default_mail_type}")
	private String mailType;

	private final static int THREADS_NUMBER = 8;
	private final static ExecutorService executors = Executors.newFixedThreadPool(THREADS_NUMBER);
	private final static ExecutorService supportMailExecutors = Executors.newFixedThreadPool(3);

	private final String SUPPORT_EMAIL = "mail@exrates.top";
	private final String MANDRILL_EMAIL = "no-reply@exrates.me";
	private final String INFO_EMAIL = "no-reply@exrates.top";

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void sendMail(Email email){
		supportMailExecutors.execute(() -> {
			try {
				sendMail(email, SUPPORT_EMAIL, supportMailSender);
			} catch (Exception e) {
				log.error(e);
				sendMail(email, INFO_EMAIL, infoMailSender);
			}
		});
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void sendMailMandrill(Email email){
		supportMailExecutors.execute(() -> {
			try {
				sendByType(email, EmailSenderType.valueOf(mailType));
			} catch (Exception e) {
				log.error(e);
				sendMail(email, SUPPORT_EMAIL, supportMailSender);
			}
		});
	}

	private void sendByType(Email email, EmailSenderType type) {
		switch (type) {
			case gmail : {
				sendInfoMail(email);
				break;
			}
			case mandrill: {
				sendMail(email, MANDRILL_EMAIL, mandrillMailSender);
				break;
			}
		}
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	@Override
	public void sendInfoMail(Email email) {
		if (allowedOnly) {
			String[] allowedEmails = allowedEmailsList.split(",");
			if (Stream.of(allowedEmails).noneMatch(mail -> mail.equals(email.getTo()))) {
				return;
			}
		}
		executors.execute(() -> {
			try {
				sendMail(email, INFO_EMAIL, infoMailSender);
			} catch (MailException e) {
				log.error(e);
				sendMail(email, SUPPORT_EMAIL, supportMailSender);
			}
		});

	}

	private void sendMail(Email email, String fromAddress, JavaMailSender mailSender) {
		LocalDateTime start = LocalDateTime.now();
		log.debug("try to send email {} to {}, time {}", email.getSubject(), email.getTo());
		email.setFrom(fromAddress);
		try {
			mailSender.send(mimeMessage -> {
                MimeMessageHelper message;
                message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                message.setFrom(email.getFrom());
                message.setTo(email.getTo());
                message.setSubject(email.getSubject());
                message.setText(email.getMessage(), true);
                if (email.getAttachments() != null) {
                    for (Email.Attachment attachment : email.getAttachments())
                        message.addAttachment(attachment.getName(), attachment.getResource(), attachment.getContentType());
                }
            });
			log.info("Email sent: {}, duration {} seconds", email, Duration.between(start, LocalDateTime.now()).getSeconds());
		} catch (Exception e) {
			log.error("Could not send email {}. Reason: {}", email, e.getMessage());
		}

	}


	@PreDestroy
	public void destroy() {
		executors.shutdown();
		supportMailExecutors.shutdown();
	}


}
