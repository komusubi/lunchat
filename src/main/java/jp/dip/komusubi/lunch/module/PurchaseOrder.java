/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package jp.dip.komusubi.lunch.module;

import java.util.Date;

import javax.inject.Inject;

import jp.dip.komusubi.common.util.Resolver;
import jp.dip.komusubi.lunch.model.Basket;
import jp.dip.komusubi.lunch.model.Payment;
import jp.dip.komusubi.lunch.module.dao.BasketDao;
import jp.dip.komusubi.lunch.module.dao.OrderDao;


public class PurchaseOrder {

	private Payment payment;

	@Inject
	public PurchaseOrder(Payment payment) {
		this.payment = payment;
	}

	public int purchase(Basket basket) {
		if (payment.isPayable(basket.amountAll()))
			return 0;
		payment.pay(basket);
		return basket.amountAll(); 
	}
	
	private OrderDao orderDao;
	private Resolver<Date> resolver;
	private BasketDao basketDao ;
	
	public void order(Basket basket) {
		basketDao.persist(basket);
	}
	
	public Basket retrieve() {
		return basketDao.find(0L);
	}
}
