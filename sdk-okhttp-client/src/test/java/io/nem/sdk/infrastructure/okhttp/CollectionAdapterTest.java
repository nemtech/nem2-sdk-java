/*
 * Copyright 2019. NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.nem.sdk.infrastructure.okhttp;

import com.google.gson.Gson;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.JSON;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CollectionAdapterTest {

    @Test
    public void shouldNotSerializeEmtpyLists() {
        Gson gson = JSON.createGson().registerTypeHierarchyAdapter(
            Collection.class, new CollectionAdapter()).create();

        Assertions.assertEquals("{\"name\":\"Lib1\"}", gson.toJson(new Library("Lib1", null)));
        Assertions.assertEquals("{\"name\":\"Lib2\"}",
            gson.toJson(new Library("Lib2", Collections.emptyList())));
        Assertions
            .assertEquals("{\"name\":\"Lib3\",\"books\":[\"book1\",\"book2\"]}",
                gson.toJson(new Library("Lib3", Arrays.asList("book1", "book2"))));
    }

    @Test
    public void shouldSerializeEmtpyLists() {
        Gson gson = JSON.createGson().create();

        Assertions.assertEquals("{\"name\":\"Lib1\"}", gson.toJson(new Library("Lib1", null)));
        Assertions.assertEquals("{\"name\":\"Lib2\",\"books\":[]}",
            gson.toJson(new Library("Lib2", Collections.emptyList())));
        Assertions
            .assertEquals("{\"name\":\"Lib3\",\"books\":[\"book1\",\"book2\"]}",
                gson.toJson(new Library("Lib3", Arrays.asList("book1", "book2"))));
    }

    public static class Library {

        private final String name;
        private final List<String> books;

        public Library(String name, List<String> books) {
            this.name = name;
            this.books = books;
        }

        public String getName() {
            return name;
        }

        public List<String> getBooks() {
            return books;
        }
    }
}
