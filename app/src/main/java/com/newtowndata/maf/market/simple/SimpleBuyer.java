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
import com.newtowndata.maf.market.Buyer;
import com.newtowndata.maf.market.Trade;
import java.util.Optional;

public class SimpleBuyer implements Buyer {

  private final String name;
  private final int maxPrice;
  private final int maxAmount;
  private final double a;
  private final double b;

  private int totalPricePaid = 0;
  private int totalAmountBought = 0;

  private int currentAmountBought = 0;
  private int currentStep = -1;
  private int lastBidPrice = 0;

  /**
   * Constructor.
   *
   * @param name Name.
   * @param maxPrice Maximum price.
   * @param maxAmount Maximum amount possible.
   * @param a a in a*price + b = amount.
   */
  public SimpleBuyer(String name, int maxPrice, int maxAmount, double a) {
    this.name = name;
    this.maxPrice = maxPrice;
    this.maxAmount = maxAmount;
    this.a = a;
    this.b = (int) Math.round(-1.0 * a * maxPrice);
  }


  @Override
  public void handleMarketPrice(int step, int currentMarketPrice) {
    if (currentStep != step) {
      currentAmountBought = 0;
      currentStep = step;
      lastBidPrice = currentMarketPrice;
    }
  }

  @Override
  public Optional<Bid> buy(Bid offer) {
    if (currentAmountBought >= maxAmount) {
      return Optional.empty();
    }

    if (offer.getPrice() < lastBidPrice) {
      lastBidPrice = offer.getPrice();
    }

    if (lastBidPrice > maxPrice) {
      return Optional.empty();
    }

    int calculatedAmount =
        (int) Math.round(Math.max(maxAmount, a * lastBidPrice + b) - currentAmountBought);
    if (calculatedAmount >= offer.getAmount()) {
      return Optional.of(new Bid(lastBidPrice, offer.getAmount()));
    }
    return Optional.empty();
  }

  @Override
  public void handleBuy(Bid bid) {
    if (bid.getAmount() == 0) {
      lastBidPrice = bid.getPrice() + 1;
    } else {
      totalPricePaid += bid.getPrice();
      totalAmountBought += bid.getAmount();
      currentAmountBought += bid.getAmount();
      lastBidPrice = Math.max(1, bid.getPrice() - 1);
    }
  }

  @Override
  public Trade getTotalTrade() {
    return new Trade(totalAmountBought, totalPricePaid);
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
