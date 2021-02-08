package it.register.edu.auction.test.integration;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import it.register.edu.auction.exception.GraphQLDataFetchingException;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class CustomGraphQLError implements GraphQLError {

  private String message;
  private List<SourceLocation> locations;
  private ErrorClassification errorType;
  private List<Object> path;
  private Map<String, Object> extensions;

  public String getErrorCode() {
    return (String) getExtensions().get(GraphQLDataFetchingException.ERROR_CODE_EXTENSION);
  }
}
