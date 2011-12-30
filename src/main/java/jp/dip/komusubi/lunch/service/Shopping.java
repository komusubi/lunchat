/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package jp.dip.komusubi.lunch.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import jp.dip.komusubi.lunch.Configuration;
import jp.dip.komusubi.lunch.model.Order;
import jp.dip.komusubi.lunch.model.Product;
import jp.dip.komusubi.lunch.model.Shop;
import jp.dip.komusubi.lunch.model.User;
import jp.dip.komusubi.lunch.module.Basket;
import jp.dip.komusubi.lunch.module.Transactional;
import jp.dip.komusubi.lunch.module.dao.OrderDao;
import jp.dip.komusubi.lunch.module.dao.ProductDao;
import jp.dip.komusubi.lunch.module.dao.ShopDao;

import org.komusubi.common.util.Resolver;

/**
 * shopping.
 * @author jun.ozeki
 * @since 2011/11/20
 */
public class Shopping implements Serializable {

	private static final long serialVersionUID = 6554643970716125151L;
	private Basket basket;
	@Inject	private transient ProductDao productDao;
	@Inject private OrderDao orderDao;
	@Inject	private ShopDao shopDao;
	@Inject	@Named("date") private transient Resolver<Date> dateResolver;

	public Shopping() {
		this(new User(), Configuration.getInstance(Basket.class));
	}

	public Shopping(User user) {
		this(user, Configuration.getInstance(Basket.class));
	}

	public Shopping(User user, Basket basket) {
		this.basket = basket;
		basket.setUser(user);
	}

	Shopping(User user, Basket basket, OrderDao orderDao, ProductDao productDao) {
		this.basket = basket;
		this.productDao = productDao;
		basket.setUser(user);
	}

	protected Resolver<Date> getResolver() {
		return dateResolver;
	}
	
	public ShoppingStore specified(Shop shop) {
		return new ShoppingStore(this, shop);
	}
	
//	public ShoppingDateStore specified(Shop shop, Date date) {
//		return new ShoppingDateStore(this, shop, date);
//		return null;
//	}
	
	public boolean isOrderable(String shopId) {
		return isOrderable(shopId, dateResolver.resolve());
	}
	
	public boolean isOrderable(String shopId, Date current) {
		Shop shop = shopDao.find(shopId);
		return isOrderable(shop, current);
	}
	
	public boolean isOrderable(Shop shop, Date current) {
		if (shop == null)
			throw new IllegalArgumentException("shop is MUST not be null.");
		return shop.getLastOrder().after(current);
	}
	
	public List<Product> getAvailableProducts(String shopId) {
		return getAvailableProducts(shopId, dateResolver.resolve());
	}
	
	public List<Product> getAvailableProducts(String shopId, Date date) {
		if (shopId == null || "".equals(shopId))
			throw new IllegalArgumentException("shopId is invalid.");
		return productDao.findBySalable(shopId, date);
	}

	public List<Product> getDeadlineProducts(Shop shop) {
		return getDeadlineProducts(shop, dateResolver.resolve());
	}
	
	public List<Product> getDeadlineProducts(Shop shop, Date date) {
		if (shop == null)
			throw new IllegalArgumentException("shop is null");
		return productDao.findByShopIdAndFinishDate(shop.getId(), date);
	}
	
	public List<Product> getProductsAll(String shopId) {
		return productDao.findByShopId(shopId);
	}

//	public List<Product> findByFinishDay(String shopId, Date finishDay) {
//		return productDao.findByShopIdAndFinishDay(shopId, finishDay);
//	}
	
	public List<Product> getRegularProduct(String shopId) {
		// user が nullの場合の考慮必要あり。
		return null;
	}

	public Basket getBasket(User user) {
		basket.setUser(user);
		return basket;
	}

	@Transactional
	public void order(Basket basket) {
		for (Order order: basket) {
			// order date
			order.setDatetime(dateResolver.resolve());
			orderDao.persist(order);
		}
	}
	
	public void order() {
		order(this.basket);
	}
	
	public void purchase() {
		throw new UnsupportedOperationException();
	}

	public List<Shop> getAvailableShops() {
		return shopDao.findAll();
	}
}
