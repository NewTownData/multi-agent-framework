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
import com.newtowndata.maf.market.Market;
import com.newtowndata.maf.market.Seller;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleMarket implements Market {

  private final Logger LOG = LoggerFactory.getLogger(SimpleMarket.class);

  private List<Buyer> buyers = new ArrayList<>();
  private List<Seller> sellers = new ArrayList<>();

  private final int noAuctionLoops;

  private int currentStep;
  private int currentPrice;

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
    updateAll();
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
      updateAll();
    }
    LOG.debug("No more auctions");

    return currentPrice;
  }

  private boolean runAuctionStep() {
    boolean auctionHappened = false;
    for (Seller seller : sellers) {
      Optional<Bid> offer = seller.sell();
      if (offer.isEmpty() || offer.get().getAmount() <= 0) {
        LOG.debug("Seller {} does not offer: {}", seller, offer.orElse(null));
        continue;
      }

      LOG.debug("Seller {} offers: {}", seller, offer.get());

      List<SimpleMarketBid> bids = new ArrayList<>();
      for (Buyer buyer : buyers) {
        Optional<Bid> bid = buyer.buy(offer.get());
        LOG.debug("Buyer {} offers: {}", buyer, bid.orElse(null));
        bid.ifPresent(b -> {
          if (b.getAmount() > 0 && b.getAmount() >= offer.get().getAmount()) {
            bids.add(new SimpleMarketBid(buyer.getName(), b));
          }
        });
      }

      if (bids.isEmpty()) {
        LOG.debug("Seller {} has no bids.", seller);
        seller.handleSell(new Bid(offer.get().getPrice(), 0));
        continue;
      }

      Collections.sort(bids);
      SimpleMarketBid winner = bids.get(0);
      currentPrice = winner.getBid().getPrice();

      LOG.debug("Seller {} has a winner: {}", seller, winner);

      seller.handleSell(winner.getBid());
      buyers.stream().filter(b -> b.getName().equals(winner.getName()))
          .forEach(b -> b.handleBuy(winner.getBid()));
      buyers.stream().filter(b -> !b.getName().equals(winner.getName()))
          .forEach(b -> b.handleBuy(new Bid(winner.getBid().getPrice(), 0)));
      bids.clear();
      auctionHappened = true;
    }

    return auctionHappened;
  }

  private void updateAll() {
    buyers.forEach(buyer -> buyer.handleMarketPrice(currentStep, currentPrice));
    sellers.forEach(seller -> seller.handleMarketPrice(currentStep, currentPrice));
  }

}
