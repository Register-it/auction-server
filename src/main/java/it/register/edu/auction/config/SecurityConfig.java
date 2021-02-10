package it.register.edu.auction.config;

import it.register.edu.auction.auth.TokenAuthenticationProvider;
import it.register.edu.auction.auth.TokenAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@ConditionalOnProperty(value = "spring.security.disabled", havingValue = "false", matchIfMissing = true)
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private TokenAuthenticationProvider tokenAuthenticationProvider;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .addFilter(new TokenAuthorizationFilter(authenticationManager(), tokenAuthenticationProvider))
        .authorizeRequests()
        .antMatchers(
            "/",
            "/graphql",
            "/subscriptions",
            "/admin/**/*",
            "/playground",
            "/vendor/playground/**/*",
            "/v2/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**/*",
            "/swagger-ui.html",
            "/webjars/**",
            "/rest/**/*"
        ).permitAll()
        .anyRequest().authenticated()
        .and()
        .csrf().disable();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
