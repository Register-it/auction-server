package it.register.edu.auction.rest.domain;

import it.register.edu.auction.entity.Item;
import java.net.URL;
import java.util.List;
import lombok.Data;

@Data
public class User {

  private int id;
  private String username;
  private String password;
  private String firstName;
  private String lastName;
  private URL image;
  private List<Item> watched;
  private List<Item> bid;
  private List<Item> awarded;

  public static User fromLoggedUser(it.register.edu.auction.entity.User user) {
    User result = new User();

    result.setId(user.getId());
    result.setUsername(user.getUsername());
    result.setPassword(user.getPassword());
    result.setFirstName(user.getFirstName());
    result.setLastName(user.getLastName());
    result.setImage(user.getImage());

    return result;
  }

}
