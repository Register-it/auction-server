package it.register.edu.auction.test;

import static it.register.edu.auction.service.UserSessionService.ROLE_AUTHENTICATED;

import it.register.edu.auction.entity.User;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticationExtension implements BeforeEachCallback, AfterEachCallback, BeforeTestExecutionCallback {

  @Override
  public void beforeEach(ExtensionContext extensionContext) {
    setAuthentication(null);
  }

  @Override
  public void beforeTestExecution(ExtensionContext extensionContext) {
    extensionContext.getElement()
        .flatMap(el -> Optional.ofNullable(el.getAnnotation(WithAuthenticatedUser.class)))
        .ifPresent(this::setAuthenticatedUser);
  }

  @Override
  public void afterEach(ExtensionContext extensionContext) {
    setAuthentication(null);
  }

  private void setAuthenticatedUser(WithAuthenticatedUser annotation) {
    User user = User.builder().id(annotation.id()).build();
    List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(ROLE_AUTHENTICATED));
    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, annotation.token(), authorities);
    setAuthentication(auth);
  }

  private void setAuthentication(Authentication auth) {
    SecurityContextHolder.getContext().setAuthentication(auth);
  }
}
