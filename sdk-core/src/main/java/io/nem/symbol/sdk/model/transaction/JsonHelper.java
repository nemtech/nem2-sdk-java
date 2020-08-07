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

package io.nem.symbol.sdk.model.transaction;

import java.math.BigInteger;
import java.util.List;

/**
 * JSON Helper interface used by the mappers to process json objects.
 *
 * Mappers should use the statically typed open API generated DTOs when possible. This helper should be used when there
 * is no way of just using the DTOs. For example when processing "anyOf" kind of attributes.
 *
 * This interface hides the json underline implementation like gson or jackson2.
 *
 * @author Fernando Boucquez
 */
public interface JsonHelper {

    /**
     * It serializes an object into json.
     *
     * @param object the object (json native object, MAP or DTO)
     * @return the string or null if the object is null.
     */
    String print(Object object);

    /**
     * It serializes an object into pretty json string.
     *
     * @param object the object (json native object, MAP or DTO)
     * @return the string or null if the object is null.
     */
    String prettyPrint(Object object);

    /**
     * It parse a json string into an object of the underlying implementation (e.g. gson JsonObject or jackson
     * ObjectNode)
     *
     * @param string the json string
     * @return the object or null if the string is null.
     */
    Object parse(String string);

    /**
     * It parse a json string into an object list of the given class.
     *
     * @param string the json string
     * @param clazz the class of the parsed object
     * @param <T> the type of the parsed object
     * @return the object or null if the string is null.
     */
    <T> List<T> parseList(String string, Class<T> clazz);

    /**
     * It parse a json string into a an object of the given class.
     *
     * @param string the json string
     * @param clazz the class of the parsed object
     * @param <T> the type of the parsed object
     * @return the object or null if the string is null.
     */
    <T> T parse(String string, Class<T> clazz);

    /**
     * It converts a object (for example a native json object or a map) into another type (like a statically type DTO).
     *
     * This is usefull to convert opan api "anyOf" objects into a defined DTO.
     *
     * @param object the json object (like a json native object or a map)
     * @param instanceClass the class of the converted object
     * @param path the path
     * @param <T> he type of the converted object
     * @return the converted object o null if the object is null;
     */
    <T> T convert(Object object, Class<T> instanceClass, String... path);


    /**
     * This method retrieves a Integer attribute following the the provided path
     *
     * for example:
     *
     * object = { "someAttribute1": {"someAttribute2": {"aNumber": 10} } }
     *
     * getInteger(object,"someAttribute1","someAttribute2","aNumber") will return 10
     *
     * @param object the object
     * @param path the path
     * @return an Integer or null if the path doesn't exist or is null.
     */
    Integer getInteger(Object object, String... path);


    /**
     * This method retrieves a Long attribute following the the provided path
     *
     * for example:
     *
     * object = { "someAttribute1": {"someAttribute2": {"aNumber": 10} } }
     *
     * getInteger(object,"someAttribute1","someAttribute2","aNumber") will return 10
     *
     * @param object the object
     * @param path the path
     * @return an Long or null if the path doesn't exist or is null.
     */
    Long getLong(Object object, String... path);

    /**
     * This method retrieves a Long attribute following the the provided path
     *
     * for example:
     *
     * object = { "someAttribute1": {"someAttribute2": {"aString": "hello"} } }
     *
     * getInteger(object,"someAttribute1","someAttribute2","aString") will return "hello"
     *
     * @param object the object
     * @param path the path
     * @return an Long or null if the path doesn't exist or is null.
     */
    String getString(Object object, String... path);

    /**
     * This method retrieves a Long attribute following the the provided path
     *
     * for example:
     *
     * object = { "someAttribute1": {"someAttribute2": {"aBoolean": true} } }
     *
     * getInteger(object,"someAttribute1","someAttribute2","aBoolean") will return true
     *
     * @param object the object
     * @param path the path
     * @return an Long or null if the path doesn't exist or is null.
     */
    Boolean getBoolean(Object object, String... path);

    /**
     * This method retrieves a BigInteger attribute following the the provided path
     *
     * for example:
     *
     * object = { "someAttribute1": {"someAttribute2": {"aBigInteger": "123"} } }
     *
     * getBigInteger(object,"someAttribute1","someAttribute2","aBigInteger") will return 123
     *
     * @param object the object
     * @param path the path
     * @return a BigInteger or null if the path doesn't exist or is null.
     */
    BigInteger getBigInteger(Object object, String... path);

    /**
     * This method retrieves if there is a field/value for the given path.
     *
     * @param object the object
     * @param path the path
     * @return if there is a attribute set in the object following the giving path.
     */
    boolean contains(Object object, String... path);

    /**
     * A generic object form the given payload.
     *
     * @param object the object
     * @param path the path
     * @return a generic object for the fiven payload.
     */
    Object getObject(Object object, String... path);
}
