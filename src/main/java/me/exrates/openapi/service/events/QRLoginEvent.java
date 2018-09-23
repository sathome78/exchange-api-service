package me.exrates.openapi.service.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import javax.servlet.http.HttpServletRequest;

@Getter
@Setter
public class QRLoginEvent extends ApplicationEvent {

    public QRLoginEvent(HttpServletRequest request) {
        super(request);
    }
}
