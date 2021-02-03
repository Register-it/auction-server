package it.register.edu.auction.coercing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("CurrencyCoercing")
class CurrencyCoercingTest {

  private CurrencyCoercing currencyCoercing;

  @BeforeEach
  void setUp() {
    currencyCoercing = new CurrencyCoercing();
  }

  @Nested
  @DisplayName("when fetching data from GraphQL server")
  class WhenFetching {

    @Test
    @DisplayName("serializes any number to a double")
    void serializeNumberToDouble() {
      assertEquals(123d, currencyCoercing.serialize(123));
      assertEquals(234d, currencyCoercing.serialize(234L));
      assertEquals(345.67d, currencyCoercing.serialize(345.67f));
      assertEquals(45.678d, currencyCoercing.serialize(45.678d));
      assertEquals(567.89d, currencyCoercing.serialize(new BigDecimal("567.89")));
    }

    @Test
    @DisplayName("serializes any string representing a number to a double")
    void serializeStringToDouble() {
      assertEquals(123d, currencyCoercing.serialize("123"));
      assertEquals(123.45d, currencyCoercing.serialize("123.45"));
    }

    @Test
    @DisplayName("throws exception if the given object cannot be converted to double")
    void serializeThrowsException() {
      Object object = new Object();
      assertThrows(CoercingSerializeException.class, () -> currencyCoercing.serialize(""));
      assertThrows(CoercingSerializeException.class, () -> currencyCoercing.serialize(object));
      assertThrows(CoercingSerializeException.class, () -> currencyCoercing.serialize(null));
    }
  }

  @Nested
  @DisplayName("when parsing a value from a GraphQL input")
  class WhenParsingValue {

    @Test
    @DisplayName("converts any number to BigDecimal")
    void parseValueNumberToBigDecimal() {
      assertEquals(new BigDecimal("123"), currencyCoercing.parseValue(123));
      assertEquals(new BigDecimal("234"), currencyCoercing.parseValue(234L));
      assertEquals(new BigDecimal("345.67"), currencyCoercing.parseValue(345.67f));
      assertEquals(new BigDecimal("45.678"), currencyCoercing.parseValue(45.678d));
      assertEquals(new BigDecimal("567.89"), currencyCoercing.parseValue(new BigDecimal("567.89")));
    }

    @Test
    @DisplayName("converts any string representing a number to a BigDecimal")
    void parseValueStringToBigDecimal() {
      assertEquals(new BigDecimal("123"), currencyCoercing.parseValue("123"));
      assertEquals(new BigDecimal("123.45"), currencyCoercing.parseValue("123.45"));
    }

    @Test
    @DisplayName("throws exception if the given object cannot be converted to BigDecimal")
    void parseValueThrowsException() {
      Object object = new Object();
      assertThrows(CoercingParseValueException.class, () -> currencyCoercing.parseValue(""));
      assertThrows(CoercingParseValueException.class, () -> currencyCoercing.parseValue(object));
      assertThrows(CoercingParseValueException.class, () -> currencyCoercing.parseValue(null));
    }
  }

  @Nested
  @DisplayName("when parsing a literal from a GraphQL input")
  class WhenParsingLiteral {

    @Test
    @DisplayName("converts an IntValue to a BigDecimal")
    void parseLiteralIntValueToBigDecimal() {
      assertEquals(new BigDecimal("123"), currencyCoercing.parseLiteral(new IntValue(new BigInteger("123"))));
    }

    @Test
    @DisplayName("converts a FloatValue to a BigDecimal")
    void parseLiteralFloatValueToBigDecimal() {
      assertEquals(new BigDecimal("123.45"), currencyCoercing.parseLiteral(new FloatValue(new BigDecimal("123.45"))));
    }

    @Test
    @DisplayName("throws exception if the given input belongs to an unsupported type")
    void parseLiteralThrowsException() {
      Object object = new Object();
      assertThrows(CoercingParseLiteralException.class, () -> currencyCoercing.parseLiteral(""));
      assertThrows(CoercingParseLiteralException.class, () -> currencyCoercing.parseLiteral(object));
      assertThrows(CoercingParseLiteralException.class, () -> currencyCoercing.parseLiteral(null));
    }
  }
}