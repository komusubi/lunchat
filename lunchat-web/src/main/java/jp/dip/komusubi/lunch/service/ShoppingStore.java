/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package jp.dip.komusubi.lunch.service;

import java.util.Date;
import java.util.List;

import jp.dip.komusubi.lunch.model.Product;
import jp.dip.komusubi.lunch.model.Shop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShoppingStore {

	private static final long serialVersionUID = 1137015665744632718L;
	private static final Logger logger = LoggerFactory.getLogger(ShoppingStore.class);
	private Shop shop;
	private Shopping shopping;
	
	ShoppingStore(Shopping shopping, Shop shop) {
		this.shopping = shopping;
		this.shop = shop;
	}
	
	public boolean isOrderable() {
		return isOrderable(shopping.getResolver().resolve());
	}
	
	public boolean isOrderable(Date date) {
		if (shop == null)
			throw new IllegalStateException("shop MUST not be null.");
		return shopping.isOrderable(shop, date);
	}
	
	public List<Product> getAvailableProducts(Date date) {
		if (shop == null)
			throw new IllegalStateException("shop MUST not be null.");
		return shopping.getAvailableProducts(shop.getId(), date);
	}
	
	public List<Product> getAvailableProducts() {
		return getAvailableProducts(shopping.getResolver().resolve());
	}
}

