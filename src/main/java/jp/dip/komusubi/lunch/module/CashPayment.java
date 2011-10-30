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

import jp.dip.komusubi.lunch.model.Action;
import jp.dip.komusubi.lunch.model.Basket;
import jp.dip.komusubi.lunch.model.Payment;
import jp.dip.komusubi.lunch.model.Role;
import jp.dip.komusubi.lunch.model.User;

public class CashPayment implements Payment, Action {

	
	private User user;

	public CashPayment(User user) {
		this.user = user;
	}
	
	public boolean pay(Basket basket) {
		return false;
	}

	public boolean isPayable(int amountAll) {

		return false;
	}

	public Role getRole() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean available(User user) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isPayable(Basket basket) {
		return isPayable(basket.amountAll());
	}

}
