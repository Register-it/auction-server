package it.register.edu.auction.builder;

import graphql.kickstart.execution.context.DefaultGraphQLContext;
import graphql.kickstart.execution.context.GraphQLContext;
import graphql.kickstart.servlet.context.DefaultGraphQLServletContext;
import graphql.kickstart.servlet.context.DefaultGraphQLWebSocketContext;
import graphql.kickstart.servlet.context.GraphQLServletContextBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataLoaderContextBuilder implements GraphQLServletContextBuilder {

  @Autowired
  private DataLoaderRegistryBuilder registryBuilder;

  @Override
  public GraphQLContext build(HttpServletRequest req, HttpServletResponse res) {
    return DefaultGraphQLServletContext.createServletContext(registryBuilder.build(), null)
        .with(req)
        .with(res)
        .build();
  }

  @Override
  public GraphQLContext build(Session session, HandshakeRequest handshakeRequest) {
    return DefaultGraphQLWebSocketContext.createWebSocketContext(registryBuilder.build(), null)
        .with(session)
        .with(handshakeRequest)
        .build();
  }

  @Override
  public GraphQLContext build() {
    return new DefaultGraphQLContext(registryBuilder.build(), null);
  }

}
