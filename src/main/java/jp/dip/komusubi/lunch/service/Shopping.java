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
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import jp.dip.komusubi.common.util.Resolver;
import jp.dip.komusubi.lunch.Configuration;
import jp.dip.komusubi.lunch.model.Basket;
import jp.dip.komusubi.lunch.model.Order;
import jp.dip.komusubi.lunch.model.Product;
import jp.dip.komusubi.lunch.model.User;
import jp.dip.komusubi.lunch.module.NotFoundProductException;
import jp.dip.komusubi.lunch.module.Transactional;
import jp.dip.komusubi.lunch.module.dao.OrderDao;
import jp.dip.komusubi.lunch.module.dao.ProductDao;

public class Shopping implements Serializable {

	private static final long serialVersionUID = 6554643970716125151L;
	private Basket basket;
	private User user;
	@Inject
	private transient OrderDao orderDao;
	@Inject
	private transient ProductDao productDao;
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
		this.user = user;
		this.basket = basket;
		basket.setUserId(user.getId());
	}
	
	Shopping(User user, Basket basket, OrderDao orderDao, ProductDao productDao) {
		this.user = user;
		this.basket = basket;
		this.orderDao = orderDao;
		this.productDao = productDao;
	}

	public void add(Order order) {
		basket.add(order);
	}
	
	public void add(Product product) {
		Order order = new Order()
							.setProduct(product)
							.setUser(user)
							.setDate(dateResolver.resolve());
		add(order);
	}
	
	public void add(String productId) {
		Product p = productDao.find(productId);
		if (p == null)
			throw new NotFoundProductException(productId);
		add(p);
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
	
	public int amount() {
		return basket.amountAll();
	}

	public void clear() {
		basket.clear();
	}

	public Basket getBasket() {
		return basket;
	}

	public Iterator<Order> iterator() {
		return basket.iterator();
	}

	public User getUser() {
		return user;
	}

    public void order() {
        order(false);
    }

    @Transactional
    public void order(boolean force) {
        if (user == null)
            throw new IllegalStateException("shopping user is null");

        // 二重購入を避けるためにチェックを行うが、マッチング項目をどうするか？
        // まとめ買いをした場合には購入日は別になる。
        // 商品名は献立に依存するので期待できない。
        for (Order o: basket) {
            if (o.getUser() == null || o.getUser().getId() == null)
                throw new IllegalStateException("order user is null");
            if (!force) {
                List<Order> ordered = orderDao.findByUserAndDate(
                        user.getId(),
                        dateResolver.resolve());

                if (ordered.size() > 0) {
                    throw new DuplicatedOrderException(ordered);
                }
            }
            int pk = orderDao.persist(o);
            // TODO pk の値とれる？
        }

    }

	public void purchase() {
		return;
	}

	public void remove(String productId) {
		basket.remove(productId);
	}

    public void setUser(User user) {
        this.user = user;
    }
}
