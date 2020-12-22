package it.register.edu.auction.coercing;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import java.net.URL;

public class URLCoercing implements Coercing<URL, String> {

  @Override
  public String serialize(Object dataFetcherResult) {
    try {
      return dataFetcherResult.toString();
    } catch (Exception e) {
      throw new CoercingSerializeException(e);
    }
  }

  @Override
  public URL parseValue(Object input) {
    try {
      return new URL((String) input);
    } catch (Exception e) {
      throw new CoercingParseValueException(e);
    }
  }

  @Override
  public URL parseLiteral(Object input) {
    try {
      return new URL(((StringValue) input).getValue());
    } catch (Exception e) {
      throw new CoercingParseLiteralException(e);
    }
  }
}
