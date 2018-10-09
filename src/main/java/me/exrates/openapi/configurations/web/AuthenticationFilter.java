package me.exrates.openapi.configurations.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.exrates.openapi.controllers.advice.OpenApiError;
import me.exrates.openapi.exceptions.MissingAuthHeaderException;
import me.exrates.openapi.models.enums.ErrorCode;
import me.exrates.openapi.services.AuthenticationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.util.Objects.isNull;

@Slf4j
public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String HEADER_PUBLIC_KEY = "API-KEY";
    private static final String HEADER_TIMESTAMP = "API-TIME";
    private static final String HEADER_SIGNATURE = "API-SIGN";

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private ObjectMapper objectMapper;

    public AuthenticationFilter(String defaultFilterProcessesUrl,
                                AuthenticationManager authenticationManager) {
        super(defaultFilterProcessesUrl);
        super.setAuthenticationManager(authenticationManager);
        setAuthenticationSuccessHandler((request, response, authentication) -> {
            String pathInfo = isNull(request.getPathInfo())
                    ? StringUtils.EMPTY
                    : request.getPathInfo();
            request
                    .getRequestDispatcher(request.getServletPath() + pathInfo)
                    .forward(request, response);
        });
        setAuthenticationFailureHandler((request, response, authenticationException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            ErrorCode errorCode = ErrorCode.FAILED_AUTHENTICATION;
            OpenApiError apiError = new OpenApiError(errorCode, request.getServletPath(), authenticationException);
            String responseString = objectMapper.writeValueAsString(apiError);
            ServletOutputStream out = response.getOutputStream();
            out.print(responseString);
            out.flush();
        });
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        final String publicKey = request.getHeader(HEADER_PUBLIC_KEY);
        final String timestampString = request.getHeader(HEADER_TIMESTAMP);
        final String signatureHex = request.getHeader(HEADER_SIGNATURE);

        if (isNull(publicKey) || isNull(timestampString) || isNull(signatureHex)) {
            throw new MissingAuthHeaderException(String.format("One of required headers missing. Required headers: %s",
                    String.join(", ", HEADER_PUBLIC_KEY, HEADER_TIMESTAMP, HEADER_SIGNATURE)));
        }

        var userDetails = authenticationService.getUserByPublicKey(request.getMethod(),
                request.getServletPath(),
                Long.parseLong(timestampString),
                publicKey,
                signatureHex);

        var authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authentication;
    }
}
