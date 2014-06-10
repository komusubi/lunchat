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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import jp.lunchat.core.model.Product;
import jp.lunchat.core.model.Shop;
import jp.lunchat.core.model.User;
import jp.lunchat.storage.dao.OrderDao;
import jp.lunchat.storage.dao.ProductDao;
import jp.lunchat.storage.dao.ShopDao;

import org.junit.Test;

/**
 * basket unit test.
 * @author jun.ozeki
 * @since 2011/11/26
 */
public class BasketTest {

	private ProductDao productDao;
	private OrderDao orderDao;
	private ShopDao shopDao;
	private User user;
	
	private Basket getTarget() {
		return new Basket(orderDao, productDao, shopDao, user);
	}
	
	@Test
	public void 商品追加() {
		productDao = mock(ProductDao.class);
		shopDao = mock(ShopDao.class);
		
		Product bento = new Product("bento")
							.setAmount(500)
							.setName("弁当")
							.setShop(new Shop("shopid"));
		Shop shop = new Shop("shopid")
							.setName("弁当屋さん");
		
		when(productDao.find("bento")).thenReturn(bento);
		when(shopDao.find("shopid")).thenReturn(shop);
		
		Basket target = getTarget();
		target.add("bento");
		
		verify(productDao).find("bento");
		verify(shopDao).find("shopid");
		
		assertEquals(bento, target.getOrder(0).getOrderLine(0).getProduct());
	}

}
