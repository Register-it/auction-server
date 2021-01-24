package it.register.edu.auction.admin;

import it.register.edu.auction.entity.User;
import it.register.edu.auction.service.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("dev")
@RestController
@RequestMapping("/admin")
public class AdminController {

  @Autowired
  private UserSessionService userSessionService;

  @PostMapping("/user")
  public User createUser(@RequestBody User user) {
    return userSessionService.createUser(user);
  }
}
