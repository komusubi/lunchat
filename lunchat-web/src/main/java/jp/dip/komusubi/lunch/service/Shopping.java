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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import jp.dip.komusubi.lunch.TransactionException;
import jp.dip.komusubi.lunch.module.Basket;
import jp.dip.komusubi.lunch.module.Transactional;
import jp.dip.komusubi.lunch.module.dao.OrderDao;
import jp.dip.komusubi.lunch.module.dao.OrderLineDao;
import jp.dip.komusubi.lunch.module.dao.ProductDao;
import jp.dip.komusubi.lunch.module.dao.ShopDao;
import jp.lunchat.core.model.Group;
import jp.lunchat.core.model.Order;
import jp.lunchat.core.model.OrderLine;
import jp.lunchat.core.model.Product;
import jp.lunchat.core.model.Shop;
import jp.lunchat.core.model.User;
import jp.lunchat.core.model.OrderLine.OrderLineKey;

import org.komusubi.common.util.Resolver;

/**
 * shopping.
 * @author jun.ozeki
 * @since 2011/11/20
 */
public class Shopping implements Serializable {

	private static final long serialVersionUID = 6554643970716125151L;
	@Inject private Basket basket;
	@Inject private ProductDao productDao;
	@Inject private OrderDao orderDao;
	@Inject private OrderLineDao orderLineDao;
	@Inject private ShopDao shopDao;
	@Inject @Named("date") private Resolver<Date> dateResolver;

	public Shopping() {
//		this(new User(), Configuration.getInstance(Basket.class));
//	    this(new User());
	}

//	public Shopping(User user) {
//		this(user, Configuration.getInstance(Basket.class));
//	}

//	public Shopping(User user) {
//	    basket.setUser(user);
//	}
	
//	public Shopping(User user, Basket basket) {
//		this.basket = basket;
//		basket.setUser(user);
//	}

	// package scope for unit test
	Shopping(User user, Basket basket, OrderDao orderDao, 
	        OrderLineDao orderLineDao, ProductDao productDao, ShopDao shopDao, Resolver<Date> resolver) {
		this.basket = basket;
		this.orderDao = orderDao;
		this.orderLineDao = orderLineDao;
		this.productDao = productDao;
		this.shopDao = shopDao;
		this.dateResolver = resolver;
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
	
	public List<Product> getDeadlineTimeProducts(Shop shop) {
		return getDeadlineTimeProducts(shop, dateResolver.resolve());
	}
	
	public List<Product> getDeadlineTimeProducts(Shop shop, Date date) {
		if (shop == null)
			throw new IllegalArgumentException("shop is null");
		return productDao.findByShopIdAndFinishDatetime(shop.getId(), date);
	}
	
	public List<Product> getProductsAll(String shopId) {
		return productDao.findByShopId(shopId);
	}

//	public List<Product> findByFinishDay(String shopId, Date finishDay) {
//		return productDao.findByShopIdAndFinishDay(shopId, finishDay);
//	}
	
	public List<Product> getRegularProduct(String shopId) {
		// user が nullの場合の考慮必要あり。
	    throw new UnsupportedOperationException("not implemented");

	}

	public Basket getBasket(User user) {
		basket.setUser(user);
		return basket;
	}

	@Transactional
	public void order(Basket basket) {
		for (Order order: basket) {
			// set order date when ordered.
			order.setDatetime(dateResolver.resolve());
			orderDao.persist(order);
		}
	}
	
	public void order() {
		order(this.basket);
	}
	
	@Transactional
	public void order(Group group) {
	    if (group == null)
	        throw new IllegalArgumentException("group must NOT be null.");
	    if (basket.getUser() == null || basket.getUser().getId() == null)
	        throw new IllegalStateException("user must NOT be null.");
	    
	    Map<Shop, Order> groupOrders = new HashMap<>();
	    List<Order> orders = orderDao.findByGroupIdAndDate(group.getId(), dateResolver.resolve(), false);
	    for (Order order: orders) {
	        if (order.isCancel())
	            continue;
	        Order summary;
	        if (groupOrders.containsKey(order.getShop())) {
	            summary = groupOrders.get(order.getShop());
	        } else {
	            summary = new Order();
	            summary.setGroup(order.getGroup());
	            summary.setShop(order.getShop());
	            summary.setUser(basket.getUser());
	            summary.setDatetime(dateResolver.resolve());
	            summary.setSummary(true);
	        }
   	        for (OrderLine orderLine: order) {
   	            if (orderLine.isCancel())
   	                continue;
   	            summary.addLine(orderLine, true);
   	        }
   	        groupOrders.put(order.getShop(), summary);
	    }

	    List<Order> existOrder = orderDao.findByGroupIdAndDate(group.getId(), dateResolver.resolve(), true);
	    if (existOrder.size() > 0) {
	        throw new TransactionException("already ordered. groupId: " 
	                + group.getId() + ", date: " + dateResolver.resolve());
	    }
	    
	    // persist summary order. 
	    for (Order order: groupOrders.values()) 
	        orderDao.persist(order);
	}
	
	public void purchase() {
		throw new UnsupportedOperationException();
	}

	public List<Shop> getAvailableShops() {
		return shopDao.findAll();
	}

    /**
     * cancel order (include all order lines).
     * @param order
     */
    @Transactional
    public void cancel(Order order) {
        order.setCancel(true);
        orderDao.update(order);
    }
    
    /**
     * cancel a order line.
     * @param orderLine
     */
    @Transactional
    public void cancel(OrderLine orderLine) {
        OrderLineKey key = orderLine.getPrimaryKey();
        Order order = orderDao.find(key.getOrderId());
        
        // check available order lines except for cancel requested.
        boolean available = false;
        for (OrderLine line: order.getOrderLines(false)) {
            if (line.getPrimaryKey().getNo() != orderLine.getPrimaryKey().getNo()) {
                available = true;
                continue;
            }
        } 
        
        // true: cancel just a order line. 
        // false: cancel order(all order lines)
        if (available) {
            order.setCancel(true);
            orderDao.update(order);
        } else {
            cancel(order);
        }
    }
	
}
