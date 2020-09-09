/*
 * Copyright 2020 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.nem.symbol.sdk.infrastructure.okhttp;

import com.google.gson.JsonObject;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import java.math.BigInteger;
import java.util.Objects;
import java.util.OptionalInt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link JsonHelperGson}
 *
 * @author Fernando Boucquez
 */
public class JsonHelperGsonTest {

  private JsonHelper jsonHelper;

  @BeforeEach
  public void setUp() {
    jsonHelper = new JsonHelperGson();
  }

  @Test
  public void shouldFailWhenParsingInvalid() {
    Assertions.assertEquals(
        "JsonSyntaxException: java.io.EOFException: End of input at line 1 column 2 path $.. Json payload: {",
        Assertions.assertThrows(
                IllegalArgumentException.class, () -> jsonHelper.parse("{", Car.class))
            .getMessage());
  }

  @Test
  public void shouldParseNull() {
    Assertions.assertNull(jsonHelper.parse(null));
    Assertions.assertNull(jsonHelper.parse(null, Car.class));
  }

  @Test
  public void shouldParsePrintedObject() {
    Car car = new Car("Renault", "Scenic", 2005, OptionalInt.of(100));
    String json = jsonHelper.print(car);

    Assertions.assertNotNull(json);
    Assertions.assertTrue(json.contains("Renault"));
    Assertions.assertTrue(json.contains("\"millage\":100"));

    Car parsedCar = jsonHelper.parse(json, Car.class);
    Assertions.assertEquals(car, parsedCar);
    Assertions.assertEquals(BigInteger.valueOf(2005), parsedCar.getYear());
    Assertions.assertEquals(100, parsedCar.getMillage().getAsInt());
  }

  @Test
  public void shouldParsePrettyPrintedObject() {
    Car car = new Car("Renault", "Scenic", 2005, OptionalInt.empty());
    String json = jsonHelper.prettyPrint(car);

    Assertions.assertNotNull(json);
    Assertions.assertTrue(json.contains("Renault"));
    Assertions.assertFalse(json.contains("millage"));

    Car parsedCar = jsonHelper.parse(json, Car.class);
    Assertions.assertEquals(car, parsedCar);
    Assertions.assertEquals(BigInteger.valueOf(2005), parsedCar.getYear());
  }

  @Test
  public void shouldParsePrintedConvertObject() {
    Car car = new Car("Renault", "Scenic", 2005, OptionalInt.empty());
    String json = jsonHelper.print(car);

    Assertions.assertNotNull(json);
    Assertions.assertTrue(json.contains("Renault"));
    Assertions.assertFalse(json.contains("millage"));

    Object genericType = jsonHelper.parse(json);

    Car convertedType = jsonHelper.convert(genericType, Car.class);

    Assertions.assertEquals(car, convertedType);
    Assertions.assertEquals(BigInteger.valueOf(2005), convertedType.getYear());
  }

  @Test
  public void shouldParseGenericNode() {
    Car car = new Car("Renault", "11", 1989, OptionalInt.empty());
    String json = jsonHelper.print(car);

    Assertions.assertNotNull(json);
    Assertions.assertTrue(json.contains("Renault"));
    Assertions.assertFalse(json.contains("millage"));

    Object parsedCar = jsonHelper.parse(json);
    Assertions.assertEquals(JsonObject.class, parsedCar.getClass());

    Assertions.assertEquals(json, jsonHelper.print(parsedCar));
  }

  @Test
  public void shouldReturnValues() {
    Car car = new Car("Renault", "11", 1989, OptionalInt.of(200));
    Assertions.assertEquals(car.getBrand(), jsonHelper.getString(car, "brand"));
    Assertions.assertEquals(car.getModel(), jsonHelper.getString(car, "model"));
    Assertions.assertEquals(
        car.getYear().intValue(), jsonHelper.getInteger(car, "year").intValue());

    Assertions.assertEquals(car.getYear().longValue(), jsonHelper.getLong(car, "year").longValue());

    Assertions.assertEquals(
        car.getMillage().getAsInt(), jsonHelper.getLong(car, "millage").intValue());

    Assertions.assertFalse(jsonHelper.getBoolean(car, "year").booleanValue());

    Assertions.assertNull(jsonHelper.getBoolean(car, "invalidProp"));
    Assertions.assertNull(jsonHelper.getString(car, "invalidProp"));
    Assertions.assertNull(jsonHelper.getInteger(car, "invalidProp"));
    Assertions.assertNull(jsonHelper.getBigInteger(car, "invalidProp"));
    Assertions.assertNull(jsonHelper.getLong(car, "invalidProp"));

    Assertions.assertTrue(jsonHelper.contains(car, "model"));
    Assertions.assertFalse(jsonHelper.contains(car, "invalidProp"));

    Assertions.assertNull(jsonHelper.getInteger(car, "model", "notInnerProperty"));
    Assertions.assertEquals(car.getYear(), jsonHelper.getBigInteger(car, "year"));
    Assertions.assertEquals(new BigInteger(car.getModel()), jsonHelper.getBigInteger(car, "model"));
  }

  @Test
  public void shouldRaiseErrorOnInvalidPath() {
    Car car = new Car("Renault", "11", 1989, OptionalInt.empty());

    Assertions.assertThrows(IllegalArgumentException.class, () -> jsonHelper.getInteger(car));
    Assertions.assertThrows(IllegalArgumentException.class, () -> jsonHelper.getString(car));
    Assertions.assertThrows(IllegalArgumentException.class, () -> jsonHelper.getBoolean(car));
    Assertions.assertThrows(IllegalArgumentException.class, () -> jsonHelper.getLong(car));
    Assertions.assertThrows(IllegalArgumentException.class, () -> jsonHelper.getBigInteger(car));
  }

  private static class Car {

    private String brand;

    private String model;

    private BigInteger year;

    private OptionalInt millage = OptionalInt.empty();

    public Car(String brand, String model, int year, OptionalInt millage) {
      this.brand = brand;
      this.model = model;
      this.year = BigInteger.valueOf(year);
      this.millage = millage;
    }

    public Car() {}

    public String getBrand() {
      return brand;
    }

    public void setBrand(String brand) {
      this.brand = brand;
    }

    public String getModel() {
      return model;
    }

    public void setModel(String model) {
      this.model = model;
    }

    public BigInteger getYear() {
      return year;
    }

    public void setYear(BigInteger year) {
      this.year = year;
    }

    public OptionalInt getMillage() {
      return millage;
    }

    public void setMillage(OptionalInt millage) {
      this.millage = millage;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Car car = (Car) o;
      return Objects.equals(brand, car.brand)
          && Objects.equals(model, car.model)
          && Objects.equals(year, car.year)
          && Objects.equals(millage, car.millage);
    }

    @Override
    public int hashCode() {
      return Objects.hash(brand, model, year, millage);
    }
  }
}
