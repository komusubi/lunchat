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
package jp.dip.komusubi.lunch.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * order.
 * 
 * @author jun.ozeki
 * @since 2011/11/24
 */
public class Order implements Serializable, Iterable<OrderLine> {

	private static final long serialVersionUID = -6439746231384675399L;
	private int id;
	private User user;
	private Shop shop;
	private int amount;
	private Date datetime;
	// private int geoId;
	private List<OrderLine> lines;

	public Order() {
		this(0);
	}

	public Order(int id) {
		this.id = id;
		lines = new ArrayList<OrderLine>();
	}

	public Order addOrderLine(OrderLine orderLine) {
		lines.add(orderLine);
		return this;
	}

	public Order addOrderLines(Collection<OrderLine> orderLines) {
		lines.addAll(orderLines);
		return this;
	}

	public List<OrderLine> getOrderLines() {
	    return lines;
	}
	
	public void clear() {
		lines.clear();
	}

	public int getAmount() {
		// FIXME 注文確定後に商品が値段を変更した場合の対処が必要
		int amountAll = 0;
		for (OrderLine o: lines) {
			amountAll += o.getAmount();
		}
		return amountAll;
	}

	public Date getDatetime() {
		return datetime;
	}

	public int getId() {
		return id;
	}

	public OrderLine getOrderLine(int index) {
		return lines.get(index);
	}

	public OrderLine getOrderLine(String productId) {
		OrderLine orderLine = null;
		if (productId == null || "".equals(productId))
			return orderLine;
		for (OrderLine o : lines) {
			if (o.getProduct() != null && productId.equals(o.getProduct().getId()))
				orderLine = o;
		}
		return orderLine;
	}

	public Shop getShop() {
		return shop;
	}

	public User getUser() {
		return user;
	}

	@Override
	public Iterator<OrderLine> iterator() {
		return lines.iterator();
	}

	public void modify(String productId, int quantity) {
		if (quantity < 0)
			throw new IllegalArgumentException("quantity MUST not minus: " + quantity);
		if (productId == null || "".equals(productId))
			throw new IllegalArgumentException("productId MUST required.");
		OrderLine orderLine = getOrderLine(productId);
		if (orderLine == null)
			throw new IllegalArgumentException("not found productId: " + productId);
		if ((orderLine.getQuantity() + quantity) < 1)
			throw new IllegalArgumentException("quantity can't under zero. current:"
					+ orderLine.getQuantity() + ", modify to: " + quantity);
		orderLine.increment(quantity);
	}

	public void remove(Product product) {
		remove(product.getId());
	}

	public boolean remove(String productId) {
		boolean result = false;
		if (productId == null || "".equals(productId))
			return result;
		for (OrderLine o : lines) {
			if (o.getProduct() != null && productId.equals(o.getProduct().getId())) {
				result = lines.remove(o);
			}
		}
		return result;
	}

	public Order setAmount(int amount) {
		this.amount = amount;
		return this;
	}

	public Order setDatetime(Date datetime) {
		this.datetime = datetime;
		return this;
	}

	public Order setShop(Shop shop) {
		this.shop = shop;
		return this;
	}

	public Order setUser(User user) {
		this.user = user;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Order [id=").append(id).append(", user=").append(user).append(", shop=")
				.append(shop).append(", amount=").append(amount).append(", datetime=")
				.append(datetime).append(", lines=").append(lines).append("]");
		return builder.toString();
	}

}
