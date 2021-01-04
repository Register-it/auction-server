package it.register.edu.auction.config;

import it.register.edu.auction.filter.TokenAuthorizationFilter;
import it.register.edu.auction.service.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private UserSessionService userSessionService;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .addFilter(new TokenAuthorizationFilter(authenticationManager(), userSessionService))
        .authorizeRequests()
        .antMatchers("/", "/graphql", "/playground", "/vendor/playground/**/*").permitAll()
        .anyRequest().authenticated()
        .and()
        .csrf().disable();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
