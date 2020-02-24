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

package io.nem.symbol.core.utils;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.jupiter.params.provider.Arguments;

/**
 * Abstract class for all the vector tests. Tests that take their input from
 * https://raw.githubusercontent.com/nemtech/test-vectors/master/
 */
public class AbstractVectorTester {

    protected static Stream<Arguments> createArguments(String fileName,
        Function<Map<String, String>, List<Arguments>> extractArguments, long skip, int limit) {
        try {
            //The files loaded here are a trimmed down version of the vectors tests https://github.com/nemtech/test-vectors
            URL url = AbstractVectorTester.class.getClassLoader()
                .getResource("vectors/" + fileName);
            ObjectMapper objectMapper = new ObjectMapper();
            // Change this to just load the first 'limit' objects from the json array file.
            List<Map<String, String>> list = objectMapper
                .readValue(url, new TypeReference<List<Map<String, String>>>() {
                });
            //Not all the tests can be run every time as it would be slow.
            //It may be good to shuffle the list so different vectors are tested each run.
            return list.stream().skip(skip).limit(limit).map(extractArguments::apply)
                .flatMap(List::stream)
                .filter(Objects::nonNull);

        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Arguments could not be generated: for file name " + fileName + ". Exception: "
                    + ExceptionUtils
                    .getMessage(e), e);
        }

    }


}
