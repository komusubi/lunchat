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
import java.util.Date;

public class Order implements Serializable {

	private static final long serialVersionUID = -6439746231384675399L;
	private int id;
	private User user;
	private Product product;
	private Date date;
	private int quantity;
//	private int amount;

	public Order() {
		quantity = 1;
	}

	public Order(int id) {
		this();
		this.id = id;
	}

	public boolean equals(Product product) {
		if (product == null)
			return false;
		return product.equals(this.product);
	}

	public int getAmount() {
//		return amount * getQuantity();
		if (product == null)
			return 0;
		return product.getAmount() * getQuantity();
	}

	public Date getDate() {
		return date;
	}

	public int getId() {
		return id;
	}

	public Product getProduct() {
		return this.product;
	}

	public int getQuantity() {
		return quantity;
	}

	public User getUser() {
		return user;
	}

//	public Order setAmount(int amount) {
//		this.amount = amount;
//		return this;
//	}

	public Order setDate(Date date) {
		this.date = date;
		return this;
	}

	public Order setProduct(Product product) {
		this.product = product;
		return this;
	}

	public Order setQuantity(int quantity) {
		if (quantity <= 0)
			throw new IllegalArgumentException("wrong value, " + quantity	+ " quantity must not have minus.");
		this.quantity = quantity;
		return this;
	}

	public Order incrementQuantity(int quantity) {
		int sum = getQuantity() + quantity;
		if (sum < 0)
			throw new IllegalArgumentException("wrong value, now:" + this.quantity + "value:"	+ quantity);
		setQuantity(sum);
		return this;
	}

	public Order setUser(User user) {
		this.user = user;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Order [id=").append(id).append(", user=").append(user).append(", product=")
				.append(product).append(", date=").append(date).append(", quantity=")
				.append(quantity).append("]");
		return builder.toString();
	}
}
