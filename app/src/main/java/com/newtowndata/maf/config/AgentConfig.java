/*
 * Copyright 2021 Voyta Krizek, https://github.com/NewTownData
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.newtowndata.maf.config;

import com.newtowndata.maf.event.EventProducer;
import com.newtowndata.maf.topic.Topic;
import java.net.URI;
import java.util.Objects;

public class AgentConfig {

  private final URI uri;
  private final Topic requestTopic;
  private final Topic responseTopic;
  private final EventProducer eventProducer;

  public AgentConfig(URI uri, Topic requestTopic, Topic responseTopic,
      EventProducer eventProducer) {
    this.uri = Objects.requireNonNull(uri);
    this.requestTopic = Objects.requireNonNull(requestTopic);
    this.responseTopic = Objects.requireNonNull(responseTopic);
    this.eventProducer = Objects.requireNonNull(eventProducer);
  }

  public URI getUri() {
    return uri;
  }

  public Topic getRequestTopic() {
    return requestTopic;
  }

  public Topic getResponseTopic() {
    return responseTopic;
  }

  public EventProducer getEventProducer() {
    return eventProducer;
  }


}
