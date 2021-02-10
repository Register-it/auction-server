package it.register.edu.auction.test.integration.subscription;

import static it.register.edu.auction.service.UserSessionService.ROLE_AUTHENTICATED;
import static it.register.edu.auction.test.integration.subscription.AuctionEventSubscriptionTest.SUBSCRIBED_USER_ID;

import it.register.edu.auction.entity.User;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Set;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Profile("test")
@ConditionalOnProperty(value = "spring.security.disabled", havingValue = "true")
public class MockUserInjector {

  @Before("execution(* it.register.edu.auction.resolver.root.SubscriptionResolver.*(..)) && @annotation(org.springframework.security.access.annotation.Secured)")
  public void beforeSecuredSubscriptionResolverMethod() throws MalformedURLException {
    User principal = User.builder().id(SUBSCRIBED_USER_ID).build();
    Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(ROLE_AUTHENTICATED));
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, authorities));
  }

}
