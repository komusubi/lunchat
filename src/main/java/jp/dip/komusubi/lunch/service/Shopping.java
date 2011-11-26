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

import jp.dip.komusubi.common.util.Resolver;
import jp.dip.komusubi.lunch.Configuration;
import jp.dip.komusubi.lunch.model.Order;
import jp.dip.komusubi.lunch.model.Product;
import jp.dip.komusubi.lunch.model.User;
import jp.dip.komusubi.lunch.module.Basket;
import jp.dip.komusubi.lunch.module.Transactional;
import jp.dip.komusubi.lunch.module.dao.OrderDao;
import jp.dip.komusubi.lunch.module.dao.ProductDao;

/**
 * shopping.
 * @author jun.ozeki
 * @since 2011/11/20
 */
public class Shopping implements Serializable {

	private static final long serialVersionUID = 6554643970716125151L;
	private Basket basket;
	@Inject
	private transient ProductDao productDao;
	@Inject 
	private OrderDao orderDao;
	@Inject
	@Named("date")
	private transient Resolver<Date> dateResolver;

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

	public List<Product> getProducts(String shopId) {
		return getProducts(shopId, dateResolver.resolve());
	}

	public List<Product> getProducts(String shopId, Date date) {
		return productDao.findBySalable(shopId, date);
	}

	public List<Product> getProductAll(String shopId) {
		return productDao.findByShopId(shopId);
	}

	public List<Product> getRegularProduct(String shopId) {
		// user が nullの場合の考慮必要あり。
		return null;
	}

	public List<Product> getRegularProduct() {
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


//				if (map.containsKey(o.getProduct().getShopId())) {
//				List<OrderLine> list = map.get(o.getProduct().getShopId());
//				list.add(o);
//			} else {
//				List<OrderLine> list = new ArrayList<>();
//				list.add(o);
//				map.put(o.getProduct().getShopId(), list);
//			}
//		}
//		for (Entry<String, List<OrderLine>> e: map.entrySet()) {
//			String shopId = e.getKey();
//			Order order = orderDao.findByUnique(user.getGroupId(), shopId, null);
//			if (order == null) {
//				order = new Order();
//				order.setShop(shopDao.find(shopId))
//					.setGroup(groupDao.find(user.getGroupId()))
//					.setDatetime(dateResolver.resolve());
//			}
//			order.addOrderLines(e.getValue());
//			orderDao.persist(order);
//		}

	}
	
	public void order() {
		order(this.basket);
	}
	
	public void purchase() {
		throw new UnsupportedOperationException();
	}
}
