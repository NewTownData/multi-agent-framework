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
package com.newtowndata.maf.runtime.simple;

import com.newtowndata.maf.config.AgentConfig;
import com.newtowndata.maf.event.EventProducer;
import com.newtowndata.maf.event.simple.SimpleAgentRegisteredData;
import com.newtowndata.maf.event.simple.SimpleEventProducer;
import com.newtowndata.maf.runtime.AgentRegisterCallback;
import com.newtowndata.maf.runtime.EnvironmentRegisterCallback;
import com.newtowndata.maf.runtime.Runtime;
import com.newtowndata.maf.topic.Topic;
import com.newtowndata.maf.topic.simple.SimpleTopic;
import java.time.Clock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleRuntime implements Runtime {

  private static final Logger LOG = LoggerFactory.getLogger(SimpleRuntime.class);

  public static final String TOPIC_AGENT = "agent.topic";
  public static final String TOPIC_ENVIRONMENT = "env.topic";

  private final Topic agentTopic;
  private final Topic envTopic;
  private final ExecutorService runtimeExecutorService;
  private final EventProducer eventProducer;

  public SimpleRuntime() {
    this.agentTopic = new SimpleTopic(TOPIC_AGENT);
    this.envTopic = new SimpleTopic(TOPIC_ENVIRONMENT);

    this.runtimeExecutorService = Executors.newCachedThreadPool();
    this.eventProducer = new SimpleEventProducer(Clock.systemUTC());
  }

  @Override
  public void registerEnvironment(EnvironmentRegisterCallback callback) {
    AgentConfig config =
        new AgentConfig(SimpleEventProducer.URI_ENVIRONMENT, agentTopic, envTopic, eventProducer);
    runtimeExecutorService.submit(callback.register(config));
  }

  @Override
  public void registerAgent(String name, AgentRegisterCallback callback) {
    AgentConfig config = new AgentConfig(SimpleEventProducer.createAgentUri(name), envTopic,
        agentTopic, eventProducer);
    runtimeExecutorService.submit(callback.register(config));

    agentTopic.publish(
        eventProducer.create(SimpleEventProducer.URI_RUNTIME, SimpleEventProducer.URI_RUNTIME,
            SimpleEventProducer.TYPE_AGENT_REGISTERED, new SimpleAgentRegisteredData(name)));
  }

  @Override
  public void start() {
    agentTopic.publish(eventProducer.create(SimpleEventProducer.URI_RUNTIME,
        SimpleEventProducer.URI_RUNTIME, SimpleEventProducer.TYPE_START, null));
  }

  @Override
  public void close() {
    runtimeExecutorService.shutdown();
    runtimeExecutorService.shutdownNow();
    try {
      runtimeExecutorService.awaitTermination(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      LOG.warn("Shutdown interrupted", e);
      Thread.currentThread().interrupt();
    }
  }

}
