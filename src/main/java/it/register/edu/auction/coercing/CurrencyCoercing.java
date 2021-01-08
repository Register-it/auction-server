package it.register.edu.auction.coercing;

import graphql.language.FloatValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import java.math.BigDecimal;

public class CurrencyCoercing implements Coercing<BigDecimal, Double> {

  @Override
  public Double serialize(Object dataFetcherResult) {
    try {
      return ((BigDecimal) dataFetcherResult).doubleValue();
    } catch (Exception e) {
      throw new CoercingSerializeException(e);
    }
  }

  @Override
  public BigDecimal parseValue(Object input) {
    try {
      return BigDecimal.valueOf((Double) input);
    } catch (Exception e) {
      throw new CoercingParseValueException(e);
    }
  }

  @Override
  public BigDecimal parseLiteral(Object input) {
    try {
      return ((FloatValue) input).getValue();
    } catch (Exception e) {
      throw new CoercingParseLiteralException(e);
    }
  }
}
