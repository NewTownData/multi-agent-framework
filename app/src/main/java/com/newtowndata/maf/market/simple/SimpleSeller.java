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

import com.newtowndata.maf.market.Ask;
import com.newtowndata.maf.market.Quote;
import com.newtowndata.maf.market.Seller;
import com.newtowndata.maf.market.Trade;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleSeller implements Seller {

  private final Logger log;
  private final String name;
  private final int minPrice;
  private final int maxAmount;
  private final double a;
  private final double b;

  private int totalPriceReceived = 0;
  private int totalAmountSold = 0;

  private int currentAmountSold = 0;
  private int currentStep = -1;

  /**
   * Constructor.
   *
   * @param name Name.
   * @param minPrice Minimum price.
   * @param maxAmount Maximum amount possible.
   * @param a a in a*price + b = amount.
   */
  public SimpleSeller(String name, int minPrice, int maxAmount, double a) {
    this.log = LoggerFactory.getLogger(SimpleSeller.class.getName() + "#" + name);
    this.name = name;
    this.minPrice = minPrice;
    this.maxAmount = maxAmount;
    this.a = a;
    this.b = (int) Math.round(-1.0 * a * minPrice);
  }

  @Override
  public void handleNextStep(int step) {
    if (currentStep != step) {
      currentAmountSold = 0;
      currentStep = step;
    }
  }

  @Override
  public List<Ask> sell(Quote quote) {
    if (currentAmountSold >= maxAmount) {
      log.debug("All items offered in {}", this);
      return Collections.emptyList();
    }

    int targetPrice;
    if (quote.getBestAsk().isEmpty()) {
      targetPrice = (int) Math.round(quote.getPrice() * 1.2);
      log.info("No ask, target price: {}", targetPrice);
    } else {
      targetPrice = quote.getBestAsk().get().getPrice();
      log.info("Ask, target price: {}", targetPrice);
    }

    int spread = 0;
    if (quote.getBestBid().isPresent() && quote.getBestAsk().isPresent()) {
      spread = quote.getBestAsk().get().getPrice() - quote.getBestBid().get().getPrice();
    }

    int quoteDiff = (quote.getTotalAskAmount() - quote.getTotalBidAmount()) * 10 / maxAmount;
    if (quoteDiff == 0) {
      if (quote.getBestAsk().isPresent()) {
        targetPrice =
            quote.getBestAsk().get().getPrice() - Math.max(0, (int) Math.round(spread * 0.2));
      }
      log.info("Amounts equal: {}", targetPrice);
    } else if (quoteDiff > 0) {
      targetPrice =
          quote.getBestAsk().get().getPrice() - Math.max(1, (int) Math.round(spread * 0.7));
      log.info("Bid, spread {}, target price: {}", spread, targetPrice);
    } else {
      if (spread > 2) {
        targetPrice -= 1;
      }
      log.info("High demand: {}", targetPrice);
    }

    if (targetPrice < minPrice) {
      targetPrice = minPrice;
      log.info("Price too low");
    }

    int amount = (int) Math.round(Math.max(maxAmount, a * targetPrice + b) - currentAmountSold);
    List<Ask> asks = new ArrayList<>();
    for (int i = 0; i < Math.min(maxAmount - currentAmountSold, amount); i++) {
      asks.add(new Ask(UUID.randomUUID().toString(), targetPrice, 1));
    }
    log.info("Created {} asks", asks.size());

    return asks;
  }

  @Override
  public void handleSell(Ask ask) {
    if (ask.getAmount() > 0) {
      totalPriceReceived += ask.getPrice();
      totalAmountSold += ask.getAmount();
      currentAmountSold += ask.getAmount();
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
