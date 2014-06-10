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

import java.util.Collections;
import java.util.List;

import jp.dip.komusubi.lunch.model.Order;
import jp.lunchat.LunchatException;

public class DuplicatedOrderException extends LunchatException {

	private static final long serialVersionUID = -572106620056732612L;
	private List<Order> orders;

	public DuplicatedOrderException() {
		super();
	}

	public DuplicatedOrderException(List<Order> orders) {
		this.orders = orders;
	}

	public DuplicatedOrderException(String message) {
		super(message);
	}

	public DuplicatedOrderException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public DuplicatedOrderException(Throwable throwable) {
		super(throwable);
	}

	public List<Order> getDuplictedOrders() {
		if (orders == null)
			orders = Collections.emptyList();
		return orders;
	}

}
