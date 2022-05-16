package io.github.ctlove0523.auth.jwt.samples;

import io.github.ctlove0523.auth.jwt.core.DefaultIdentity;
import io.github.ctlove0523.auth.jwt.core.Identity;
import io.github.ctlove0523.auth.jwt.core.TokenClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AuthJwtController {

    @Autowired
    private TokenClient tokenClient;

    @RequestMapping(value = "/token",method = RequestMethod.GET)
    public ResponseEntity<String> getToken() {
        Identity identity = new DefaultIdentity();
        identity.setId("client");
        String token = tokenClient.getToken(identity);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @RequestMapping(value = "/application",method = RequestMethod.GET)
    public ResponseEntity<String> getApplication() {

        return new ResponseEntity<>("token", HttpStatus.OK);
    }
}
