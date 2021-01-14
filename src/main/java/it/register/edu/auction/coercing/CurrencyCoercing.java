package it.register.edu.auction.coercing;

import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import java.math.BigDecimal;

public class CurrencyCoercing implements Coercing<BigDecimal, Double> {

  @Override
  public Double serialize(Object dataFetcherResult) {
    BigDecimal result = convert(dataFetcherResult);
    if (result == null) {
      throw new CoercingSerializeException("Expected type 'BigDecimal' but was '" + typeName(dataFetcherResult) + "'.");
    }
    return result.doubleValue();
  }

  @Override
  public BigDecimal parseValue(Object input) {
    BigDecimal result = convert(input);
    if (result == null) {
      throw new CoercingParseValueException("Expected type 'Float' but was '" + typeName(input) + "'.");
    }
    return result;
  }

  @Override
  public BigDecimal parseLiteral(Object input) {
    if (input instanceof IntValue) {
      return new BigDecimal(((IntValue) input).getValue());
    } else if (input instanceof FloatValue) {
      return ((FloatValue) input).getValue();
    }
    throw new CoercingParseLiteralException("Expected AST type 'IntValue' or 'FloatValue' but was '" + typeName(input) + "'.");
  }

  private BigDecimal convert(Object input) {
    if (isNumber(input)) {
      try {
        return new BigDecimal(input.toString());
      } catch (NumberFormatException e) {
        return null;
      }
    }
    return null;
  }

  private boolean isNumber(Object input) {
    return input instanceof Number || input instanceof String;
  }

  private String typeName(Object input) {
    if (input == null) {
      return "null";
    }

    return input.getClass().getSimpleName();
  }
}
