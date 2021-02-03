package it.register.edu.auction.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.register.edu.auction.entity.User;
import it.register.edu.auction.service.UserSessionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("AdminController")
@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

  @Mock
  private UserSessionService mockService;

  @InjectMocks
  private AdminController controller;

  @Test
  @DisplayName("invokes UserSessionService and returns the result")
  void createUser() {
    User user = new User();
    when(mockService.createUser(user)).thenReturn(user);

    User result = controller.createUser(user);

    assertEquals(user, result);
    verify(mockService).createUser(user);
  }
}