package it.register.edu.auction.resolver.field;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import graphql.kickstart.execution.context.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import java.util.Optional;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
abstract class WithDataLoader {

  @Mock(lenient = true)
  protected DataFetchingEnvironment mockEnvironment;

  @Mock(lenient = true)
  protected GraphQLContext mockContext;

  @Mock(lenient = true)
  protected DataLoaderRegistry mockDataLoaderRegistry;

  @Mock
  protected DataLoader<Object, Object> mockDataLoader;

  @BeforeEach
  void setUp() {
    when(mockEnvironment.getContext()).thenReturn(mockContext);
    when(mockContext.getDataLoaderRegistry()).thenReturn(Optional.of(mockDataLoaderRegistry));
    when(mockDataLoaderRegistry.getDataLoader(anyString())).thenReturn(mockDataLoader);
  }
}