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

public class Bid {

  private final String bidId;
  private final int price;
  private final int amount;

  public Bid(String bidId, int price, int amount) {
    this.bidId = bidId;
    this.price = price;
    this.amount = amount;
  }

  public String getBidId() {
    return bidId;
  }

  public int getPrice() {
    return price;
  }

  public int getAmount() {
    return amount;
  }

  @Override
  public String toString() {
    return "B(" + bidId + "," + amount + "@" + price + ")";
  }


}
