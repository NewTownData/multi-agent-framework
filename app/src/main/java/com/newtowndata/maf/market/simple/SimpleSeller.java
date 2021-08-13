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
import com.newtowndata.maf.market.Seller;
import com.newtowndata.maf.market.Trade;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleSeller implements Seller {

  private final Logger LOG = LoggerFactory.getLogger(SimpleSeller.class);

  private final String name;
  private final int minPrice;
  private final int maxAmount;
  private final double a;
  private final double b;

  private int totalPriceReceived = 0;
  private int totalAmountSold = 0;

  private int currentAmountSold = 0;
  private int currentStep = -1;
  private int lastBidPrice = 0;

  /**
   * Constructor.
   *
   * @param name Name.
   * @param minPrice Minimum price.
   * @param maxAmount Maximum amount possible.
   * @param a a in a*price + b = amount.
   */
  public SimpleSeller(String name, int minPrice, int maxAmount, double a) {
    this.name = name;
    this.minPrice = minPrice;
    this.maxAmount = maxAmount;
    this.a = a;
    this.b = (int) Math.round(-1.0 * a * minPrice);
  }

  @Override
  public void handleMarketPrice(int step, int currentMarketPrice) {
    if (currentStep != step) {
      currentAmountSold = 0;
      currentStep = step;
      lastBidPrice = currentMarketPrice;
    }
  }

  @Override
  public Optional<Bid> sell() {
    if (currentAmountSold >= maxAmount) {
      LOG.debug("All items offered in {}", this);
      return Optional.empty();
    }

    if (lastBidPrice < minPrice) {
      return Optional.of(new Bid(minPrice, 1));
    }

    int amount = (int) Math.round(Math.max(maxAmount, a * lastBidPrice + b) - currentAmountSold);
    if (amount > 0) {
      return Optional.of(new Bid(lastBidPrice, 1));
    }
    return Optional.of(new Bid(minPrice, 1));
  }

  @Override
  public void handleSell(Bid bid) {
    if (bid.getAmount() == 0) {
      lastBidPrice = Math.max(minPrice, bid.getPrice() - 1);
    } else {
      totalPriceReceived += bid.getPrice();
      totalAmountSold += bid.getAmount();
      currentAmountSold += bid.getAmount();
      lastBidPrice = Math.max(minPrice, bid.getPrice() + 1);
    }
  }

  @Override
  public Trade getTotalTrade() {
    return new Trade(totalAmountSold, totalPriceReceived);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }

}
