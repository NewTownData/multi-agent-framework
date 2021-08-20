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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleBuyer implements Buyer {

  private final Logger log;
  private final String name;
  private final int maxPrice;
  private final int maxAmount;
  private final double a;
  private final double b;

  private int totalPricePaid = 0;
  private int totalAmountBought = 0;

  private int currentAmountBought = 0;
  private int currentStep = -1;

  /**
   * Constructor.
   *
   * @param name Name.
   * @param maxPrice Maximum price.
   * @param maxAmount Maximum amount possible.
   * @param a a in a*price + b = amount.
   */
  public SimpleBuyer(String name, int maxPrice, int maxAmount, double a) {
    this.log = LoggerFactory.getLogger(SimpleBuyer.class.getName() + "#" + name);
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
      log.debug("All items offered in {}", this);
      return Collections.emptyList();
    }


    int targetPrice;
    if (quote.getBestBid().isEmpty()) {
      targetPrice = (int) Math.round(quote.getPrice() * 0.8);
      log.info("No bid, target price: {}", targetPrice);
    } else {
      targetPrice = quote.getBestBid().get().getPrice();
      log.info("Bid, target price: {}", targetPrice);
    }

    int spread = 0;
    if (quote.getBestBid().isPresent() && quote.getBestAsk().isPresent()) {
      spread = quote.getBestAsk().get().getPrice() - quote.getBestBid().get().getPrice();
    }

    int quoteDiff = (quote.getTotalAskAmount() - quote.getTotalBidAmount()) * 10 / maxAmount;
    if (quoteDiff == 0) {
      if (quote.getBestBid().isPresent()) {
        targetPrice =
            quote.getBestBid().get().getPrice() + Math.max(1, (int) Math.round(spread * 0.2));
      }
      log.info("Amounts equal: {}", targetPrice);
    } else if (quoteDiff < 0) {
      targetPrice =
          quote.getBestBid().get().getPrice() + Math.max(1, (int) Math.round(spread * 0.7));
      log.info("Ask, spread {}, target price: {}", spread, targetPrice);
    } else {
      if (spread > 2) {
        targetPrice += 1;
      }
      log.info("Low demand: {}", targetPrice);
    }

    if (targetPrice > maxPrice) {
      targetPrice = maxPrice;
      log.info("Price too high");
    }

    int amount = (int) Math.round(Math.max(maxAmount, a * targetPrice + b) - currentAmountBought);
    List<Bid> bids = new ArrayList<>();
    for (int i = 0; i < Math.min(maxAmount - currentAmountBought, amount); i++) {
      bids.add(new Bid(UUID.randomUUID().toString(), targetPrice, 1));
    }
    log.info("Created {} bids", bids.size());

    return bids;
  }

  @Override
  public void handleBuy(Bid bid) {
    if (bid.getAmount() > 0) {
      totalPricePaid += bid.getPrice();
      totalAmountBought += bid.getAmount();
      currentAmountBought += bid.getAmount();
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
