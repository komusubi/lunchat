/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package jp.dip.komusubi.lunch.service;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import jp.dip.komusubi.lunch.model.Product;
import jp.dip.komusubi.lunch.model.Shop;
import jp.dip.komusubi.lunch.module.dao.ProductDao;
import jp.dip.komusubi.lunch.module.dao.ShopDao;
import jp.dip.komusubi.lunch.module.database.Lunchat;

import org.komusubi.common.util.Resolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * back office.
 * 
 * @author jun.ozeki
 * @since 2011/11/27
 */
public class BackOffice {

	private static final Logger logger = LoggerFactory.getLogger(BackOffice.class);
	@Inject
	private ShopDao shopDao;
	@Inject
	private ProductDao productDao;
	@Inject
	@Named("calendar")
	private Resolver<Calendar> calendarResolver;
	private Shop shop;

	public BackOffice() {

	}

	public void supplyProduct() {
		// supply product in 7 days
		// FIXME not supported shop and national holiday.
		Calendar calendar = calendarResolver.resolve();
//		for (Shop shop: shopDao.findAll()) {
			for (int i = 0; i < 7; i++) {
				calendar.add(Calendar.DATE, 1);
				if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
						|| calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
					logger.info("{} is skip", calendar.getTime());
					continue;
				}
				// 代表してtamagoyaでチェック
				List<Product> products = productDao.findByShopIdAndFinishDate("tamagoya", calendar.getTime());
				if (products.size() == 0)
					supplyProduct(calendar);
			}
//		}
	}

	public void supplyProduct(Calendar finish) {
		Calendar cal = calendarResolver.resolve();
		List<Product> products = Lunchat.getTamagoyaProducts(cal, finish);
		products.addAll(Lunchat.getFreshLunchProducts(cal, finish));
		for (Product product: products) {
			productDao.persist(product);
		}
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public void produce(Product product) {
		if (shop == null)
			throw new IllegalStateException("shop is null, first call #setShop()");
		if (shop.getId() != null && !"".equals(shop.getId()))
			if (!shop.getId().equals(product.getShopId()))
				throw new IllegalArgumentException("shop unmatch. product#getShopId:"
						+ product.getShopId() + ", shop.getId():" + shop.getId());
		productDao.persist(product);
	}

}
