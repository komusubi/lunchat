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

import jp.dip.komusubi.lunch.model.OrderLine.OrderLineKey;

import org.junit.Ignore;
import org.junit.Test;

public class OrderTest extends Order {

	private static final long serialVersionUID = 1L;
	
	@Ignore
	@Test
	public void 料金取得テスト() {
		setAmount(500);
		assertEquals(500, getAmount());
	}

	@Test(expected=IllegalArgumentException.class)
	public void 保持していない商品の数量変更() {
		modify("123product", 10);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void 数量マイナス設定() {
		addLine(new OrderLine()
							.setDatetime(new Date())
							.setProduct(new Product("product-yummy")
											.setName("美味しいお弁当")
											.setAmount(500)
											.setShop(new Shop("bento"))));

		modify("product-yummy", -1);
	}
	
	@Test
	public void 明細サマリー() {
	    Product p1 = new Product("p1")
	                    .setAmount(400)
	                    .setName("お弁当");

	    Product p2 = new Product("p2")
	                    .setAmount(380)
	                    .setName("サンドイッチ");
	    OrderLine o1 = new OrderLine(new OrderLineKey(1, 0));
	    OrderLine o2 = new OrderLine(new OrderLineKey(2, 0));
	    OrderLine o3 = new OrderLine(new OrderLineKey(3, 0));
	    o1.setProduct(p1);
	    o2.setProduct(p2);
	    o3.setProduct(p1);
	    addLine(o1);
	    addLine(o2, true);
	    assertEquals(2, getOrderLines().size());
	    
	    addLine(o3, true);
	    assertEquals(2, getOrderLines().size());
	    
	    assertEquals(2, getOrderLine("p1").getQuantity());
	    assertEquals(1, getOrderLine("p2").getQuantity());
	}
	
	@Test
	public void キャンセルオーダー混在() {
	    OrderLine o1 = new OrderLine();
	    OrderLine o2 = new OrderLine();
	    OrderLine o3 = new OrderLine();
	    Order order = new Order();
	    order.addLine(o1);
	    order.addLine(o2);
	    order.addLine(o3.setCancel(true));
	    
	    assertEquals(2, order.getOrderLines(false).size());
	    assertEquals(1, order.getOrderLines(true).size());
	}
}
