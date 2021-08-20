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
package com.newtowndata.maf.env.simple;

import com.newtowndata.maf.config.AgentConfig;
import com.newtowndata.maf.env.Environment;
import com.newtowndata.maf.event.Event;
import com.newtowndata.maf.event.simple.SimpleEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleEnvironment extends Environment {

  private static final Logger LOG = LoggerFactory.getLogger(SimpleEnvironment.class);

  public SimpleEnvironment(AgentConfig agentConfig) {
    super(agentConfig);
  }

  @Override
  protected void handleEvent(Event event) {
    LOG.info("Event received: {}", event);

    if (SimpleEventProducer.TYPE_START.equals(event.getType())) {
      sendEvent(SimpleEventProducer.URI_ALL, SimpleEventProducer.TYPE_START, null);
    }
  }

}
