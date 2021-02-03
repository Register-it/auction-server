package it.register.edu.auction.coercing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("DateTimeCoercing")
class DateTimeCoercingTest {

  private static final String DATE = "2027-06-13T12:30:00";
  
  private DateTimeCoercing dateTimeCoercing;

  @BeforeEach
  void setUp() {
    dateTimeCoercing = new DateTimeCoercing();
  }

  @Nested
  @DisplayName("when fetching data from GraphQL server")
  class WhenFetching {

    @Test
    @DisplayName("serializes a LocalDateTime as a string in ISO format")
    void serializeLocalDateTimeToISOString() {
      assertEquals(DATE, dateTimeCoercing.serialize(LocalDateTime.parse(DATE)));
    }

    @Test
    @DisplayName("truncates to seconds the given LocalDateTime before serialize")
    void serializeTruncatesToSeconds() {
      assertEquals(DATE, dateTimeCoercing.serialize(LocalDateTime.parse(DATE + ".1234")));
    }

    @Test
    @DisplayName("throws exception if the given object cannot be cast to LocalDateTime")
    void serializeThrowsException() {
      Date date = new Date();
      assertThrows(CoercingSerializeException.class, () -> dateTimeCoercing.serialize(""));
      assertThrows(CoercingSerializeException.class, () -> dateTimeCoercing.serialize(date));
      assertThrows(CoercingSerializeException.class, () -> dateTimeCoercing.serialize(null));
    }
  }

  @Nested
  @DisplayName("when parsing a value from a GraphQL input")
  class WhenParsingValue {

    @Test
    @DisplayName("converts an ISO formatted string to a LocalDateTime")
    void parseValueISOStringToLocalDateTime() {
      assertEquals(LocalDateTime.parse(DATE), dateTimeCoercing.parseValue(DATE));
    }

    @Test
    @DisplayName("throws exception if the input string is not in ISO format")
    void parseValueThrowsExceptionForInvalidFormat() {
      assertThrows(CoercingParseValueException.class, () -> dateTimeCoercing.parseValue(""));
      assertThrows(CoercingParseValueException.class, () -> dateTimeCoercing.parseValue("13/06/2027 12:30"));
    }

    @Test
    @DisplayName("throws exception if the input is not a string")
    void parseValueThrowsExceptionForInvalidType() {
      Object object = new Object();
      assertThrows(CoercingParseValueException.class, () -> dateTimeCoercing.parseValue(123));
      assertThrows(CoercingParseValueException.class, () -> dateTimeCoercing.parseValue(object));
      assertThrows(CoercingParseValueException.class, () -> dateTimeCoercing.parseValue(null));
    }
  }

  @Nested
  @DisplayName("when parsing a literal from a GraphQL input")
  class WhenParsingLiteral {

    @Test
    @DisplayName("converts a StringValue to a LocalDateTime")
    void parseLiteralStringValueToLocalDateTime() {
      assertEquals(LocalDateTime.parse(DATE), dateTimeCoercing.parseLiteral(new StringValue(DATE)));
    }

    @Test
    @DisplayName("throws exception if the StringValue input is not in ISO format")
    void parseLiteralThrowsExceptionForInvalidFormat() {
      StringValue stringValue = new StringValue("13/06/2027");
      assertThrows(CoercingParseLiteralException.class, () -> dateTimeCoercing.parseLiteral(stringValue));
    }

    @Test
    @DisplayName("throws exception if the given input is not a StringValue")
    void parseLiteralThrowsExceptionForInvalidType() {
      IntValue intValue = new IntValue(new BigInteger("123"));
      Object object = new Object();
      assertThrows(CoercingParseLiteralException.class, () -> dateTimeCoercing.parseLiteral(intValue));
      assertThrows(CoercingParseLiteralException.class, () -> dateTimeCoercing.parseLiteral(object));
      assertThrows(CoercingParseLiteralException.class, () -> dateTimeCoercing.parseLiteral(null));
    }
  }
}