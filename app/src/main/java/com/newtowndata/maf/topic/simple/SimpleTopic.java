/*
 * Copyright 2021 Voyta Krizek, https://github.com/NewTownData
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.newtowndata.maf.topic.simple;

import com.newtowndata.maf.event.Event;
import com.newtowndata.maf.topic.Topic;
import com.newtowndata.maf.topic.TopicConsumer;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SimpleTopic implements Topic {

  private final ConcurrentMap<String, BlockingQueue<Event>> subscribers = new ConcurrentHashMap<>();

  private final String name;

  public SimpleTopic(String name) {
    this.name = name;
  }

  @Override
  public void publish(Event event) {
    subscribers.values().forEach(queue -> queue.add(event));
  }

  @Override
  public TopicConsumer subscribe() {
    String consumerId = UUID.randomUUID().toString();
    BlockingQueue<Event> queue =
        subscribers.computeIfAbsent(consumerId, id -> new LinkedBlockingQueue<>());
    return new SimpleTopicConsumer(consumerId, queue);
  }

  @Override
  public String toString() {
    return name;
  }

  private class SimpleTopicConsumer implements TopicConsumer {

    private final String consumerId;
    private final BlockingQueue<Event> queue;

    public SimpleTopicConsumer(String consumerId, BlockingQueue<Event> queue) {
      this.consumerId = consumerId;
      this.queue = queue;
    }

    @Override
    public Optional<Event> poll(long waitInMillis) throws InterruptedException {
      return Optional.ofNullable(queue.poll(waitInMillis, TimeUnit.MILLISECONDS));
    }

    @Override
    public void close() {
      SimpleTopic.this.subscribers.remove(consumerId);
    }

    @Override
    public String toString() {
      return name + "/" + consumerId;
    }

  }

}
