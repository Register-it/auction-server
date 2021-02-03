package it.register.edu.auction.coercing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import graphql.language.StringValue;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("URLCoercing")
class URLCoercingTest {

  private static final String URL = "https://this.is.an.url";
  private URLCoercing urlCoercing;

  @BeforeEach
  void setUp() {
    urlCoercing = new URLCoercing();
  }

  @Nested
  @DisplayName("when fetching data from GraphQL server")
  class WhenFetching {

    @Test
    @DisplayName("serializes the fetched object to a string")
    void serializeObjectToString() throws MalformedURLException {
      assertEquals(URL, urlCoercing.serialize(URL));
      assertEquals(URL, urlCoercing.serialize(new URL(URL)));
    }

    @Test
    @DisplayName("throws an exception if it cannot serialize the object to a string")
    void serializeThrowsException() {
      assertThrows(CoercingSerializeException.class, () -> urlCoercing.serialize(null));
    }
  }

  @Nested
  @DisplayName("when parsing a value from a GraphQL input")
  class WhenParsingValue {

    @Test
    @DisplayName("converts the given string to an URL")
    void parseValueStringToURL() throws MalformedURLException {
      assertEquals(new URL(URL), urlCoercing.parseValue(URL));
    }

    @Test
    @DisplayName("throws an exception if the given string is a malformed URL")
    void parseValueThrowsExceptionForMalformedURL() {
      assertThrows(CoercingParseValueException.class, () -> urlCoercing.parseValue("not.a valid.url"));
    }

    @Test
    @DisplayName("throws an exception if the input is not a string")
    void parseValueThrowsExceptionForInvalidType() {
      assertThrows(CoercingParseValueException.class, () -> urlCoercing.parseValue(123));
      assertThrows(CoercingParseValueException.class, () -> urlCoercing.parseValue(null));
    }
  }

  @Nested
  @DisplayName("when parsing a literal from a GraphQL input")
  class WhenParsingLiteral {

    @Test
    @DisplayName("converts the given StringValue to an URL")
    void parseLiteralStringValueToURL() throws MalformedURLException {
      assertEquals(new URL(URL), urlCoercing.parseLiteral(new StringValue(URL)));
    }

    @Test
    @DisplayName("throws an exception if the given StringValue is a malformed URL")
    void parseLiteralThrowsExceptionForMalformedURL() {
      StringValue stringValue = new StringValue("not.a valid.url");
      assertThrows(CoercingParseLiteralException.class, () -> urlCoercing.parseLiteral(stringValue));
    }

    @Test
    @DisplayName("throws an exception if the input is not a StringValue")
    void parseLiteralThrowsExceptionForInvalidType() {
      assertThrows(CoercingParseLiteralException.class, () -> urlCoercing.parseLiteral(123));
      assertThrows(CoercingParseLiteralException.class, () -> urlCoercing.parseLiteral(null));
    }
  }
}