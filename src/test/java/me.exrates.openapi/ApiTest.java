package me.exrates.openapi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.openapi.model.HmacSignature;
import me.exrates.openapi.model.dto.CallbackURL;
import me.exrates.openapi.model.dto.openAPI.TickerJsonDto;
import me.exrates.openapi.service.UserSettingService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExratesApiServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(locations = "classpath:dao.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ApiTest {

    private static final String PUB_KEY = "dyFh1UGDZJomAhQahaE63WNoRjlRLS969IpO2ykE";
    private static final String PRIV_KEY = "AnL7vZlcfyIYAzLTrrQhWKigSmdlm7OHGwnTpXHb";
    private int id;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserSettingService userSettingService;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Before
    public void setUp() {
        String email = "mikita.malykov@upholding.biz";
        int update = jdbcTemplate.update("INSERT INTO USER ( nickname, email, password, regdate, phone, finpassword, status, ipaddress, roleid, preferred_lang, avatar_path, tmp_poll_passed, login_pin, use2fa, `2fa_last_notify_date`, withdraw_pin, transfer_pin, reset_password_date, change_2fa_setting_pin) VALUES ('dudoser228', '" + email + "', '$2a$10$I9zD5k7OUtqN4G62L7X7WuN0jM9i9NnT1RckDzkFZP1b/7nB4yGSC', '2018-08-28 14:23:02', '', null, 2, '', 1, 'en', null, 1, '$2a$10$uxXSXZVXJn1I/S5WP2QjEOSbkcQMuWgeMtsf3VEkG.sCDJw2xaoMu', 0, '2018-10-12 03:00:00', '$2a$10$7eAZvix3gsV3AV6fEdv2J.Vl.13FOGumwwwSoV.0FjaPPfhNo5y4.', '$2a$10$I9zD5k7OUtqN4G62L7X7WuN0jM9i9NnT1RckDzkFZP1b/7nB4yGSC', null, null)");
        id = jdbcTemplate.queryForObject("SELECT id FROM USER WHERE email = " + "'" + email + "'", Integer.class);
        jdbcTemplate.update("INSERT INTO OPEN_API_USER_TOKEN (user_id, alias, public_key, private_key, date_generation, is_active, allow_trade, allow_withdraw) VALUES (" + id + ", 'trololo', '" + PUB_KEY + "', '" + PRIV_KEY + "', '2018-12-07 17:28:04', 1, 1, 0);");
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithUserDetails("denis@denis.com")
    public void callBack() throws Exception {

        Date timestamp = new Date();

        HmacSignature signature = new HmacSignature.Builder()
                .algorithm("HmacSHA256")
                .delimiter("|")
                .apiSecret(PRIV_KEY)
                .endpoint("/openapi/v1/orders/callback/add")
                .requestMethod("POST")
                .timestamp(timestamp.getTime())
                .publicKey(PUB_KEY).build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add("API-KEY", PUB_KEY);
        headers.add("API-TIME", String.valueOf(timestamp.getTime()));
        headers.add("API-SIGN", signature.getSignatureHexString());

        CallbackURL callbackURL = new CallbackURL();
        String callBackUrl = "dwadaw";
        callbackURL.setCallbackURL(callBackUrl);
        int currencyId = 11;
        callbackURL.setPairId(currencyId);

        assertNull(userSettingService.getCallbackURL(id, currencyId));

        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(callbackURL), headers);

        //todo assert call not exists

//        ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity("http://localhost:8080/openapi/v1/orders/callback/add", entity, String.class);


        ResultActions resultActions = mvc.perform(post("/openapi/v1/orders/callback/add")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .headers(headers)
                .content(new ObjectMapper().writeValueAsString(callBackUrl))
        ).andExpect(status().isOk());

        String response = resultActions.andReturn().getResponse().getContentAsString();
        assert userSettingService.getCallbackURL(id, currencyId).equals(callBackUrl);
    }

    @Test
    public void test() throws Exception {

        Date timestamp = new Date();

        HmacSignature signature = new HmacSignature.Builder()
                .algorithm("HmacSHA256")
                .delimiter("|")
                .apiSecret(PRIV_KEY)
                .endpoint("/openapi/v1/orders/callback/add")
                .requestMethod("POST")
                .timestamp(timestamp.getTime())
                .publicKey(PUB_KEY).build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add("API-KEY", PUB_KEY);
        headers.add("API-TIME", String.valueOf(timestamp.getTime()));
        headers.add("API-SIGN", signature.getSignatureHexString());

        MvcResult mvcResult = mvc.perform(get("/openapi/v1/public/ticker")
                .param("currency_pair", "btc_usd")
                .headers(headers))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        List<TickerJsonDto> list = objectMapper.readValue(response, new TypeReference<List<TickerJsonDto>>() {
        });
    }
}