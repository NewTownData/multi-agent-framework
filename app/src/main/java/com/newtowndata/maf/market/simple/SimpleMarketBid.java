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
package com.newtowndata.maf.market.simple;

import com.newtowndata.maf.market.Bid;

public class SimpleMarketBid implements Comparable<SimpleMarketBid> {

  private final String name;
  private final Bid bid;

  public SimpleMarketBid(String name, Bid bid) {
    this.name = name;
    this.bid = bid;
  }

  public String getName() {
    return name;
  }

  public Bid getBid() {
    return bid;
  }

  @Override
  public int compareTo(SimpleMarketBid o) {
    return -Integer.compare(bid.getPrice(), o.bid.getPrice());
  }

  @Override
  public String toString() {
    return "SimpleMarketBid [bid=" + bid + ", name=" + name + "]";
  }

}
