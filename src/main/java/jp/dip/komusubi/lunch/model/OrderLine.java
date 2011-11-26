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

/**
 * order line.
 * @author jun.ozeki
 * @since 2011/11/20
 */
public class OrderLine implements Serializable {

	/**
	 * order line primary key.
	 * 
	 * @author jun.ozeki
	 * @since 2011/11/23
	 */
	public static class OrderLineKey implements Serializable {

		private static final long serialVersionUID = -9158460336546810565L;
		private int id;
		private int orderId;

		public OrderLineKey() {
			this(0, 0);
		}
		
		public OrderLineKey(int id, int orderId) {
			this.id = id;
			this.orderId = orderId;
		}
		
		public int getId() {
			return id;
		}
		
		public int getOrderId() {
			return orderId;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("OrderLineKey [id=").append(id).append(", orderId=").append(orderId)
					.append("]");
			return builder.toString();
		}

	}
	private static final long serialVersionUID = 4918522454881089820L;
	private Product product;
	private OrderLineKey primaryKey;
	private int quantity;
	private int fixedAmount;
	private Date datetime;

	public OrderLine() {
		this(new OrderLineKey());
	}

	public OrderLine(OrderLineKey primaryKey) {
		this.primaryKey = primaryKey;
		quantity = 1;
	}

	public int getAmount() {
		// FIXME fixed amount は確定後の注文金額なので加算（追加注文）のみ可能。
		// その場合のProduct の修正不可とする対応が必要。
//		int amount = fixedAmount;
		int amount = 0;
		if (getProduct() != null)
			amount += getProduct().getAmount() * getQuantity();
		return amount;
	}

	public Date getDatetime() {
		return datetime;
	}

	public OrderLineKey getPrimaryKey() {
		return primaryKey;
	}

	public Product getProduct() {
		return product;
	}

	public int getQuantity() {
		return quantity;
	}

	public OrderLine setAmount(int fixedAmount) {
		this.fixedAmount = fixedAmount;
		return this;
	}
	
	public OrderLine setDatetime(Date datetime) {
		this.datetime = datetime;
		return this;
	}

	public OrderLine setProduct(Product product) {
		this.product = product;
		return this;
	}

	public OrderLine setQuantity(int quantity) {
		if (quantity <= 0)
			throw new IllegalArgumentException("quantity MUST not be under zero: " + quantity);
		this.quantity = quantity;
		return this;
	}

	public int increment(int i) {
		setQuantity(getQuantity() + i);
		return quantity;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OrderLine [product=").append(product).append(", primaryKey=")
				.append(primaryKey).append(", quantity=").append(quantity).append(", fixedAmount=")
				.append(fixedAmount).append(", datetime=").append(datetime).append("]");
		return builder.toString();
	}
}
