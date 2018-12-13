package me.exrates.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.security.OpenApiAuthService;
import me.exrates.security.exception.MissingAuthHeaderException;
import me.exrates.service.exception.api.ErrorCode;
import me.exrates.service.exception.api.OpenApiError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2(topic = "open_api")
public class OpenApiAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String HEADER_PUBLIC_KEY = "API-KEY";
    private static final String HEADER_TIMESTAMP = "API-TIME";
    private static final String HEADER_SIGNATURE = "API-SIGN";

    @Autowired
    OpenApiAuthService openApiAuthService;


    public OpenApiAuthenticationFilter(String defaultFilterProcessesUrl, AuthenticationManager authenticationManager) {
        super(defaultFilterProcessesUrl);
        super.setAuthenticationManager(authenticationManager);
        setAuthenticationSuccessHandler((request, response, authentication) ->
        {
            String pathInfo = request.getPathInfo() == null ? "" : request.getPathInfo();
            request.getRequestDispatcher(request.getServletPath() + pathInfo).forward(request, response);
        });
        setAuthenticationFailureHandler((request, response, authenticationException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            ErrorCode errorCode = ErrorCode.FAILED_AUTHENTICATION;
            OpenApiError apiError = new OpenApiError(errorCode, request.getRequestURL().toString(), authenticationException);
            String responseString = new ObjectMapper().writeValueAsString(apiError);
            ServletOutputStream out = response.getOutputStream();
            out.print(responseString);
            out.flush();
        });


    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String publicKey = request.getHeader(HEADER_PUBLIC_KEY);
        String timestampString = request.getHeader(HEADER_TIMESTAMP);
        String signatureHex = request.getHeader(HEADER_SIGNATURE);
        if (publicKey == null || timestampString == null || signatureHex == null) {
            throw new MissingAuthHeaderException("One of required headers missing. Required headers: " + String.join(", ", HEADER_PUBLIC_KEY, HEADER_TIMESTAMP, HEADER_SIGNATURE));
        }
        Long timestamp = Long.parseLong(timestampString);

        UserDetails userDetails = openApiAuthService.getUserByPublicKey(request.getMethod(), request.getServletPath(), timestamp, publicKey, signatureHex);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authentication;
    }
}
