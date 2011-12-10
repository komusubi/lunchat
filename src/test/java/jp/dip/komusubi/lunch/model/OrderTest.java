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
package jp.dip.komusubi.lunch.model;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class OrderTest {

	private Order target;
	
	@Before
	public void before() {
		target = new Order();
	}
	
	@Ignore
	@Test
	public void 料金取得テスト() {
		target.setAmount(500);
		assertEquals(500, target.getAmount());
	}

	@Test(expected=IllegalArgumentException.class)
	public void 保持していない商品の数量変更() {
		target.modify("123product", 10);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void 数量マイナス設定() {
		target.addOrderLine(new OrderLine()
								.setDatetime(new Date())
								.setProduct(new Product("product-yummy")
												.setName("美味しいお弁当")
												.setAmount(500)
												.setShop(new Shop("bento"))));

		target.modify("product-yummy", -1);
	}
}
