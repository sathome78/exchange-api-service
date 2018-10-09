package me.exrates.openapi.controllers;

import me.exrates.model.OpenApiToken;
import me.exrates.model.dto.openAPI.OpenApiTokenPublicDto;
import me.exrates.openapi.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/token/settings")
public class TokenSettingsController {

    private final TokenService openApiTokenService;

    @Autowired
    public TokenSettingsController(TokenService openApiTokenService) {
        this.openApiTokenService = openApiTokenService;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RedirectView tokenCreate(@RequestParam String alias, RedirectAttributes redirectAttributes,
                                    Principal principal) {
        try {
            RedirectView redirectView = new RedirectView("/settings/token/created");
            OpenApiToken token = openApiTokenService.generateToken(principal.getName(), alias);
            redirectAttributes.addFlashAttribute("publicKey", token.getPublicKey());
            redirectAttributes.addFlashAttribute("privateKey", token.getPrivateKey());
            return redirectView;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("msg", e.getMessage());
            return new RedirectView("/settings");
        }
    }


    @ResponseBody
    @RequestMapping(value = "/findAll", method = GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<OpenApiTokenPublicDto> getUserTokens(Principal principal) {
        return openApiTokenService.getUserTokens(principal.getName());
    }

    @RequestMapping(value = "/created", method = RequestMethod.GET)
    public ModelAndView tokenCreated(HttpServletRequest request) {
        Map<String, ?> flashAttributes = RequestContextUtils.getInputFlashMap(request);
        if (flashAttributes == null || !(flashAttributes.containsKey("publicKey") && flashAttributes.containsKey("privateKey"))) {
            return new ModelAndView("redirect:/settings");
        }
        return new ModelAndView("globalPages/tokenKey");
    }

    @ResponseBody
    @RequestMapping(value = "/allowTrade", method = POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void allowTrade(@RequestParam Long tokenId, @RequestParam Boolean allowTrade, Principal principal) {
        openApiTokenService.updateToken(tokenId, allowTrade, principal.getName());
    }

    @ResponseBody
    @RequestMapping(value = "/delete", method = POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void deleteToken(@RequestParam Long tokenId, Principal principal) {
        openApiTokenService.deleteToken(tokenId, principal.getName());
    }


}
