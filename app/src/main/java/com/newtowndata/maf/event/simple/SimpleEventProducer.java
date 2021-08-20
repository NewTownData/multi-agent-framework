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
package com.newtowndata.maf.event.simple;

import com.newtowndata.maf.event.Event;
import com.newtowndata.maf.event.EventProducer;
import java.net.URI;
import java.time.Clock;
import java.util.UUID;

public class SimpleEventProducer implements EventProducer {

  public static final String TYPE_START = "start";
  public static final String TYPE_AGENT_REGISTERED = "agent_registered";
  public static final String TYPE_ACK = "ack";

  public static final String AGENT_URI_PREFIX = "agent:";

  public static URI URI_ALL = URI.create("env:all");
  public static URI URI_ENVIRONMENT = URI.create("env:env");
  public static URI URI_RUNTIME = URI.create("env:runtime");

  private final Clock clock;

  public SimpleEventProducer(Clock clock) {
    this.clock = clock;
  }

  @Override
  public Event create(URI source, URI target, String type, Object data) {
    return new Event(UUID.randomUUID().toString(), clock.millis(), source, target, type, data);
  }

  public static URI createAgentUri(String agentName) {
    return URI.create(AGENT_URI_PREFIX + agentName);
  }

}
