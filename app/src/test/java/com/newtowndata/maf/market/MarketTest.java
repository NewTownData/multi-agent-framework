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
package com.newtowndata.maf.market;

import static org.junit.jupiter.api.Assertions.assertTrue;
import com.newtowndata.maf.market.simple.SimpleBuyer;
import com.newtowndata.maf.market.simple.SimpleMarket;
import com.newtowndata.maf.market.simple.SimpleSeller;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarketTest {

  private static final Logger LOG = LoggerFactory.getLogger(MarketTest.class);

  @Test
  void testMarketLowDemand() {
    int initialMarketPrice = 100;

    Buyer buyer1 = new SimpleBuyer("B1", 200, 100, -0.6);
    Buyer buyer2 = new SimpleBuyer("B2", 300, 200, -0.4);
    Buyer buyer3 = new SimpleBuyer("B3", 170, 150, -0.7);

    Seller seller1 = new SimpleSeller("S1", 30, 400, 6);
    Seller seller2 = new SimpleSeller("S2", 50, 200, 7);

    Market market = new SimpleMarket(10);
    market.registerBuyer(buyer1);
    market.registerBuyer(buyer2);
    market.registerBuyer(buyer3);
    market.registerSeller(seller1);
    market.registerSeller(seller2);

    market.initializeMarket(0, initialMarketPrice);

    int price = market.runMarket(1);
    LOG.info("Current price {}", price);
    LOG.info("Buyer 1: {}", buyer1.getTotalTrade());
    LOG.info("Buyer 2: {}", buyer2.getTotalTrade());
    LOG.info("Buyer 3: {}", buyer3.getTotalTrade());
    LOG.info("Seller 1: {}", seller1.getTotalTrade());
    LOG.info("Seller 2: {}", seller2.getTotalTrade());

    assertTrue(price < 100);
  }

  @Test
  void testMarketHighDemand() {
    int initialMarketPrice = 100;

    Buyer buyer1 = new SimpleBuyer("B1", 200, 100, -0.6);
    Buyer buyer2 = new SimpleBuyer("B2", 300, 200, -0.4);
    Buyer buyer3 = new SimpleBuyer("B3", 170, 150, -0.7);

    Seller seller1 = new SimpleSeller("S1", 30, 100, 6);
    Seller seller2 = new SimpleSeller("S2", 50, 200, 7);

    Market market = new SimpleMarket(10);
    market.registerBuyer(buyer1);
    market.registerBuyer(buyer2);
    market.registerBuyer(buyer3);
    market.registerSeller(seller1);
    market.registerSeller(seller2);

    market.initializeMarket(0, initialMarketPrice);

    int price = market.runMarket(1);
    LOG.info("Current price {}", price);
    LOG.info("Buyer 1: {}", buyer1.getTotalTrade());
    LOG.info("Buyer 2: {}", buyer2.getTotalTrade());
    LOG.info("Buyer 3: {}", buyer3.getTotalTrade());
    LOG.info("Seller 1: {}", seller1.getTotalTrade());
    LOG.info("Seller 2: {}", seller2.getTotalTrade());

    assertTrue(price > 100);
  }


}
