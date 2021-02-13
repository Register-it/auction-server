package it.register.edu.auction.config;

import graphql.schema.GraphQLScalarType;
import it.register.edu.auction.coercing.CurrencyCoercing;
import it.register.edu.auction.coercing.DateTimeCoercing;
import it.register.edu.auction.coercing.URLCoercing;
import it.register.edu.auction.service.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphQLConfig {

  @Autowired
  private AuctionService auctionService;

  @Bean
  public GraphQLScalarType dateTimeScalar() {
    return GraphQLScalarType.newScalar()
        .name("DateTime")
        .description("DateTime type")
        .coercing(new DateTimeCoercing())
        .build();
  }

  @Bean
  public GraphQLScalarType currencyScalar() {
    return GraphQLScalarType.newScalar()
        .name("Currency")
        .description("Currency type")
        .coercing(new CurrencyCoercing())
        .build();
  }

  
  public GraphQLScalarType urlScalar() {
    return GraphQLScalarType.newScalar()
        .name("URL")
        .description("URL type")
        .coercing(new URLCoercing())
        .build();
  }

}
