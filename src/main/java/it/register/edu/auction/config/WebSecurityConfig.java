package it.register.edu.auction.config;

import it.register.edu.auction.filter.TokenAuthorizationFilter;
import it.register.edu.auction.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private TokenService tokenService;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .addFilter(new TokenAuthorizationFilter(authenticationManager(), tokenService))
        .authorizeRequests()
        .antMatchers("/", "/graphql", "/playground", "/vendor/playground/**/*").permitAll()
        .anyRequest().authenticated()
        .and()
        .csrf().disable();
  }

}
