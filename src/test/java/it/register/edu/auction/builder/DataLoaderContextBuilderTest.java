package it.register.edu.auction.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import graphql.kickstart.execution.context.DefaultGraphQLContext;
import graphql.kickstart.execution.context.GraphQLContext;
import graphql.kickstart.servlet.context.DefaultGraphQLServletContext;
import graphql.kickstart.servlet.context.DefaultGraphQLWebSocketContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;
import org.dataloader.DataLoaderRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("DataLoaderContextBuilder")
class DataLoaderContextBuilderTest {

  private static final DataLoaderRegistry REGISTRY = new DataLoaderRegistry();

  @Mock
  private DataLoaderRegistryBuilder mockDataLoaderRegistryBuilder;

  @InjectMocks
  private DataLoaderContextBuilder builder;

  @BeforeEach
  void setUp() {
    when(mockDataLoaderRegistryBuilder.build()).thenReturn(REGISTRY);
  }

  @Nested
  @DisplayName("when invoked over an HttpServletRequest")
  class WithHttpServletRequest {

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Test
    @DisplayName("injects the data loader registry builder")
    void build() {
      GraphQLContext result = builder.build(mockRequest, mockResponse);
      assertTrue(result instanceof DefaultGraphQLServletContext);
      assertTrue(result.getDataLoaderRegistry().isPresent());
      assertEquals(REGISTRY, result.getDataLoaderRegistry().get());
    }
  }

  @Nested
  @DisplayName("when invoked over an HandshakeRequest")
  class WithHandshakeRequest {

    @Mock
    private HandshakeRequest mockRequest;

    @Mock
    private Session mockSession;

    @Test
    @DisplayName("injects the data loader registry builder")
    void build() {
      GraphQLContext result = builder.build(mockSession, mockRequest);
      assertTrue(result instanceof DefaultGraphQLWebSocketContext);
      assertTrue(result.getDataLoaderRegistry().isPresent());
      assertEquals(REGISTRY, result.getDataLoaderRegistry().get());
    }
  }

  @Nested
  @DisplayName("when invoked without a request")
  class WithoutRequest {

    @Test
    @DisplayName("injects the data loader registry builder")
    void build() {
      GraphQLContext result = builder.build();
      assertTrue(result instanceof DefaultGraphQLContext);
      assertTrue(result.getDataLoaderRegistry().isPresent());
      assertEquals(REGISTRY, result.getDataLoaderRegistry().get());
    }
  }
}