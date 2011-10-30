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
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import jp.dip.komusubi.common.util.Resolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Basket implements Iterable<Order>, Serializable {

	private static final long serialVersionUID = -4354827769351959572L;
	private static final Logger logger = LoggerFactory.getLogger(Basket.class);
	private List<Order> orders;
	@Inject
	@Named("date")
	private transient Resolver<Date> dateResolver;
	private String userId;
	private Date date;

	public Basket() {
		orders = new ArrayList<Order>();
	}

	public void add(Order order) {
		boolean found = false;
		for (Order o: orders) {
			if (o.equals(order.getProduct())) {
				o.incrementQuantity(1);
				found = true;
			}
		}
		if (!found)
			orders.add(order);
	}

	public int amountAll() {
		int amount = 0;
		for (Order o: orders) {
			amount += o.getAmount();
		}

		return amount;
	}

	public void clear() {
		orders.clear();
	}

	public Date getDate() {
		if (date == null)
			date = dateResolver.resolve();
		return date;
	}

	public String getUserId() {
		return userId;
	}

	public Iterator<Order> iterator() {
		return orders.iterator();
	}

	public void remove(String productId) {
		if (productId == null)
			return;
		boolean found = false;
		for (Order o: orders) {
			if (productId.equals(o.getProduct().getId())) {
				if (o.getQuantity() > 1)
					o.incrementQuantity(-1);
				else
					orders.remove(o);
				found = true;
			}
		}
		if (!found)
			logger.info("remove({}) doesn't have in a basket.", productId);
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Basket [orders=").append(orders).append(", userId=").append(userId)
				.append(", date=").append(date).append("]");
		return builder.toString();
	}
}
