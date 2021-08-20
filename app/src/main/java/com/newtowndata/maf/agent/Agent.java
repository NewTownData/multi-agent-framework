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
package com.newtowndata.maf.agent;

import com.newtowndata.maf.config.AgentConfig;
import com.newtowndata.maf.event.Event;
import com.newtowndata.maf.topic.TopicConsumer;
import java.net.URI;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Agent implements Runnable {

  private static final Logger LOG = LoggerFactory.getLogger(Agent.class);

  public static final long POLL_WAIT_IN_MILLIS = 1000;

  protected AgentConfig agentConfig;

  public Agent(AgentConfig agentConfig) {
    this.agentConfig = agentConfig;
  }

  @Override
  public void run() {
    try (TopicConsumer consumer = agentConfig.getRequestTopic().subscribe()) {
      Optional<Event> event;
      while (true) {
        event = consumer.poll(POLL_WAIT_IN_MILLIS);
        if (event.isPresent()) {
          handleEvent(event.get());
        }
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (Exception e) {
      LOG.warn("Agent {} failed", agentConfig.getUri(), e);
    }
  }

  protected abstract void handleEvent(Event event);

  protected void sendEvent(URI target, String type, Object data) {
    agentConfig.getResponseTopic()
        .publish(agentConfig.getEventProducer().create(agentConfig.getUri(), target, type, data));
  }

  @Override
  public String toString() {
    return agentConfig.getUri().toString();
  }

}
