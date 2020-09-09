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
package io.nem.symbol.sdk.api;

import io.reactivex.Observable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.ReturnsElementsOf;

/**
 * Helper class to test PaginationStreamer objects.
 *
 * @param <E> the entity type
 * @param <C> the criteria type.
 */
public class PaginationStreamerTester<E, C extends SearchCriteria<C>> {

  private final PaginationStreamer<E, C> streamer;
  private final Class<E> entityClass;
  private final Searcher<E, C> repository;
  private final C criteria;

  public PaginationStreamerTester(
      PaginationStreamer<E, C> streamer,
      Class<E> entityClass,
      Searcher<E, C> repository,
      C criteria) {
    this.streamer = streamer;
    this.entityClass = entityClass;
    this.repository = repository;
    this.criteria = criteria;
  }

  public void basicMultiPageTest() {
    int pageSize = 20;
    int totalEntries = 110;
    runSearch(pageSize, totalEntries, null);
  }

  public void multipageWithLimit() {
    int pageSize = 20;
    int totalEntries = 110;
    runSearch(pageSize, totalEntries, 30);
  }

  public void limitToTwoPages() {
    int pageSize = 20;
    int totalEntries = 110;
    runSearch(pageSize, totalEntries, pageSize * 2);
  }

  public void basicSinglePageTest() {
    int pageSize = 20;
    int totalEntries = 19;
    runSearch(pageSize, totalEntries, null);
  }

  private void runSearch(int pageSize, int totalEntries, Integer limit) {
    try {
      criteria.setPageSize(pageSize);
      List<E> infos =
          IntStream.range(0, totalEntries)
              .mapToObj((i) -> Mockito.mock(entityClass))
              .collect(Collectors.toList());
      Assertions.assertEquals(totalEntries, infos.size());
      List<Observable<Page<E>>> pages = toPages(infos, criteria.getPageSize());
      Mockito.when(repository.search(Mockito.eq(criteria)))
          .thenAnswer(new ReturnsElementsOf(pages));
      Observable<E> search = streamer.search(criteria);
      if (limit != null) {
        search = search.take(limit);
      }
      List<E> returnedInfos = search.toList().toFuture().get();
      Assertions.assertEquals(
          infos.subList(0, limit == null ? infos.size() : limit), returnedInfos);
      int totalPagesRead =
          limit == null ? pages.size() : (int) Math.ceil(limit.doubleValue() / pageSize);
      Mockito.verify(repository, Mockito.times(totalPagesRead)).search(Mockito.eq(criteria));
    } catch (InterruptedException | ExecutionException e) {
      throw new IllegalStateException(e.getMessage(), e);
    }
  }

  private <T> List<Observable<Page<T>>> toPages(List<T> infos, Integer pageSize) {
    List<List<T>> partitions = new ArrayList<>();
    for (int i = 0; i < infos.size(); i += pageSize) {
      partitions.add(infos.subList(i, Math.min(i + pageSize, infos.size())));
    }
    AtomicInteger pageNumber = new AtomicInteger();
    return partitions.stream()
        .map(
            pageData ->
                Observable.just(new Page<T>(pageData, pageNumber.incrementAndGet(), pageSize)))
        .collect(Collectors.toList());
  }
}
