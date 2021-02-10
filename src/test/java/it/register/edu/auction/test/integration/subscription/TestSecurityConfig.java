package it.register.edu.auction.test.integration.subscription;

import it.register.edu.auction.auth.TokenAuthenticationProvider;
import it.register.edu.auction.auth.TokenAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Profile("test")
@Configuration
@ConditionalOnProperty(value = "spring.security.disabled", havingValue = "true")
public class TestSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private TokenAuthenticationProvider tokenAuthenticationProvider;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .addFilter(new TokenAuthorizationFilter(authenticationManager(), tokenAuthenticationProvider))
        .authorizeRequests()
        .antMatchers("/", "/graphql", "/subscriptions", "/admin/**/*", "/playground", "/vendor/playground/**/*").permitAll()
        .anyRequest().authenticated()
        .and()
        .csrf().disable();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
