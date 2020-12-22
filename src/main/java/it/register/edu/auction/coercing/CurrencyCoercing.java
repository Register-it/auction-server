package it.register.edu.auction.coercing;

import graphql.language.FloatValue;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CurrencyCoercing implements Coercing<BigDecimal, Float> {

  @Override
  public Float serialize(Object dataFetcherResult) {
    try {
      return ((BigDecimal) dataFetcherResult).floatValue();
    } catch (Exception e) {
      throw new CoercingSerializeException(e);
    }
  }

  @Override
  public BigDecimal parseValue(Object input) {
    try {
      return BigDecimal.valueOf((Float) input);
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
