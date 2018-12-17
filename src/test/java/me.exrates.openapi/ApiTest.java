package me.exrates.openapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.openapi.model.HmacSignature;
import me.exrates.openapi.model.dto.CallbackURL;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ExratesApiServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(locations = "classpath:dao.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ApiTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    RestTemplate restTemplate = new RestTemplate();

    String pub_key = "dyFh1UGDZJomAhQahaE63WNoRjlRLS969IpO2ykE";
    String priv_key = "AnL7vZlcfyIYAzLTrrQhWKigSmdlm7OHGwnTpXHb";

    @Autowired
    @Before
    public void setUp(){

        String email = "mikita.malykov@upholding.biz";
        jdbcTemplate.update("INSERT INTO USER ( nickname, email, password, regdate, phone, finpassword, status, ipaddress, roleid, preferred_lang, avatar_path, tmp_poll_passed, login_pin, use2fa, `2fa_last_notify_date`, withdraw_pin, transfer_pin, reset_password_date, change_2fa_setting_pin) VALUES ('dudoser228', '" + email + "', '$2a$10$I9zD5k7OUtqN4G62L7X7WuN0jM9i9NnT1RckDzkFZP1b/7nB4yGSC', '2018-08-28 14:23:02', '', null, 2, '', 1, 'en', null, 1, '$2a$10$uxXSXZVXJn1I/S5WP2QjEOSbkcQMuWgeMtsf3VEkG.sCDJw2xaoMu', 0, '2018-10-12 03:00:00', '$2a$10$7eAZvix3gsV3AV6fEdv2J.Vl.13FOGumwwwSoV.0FjaPPfhNo5y4.', '$2a$10$I9zD5k7OUtqN4G62L7X7WuN0jM9i9NnT1RckDzkFZP1b/7nB4yGSC', null, null)");
        Long id = jdbcTemplate.queryForObject("SELECT id FROM USER WHERE email = " + "'" + email + "'", Long.class);
        jdbcTemplate.update("INSERT INTO OPEN_API_USER_TOKEN (user_id, alias, public_key, private_key, date_generation, is_active, allow_trade, allow_withdraw) VALUES (" + id + ", 'trololo', '" + pub_key + "', '" + priv_key + "', '2018-12-07 17:28:04', 1, 1, 0);");
    }
    
    @Test
    public void callBack() throws Exception {
        Date timestamp = new Date();

        HmacSignature signature = new HmacSignature.Builder()
                .algorithm("HmacSHA256")
                .delimiter("|")
                .apiSecret(priv_key)
                .endpoint("/openapi/v1/orders/callback/add")
                .requestMethod("POST")
                .timestamp(timestamp.getTime())
                .publicKey(pub_key).build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add("API-KEY", pub_key);
        headers.add("API-TIME", String.valueOf(timestamp.getTime()));
        headers.add("API-SIGN", signature.getSignatureHexString());

        CallbackURL callbackURL = new CallbackURL();
        callbackURL.setCallbackURL("dwadaw");
        callbackURL.setPairId(11);
        HttpEntity<String> entity = new HttpEntity<String>(new ObjectMapper().writeValueAsString(callbackURL), headers);

        //todo assert call not exists

        ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity("http://localhost:8080/openapi/v1/orders/callback/add", entity, String.class);
        String s = stringResponseEntity.toString();
        System.out.println(stringResponseEntity.toString());

        //todo assert call were added
    }


}