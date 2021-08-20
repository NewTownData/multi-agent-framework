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
package com.newtowndata.maf.market;

import java.util.Optional;

public class Quote {

  private final int price;
  private final Optional<Bid> bestBid;
  private final Optional<Ask> bestAsk;
  private final int totalBidAmount;
  private final int totalAskAmount;

  public Quote(int price, Optional<Bid> bestBid, Optional<Ask> bestAsk, int totalBidAmount,
      int totalAskAmount) {
    this.price = price;
    this.bestBid = bestBid;
    this.bestAsk = bestAsk;
    this.totalBidAmount = totalBidAmount;
    this.totalAskAmount = totalAskAmount;
  }

  public int getPrice() {
    return price;
  }

  public Optional<Bid> getBestBid() {
    return bestBid;
  }

  public Optional<Ask> getBestAsk() {
    return bestAsk;
  }

  public int getTotalAskAmount() {
    return totalAskAmount;
  }

  public int getTotalBidAmount() {
    return totalBidAmount;
  }

  @Override
  public String toString() {
    return "Quote [" + price + ", " + bestBid + ", " + bestAsk + ", " + totalBidAmount + ", "
        + totalAskAmount + "]";
  }

}
