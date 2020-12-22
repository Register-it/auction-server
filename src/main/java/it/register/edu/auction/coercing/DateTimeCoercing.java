package it.register.edu.auction.coercing;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateTimeCoercing implements Coercing<LocalDateTime, String> {

  @Override
  public String serialize(Object dataFetcherResult) {
    try {
      return ((LocalDateTime) dataFetcherResult).truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    } catch (Exception e) {
      throw new CoercingSerializeException(e);
    }
  }

  @Override
  public LocalDateTime parseValue(Object input) {
    try {
      return LocalDateTime.parse((String) input, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    } catch (Exception e) {
      throw new CoercingParseValueException(e);
    }
  }

  @Override
  public LocalDateTime parseLiteral(Object input) {
    try {
      return LocalDateTime.parse(((StringValue) input).getValue(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    } catch (Exception e) {
      throw new CoercingParseLiteralException(e);
    }
  }
}
