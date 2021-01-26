package it.register.edu.auction.config;

import it.register.edu.auction.util.CookieUtils;
import javax.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DevCookieConfig {

  @PostConstruct
  public void setCookieSecure() {
    CookieUtils.setSecure(false);
  }

}
