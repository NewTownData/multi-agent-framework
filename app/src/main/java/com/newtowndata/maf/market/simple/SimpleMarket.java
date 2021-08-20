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
import com.newtowndata.maf.market.Bid;
import com.newtowndata.maf.market.Buyer;
import com.newtowndata.maf.market.Market;
import com.newtowndata.maf.market.Quote;
import com.newtowndata.maf.market.Seller;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleMarket implements Market {

  private final Logger LOG = LoggerFactory.getLogger(SimpleMarket.class);

  private List<Buyer> buyers = new ArrayList<>();
  private List<Seller> sellers = new ArrayList<>();

  private final int noAuctionLoops;

  private int currentStep;
  private int currentPrice;
  private Optional<Bid> bestBid = Optional.empty();
  private Optional<Ask> bestAsk = Optional.empty();
  private int totalBidAmount = 0;
  private int totalAskAmount = 0;

  public SimpleMarket(int noAuctionLoops) {
    this.noAuctionLoops = noAuctionLoops;
  }

  @Override
  public void registerBuyer(Buyer buyer) {
    buyers.add(buyer);
    LOG.info("Registered buyer {}", buyer);
  }

  @Override
  public void registerSeller(Seller seller) {
    sellers.add(seller);
    LOG.info("Registered seller {}", seller);
  }

  @Override
  public void initializeMarket(int initialStep, int initialPrice) {
    this.currentStep = initialStep;
    this.currentPrice = initialPrice;
  }

  @Override
  public int runMarket(int step) {
    currentStep = step;
    updateAll();

    int auctionLoops = noAuctionLoops;
    while (auctionLoops-- > 0) {
      if (runAuctionStep()) {
        auctionLoops = noAuctionLoops;
      }
    }
    LOG.debug("No more auctions");

    return currentPrice;
  }

  private boolean runAuctionStep() {
    boolean auctionHappened = false;

    Quote quote = new Quote(currentPrice, bestBid, bestAsk, totalBidAmount, totalAskAmount);
    LOG.info("Current quote: {}", quote);

    List<SimpleHolder<Seller, Ask>> asks = new ArrayList<>();
    List<SimpleHolder<Buyer, Bid>> bids = new ArrayList<>();

    for (Seller seller : sellers) {
      seller.sell(quote).forEach(ask -> asks.add(new SimpleHolder<Seller, Ask>(seller, ask)));
    }

    for (Buyer buyer : buyers) {
      buyer.buy(quote).forEach(bid -> bids.add(new SimpleHolder<Buyer, Bid>(buyer, bid)));
    }

    this.bestAsk = findBestAsk(asks);
    this.bestBid = findBestBid(bids);
    this.totalAskAmount = asks.stream().mapToInt(ask -> ask.getItem().getAmount()).sum();
    this.totalBidAmount = bids.stream().mapToInt(bid -> bid.getItem().getAmount()).sum();

    if (this.bestAsk.isPresent() && this.bestBid.isPresent()
        && this.bestAsk.get().getPrice() <= this.bestBid.get().getPrice()) {
      // deal
      currentPrice = this.bestAsk.get().getPrice();
      LOG.info("Deal found: {}", currentPrice);

      List<SimpleHolder<Seller, Ask>> winningAsks =
          asks.stream().filter(ask -> ask.getItem().getPrice() == this.bestAsk.get().getPrice())
              .collect(Collectors.toList());
      List<SimpleHolder<Buyer, Bid>> winningBids =
          bids.stream().filter(bid -> bid.getItem().getPrice() == this.bestBid.get().getPrice())
              .collect(Collectors.toList());

      Collections.shuffle(winningAsks);
      Collections.shuffle(winningBids);

      for (int i = 0; i < Math.min(winningAsks.size(), winningBids.size()); i++) {
        SimpleHolder<Seller, Ask> winningAsk = winningAsks.get(i);
        SimpleHolder<Buyer, Bid> winningBid = winningBids.get(i);

        winningAsk.getPlayer().handleSell(winningAsk.getItem());
        winningBid.getPlayer().handleBuy(winningBid.getItem());

        LOG.info("Matching deal: {} = {}", winningAsk, winningBid);
      }

      auctionHappened = true;
    }

    return auctionHappened;
  }

  private Optional<Bid> findBestBid(List<SimpleHolder<Buyer, Bid>> bids) {
    Bid best = null;

    for (SimpleHolder<Buyer, Bid> bid : bids) {
      if (best == null || bid.getItem().getPrice() > best.getPrice()) {
        best = bid.getItem();
      }
    }

    return Optional.ofNullable(best);
  }

  private Optional<Ask> findBestAsk(List<SimpleHolder<Seller, Ask>> asks) {
    Ask best = null;

    for (SimpleHolder<Seller, Ask> ask : asks) {
      if (best == null || ask.getItem().getPrice() < best.getPrice()) {
        best = ask.getItem();
      }
    }

    return Optional.ofNullable(best);
  }

  private void updateAll() {
    buyers.forEach(buyer -> buyer.handleNextStep(currentStep));
    sellers.forEach(seller -> seller.handleNextStep(currentStep));
  }

}
