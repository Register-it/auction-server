package it.register.edu.auction.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import it.register.edu.auction.annotation.GraphQLDataLoader;
import java.util.Map;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

@DisplayName("DataLoaderRegistryBuilder")
@ExtendWith(MockitoExtension.class)
class DataLoaderRegistryBuilderTest {

  private static final String DATALOADER1 = "dataLoader1";
  private static final String DATALOADER2 = "dataLoader2";
  @Mock
  private ApplicationContext mockApplicationContext;

  @Mock
  private DataLoaderBuilder<Integer, Integer> mockDataLoaderBuilder1;

  @Mock
  private DataLoaderBuilder<Integer, String> mockDataLoaderBuilder2;

  @Mock
  private DataLoader<Integer, Integer> mockDataLoader1;

  @Mock
  private DataLoader<Integer, String> mockDataLoader2;

  @InjectMocks
  private DataLoaderRegistryBuilder builder;

  @Test
  @DisplayName("creates a registry containing all the data loaders defined in the application context")
  void build() {
    when(mockDataLoaderBuilder1.build()).thenReturn(mockDataLoader1);
    when(mockDataLoaderBuilder2.build()).thenReturn(mockDataLoader2);

    Map<String, Object> beans = Map.of(DATALOADER1, mockDataLoaderBuilder1, DATALOADER2, mockDataLoaderBuilder2);
    when(mockApplicationContext.getBeansWithAnnotation(GraphQLDataLoader.class)).thenReturn(beans);

    DataLoaderRegistry result = builder.build();

    assertEquals(2, result.getDataLoaders().size());
    assertEquals(mockDataLoader1, result.getDataLoader(DATALOADER1));
    assertEquals(mockDataLoader2, result.getDataLoader(DATALOADER2));
  }
}