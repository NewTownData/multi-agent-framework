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
package com.newtowndata.maf.market.simple;

import com.newtowndata.maf.market.Bid;
import com.newtowndata.maf.market.Buyer;
import com.newtowndata.maf.market.Quote;
import com.newtowndata.maf.market.Trade;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
  private int lastDealPrice = 0;
  private boolean dealMade = false;

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
  public void handleNextStep(int step) {
    if (currentStep != step) {
      currentAmountBought = 0;
      currentStep = step;
    }
  }

  @Override
  public List<Bid> buy(Quote quote) {
    if (currentAmountBought >= maxAmount) {
      return Collections.emptyList();
    }

    if (quote.getBestBid().isPresent()) {
      if (lastDealPrice < quote.getBestBid().get().getAmount()) {
        lastDealPrice = quote.getBestBid().get().getAmount();
      }
    } else {
      if (lastDealPrice < quote.getPrice()) {
        lastDealPrice = quote.getPrice() - 1;
      }
    }

    if (!dealMade) {
      if (quote.getTotalBidAmount() > quote.getTotalAskAmount()) {
        lastDealPrice += 1;
      }
    }

    int maxBid = maxPrice;
    if (quote.getBestAsk().isPresent()) {
      maxBid = quote.getBestAsk().get().getPrice();
    }

    List<Bid> bids = new ArrayList<>();
    if (quote.getPrice() <= maxPrice) {
      int amount =
          (int) Math.round(Math.max(maxAmount, a * lastDealPrice + b) - currentAmountBought);
      for (int i = 0; i < amount; i++) {
        bids.add(new Bid(UUID.randomUUID().toString(), Math.min(lastDealPrice - i, maxBid), 1));
      }
    }

    dealMade = false;
    return bids;
  }

  @Override
  public void handleBuy(Bid bid) {
    if (bid.getAmount() > 0) {
      totalPricePaid += bid.getPrice();
      totalAmountBought += bid.getAmount();
      currentAmountBought += bid.getAmount();
      lastDealPrice = bid.getPrice();
      dealMade = true;
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
