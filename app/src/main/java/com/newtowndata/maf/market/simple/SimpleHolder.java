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

public class SimpleHolder<Player, Item> {

  private final Player player;
  private final Item item;

  public SimpleHolder(Player player, Item item) {
    this.player = player;
    this.item = item;
  }

  public Player getPlayer() {
    return player;
  }

  public Item getItem() {
    return item;
  }

  @Override
  public String toString() {
    return "{" + player + ", " + item + "}";
  }
}
