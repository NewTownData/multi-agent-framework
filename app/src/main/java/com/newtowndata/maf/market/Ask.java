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

public class Ask {

  private final String askId;
  private final int price;
  private final int amount;

  public Ask(String askId, int price, int amount) {
    this.askId = askId;
    this.price = price;
    this.amount = amount;
  }

  public String getAskId() {
    return askId;
  }

  public int getAmount() {
    return amount;
  }

  public int getPrice() {
    return price;
  }

  @Override
  public String toString() {
    return "A(" + askId + "," + amount + "@" + price + ")";
  }
}
