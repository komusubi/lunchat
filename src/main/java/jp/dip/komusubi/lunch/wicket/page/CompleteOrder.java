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
package jp.dip.komusubi.lunch.wicket.page;

import jp.dip.komusubi.lunch.model.Order;
import jp.dip.komusubi.lunch.module.dao.OrderDao;
import jp.dip.komusubi.lunch.service.Shopping;

import org.apache.wicket.markup.html.basic.Label;

import com.google.inject.Inject;

public class CompleteOrder extends VariationBase {

	private static final long serialVersionUID = -6096514197924442740L;
	@Inject
	private transient OrderDao orderDao;
	private transient Shopping shopping;
	
	public CompleteOrder(Shopping shopping) {
		this.shopping = shopping;
		Order order = shopping.iterator().next();
		add(new Label("name", order.getProduct().getName() + " " + order.getAmount() + "円"));
	}
	
	@Override
	// wicket 管理インスタンスでは 自作アノテーションは辞めとく。
//	@Transactional
	protected void onBeforeRender() {
		// Login.class を指定する時点でonBeforeRenderが呼び出されてログイン処理が実施されてしまう。
//		if (!WicketSession.get().isSignedIn())
//			setResponsePage(Login.class);
//		try {
//			User user = WicketSession.get().getLoggedInUser();
//			shopping = new Shopping(user);
//			shopping.add("");
//			shopping.order();
//		} catch (LunchException e) {
//			error("注文失敗");
//		}
		super.onBeforeRender();
	}
}
