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
package com.newtowndata.maf.event;

import java.net.URI;

public class Event {

  private final String id;
  private final long ts;
  private final URI source;
  private final URI target;
  private final String type;
  private final Object data;

  public Event(String id, long ts, URI source, URI target, String type, Object data) {
    this.id = id;
    this.ts = ts;
    this.source = source;
    this.target = target;
    this.type = type;
    this.data = data;
  }

  public String getId() {
    return id;
  }

  public long getTs() {
    return ts;
  }

  public URI getSource() {
    return source;
  }

  public URI getTarget() {
    return target;
  }

  public String getType() {
    return type;
  }

  @SuppressWarnings("unchecked")
  public <T> T getData() {
    return (T) data;
  }

  @Override
  public String toString() {
    return "Event [id=" + id + ", source=" + source + ", target=" + target + ", ts=" + ts
        + ", type=" + type + ", data=" + data + "]";
  }

}
