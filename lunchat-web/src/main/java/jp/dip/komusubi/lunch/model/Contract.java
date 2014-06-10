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
package jp.dip.komusubi.lunch.model;

import java.io.Serializable;
import java.util.Date;

/**
 * contract.
 * @author jun.ozeki
 * @since 2011/12/03
 */
public class Contract implements Serializable {
	
	private static final long serialVersionUID = 7667124644145815969L;
	public static final Integer DEFAULT_ID = -1;
	private Integer id;
	private Group group;
//	private String groupId;
	private Shop shop;
//	private String shopId;
	private Date contracted;

	public Contract() {
		this(DEFAULT_ID);
	}
	
	public Contract(Integer id) {
		this.id = id;
	}
	
	public Contract(Integer id, Shop shop, Group group) {
	    this(id);
	    setShop(shop);
	    setGroup(group);
	    setContracted(new Date());
	}
	
	public Contract(Shop shop, Group group) {
	    this(DEFAULT_ID, shop, group);
	}
	
	public Date getContracted() {
		return contracted;
	}

	public Group getGroup() {
		return group;
	}
//	public String getGroupId() {
//		return groupId;
//	}
	
	public Integer getId() {
		return id;
	}

	public Shop getShop() {
		return shop;
	}
//	public String getShopId() {
//		return this.shopId;
//	}

	public Contract setContracted(Date contracted) {
		this.contracted = contracted;
		return this;
	}

	public Contract setGroup(Group group) {
		this.group = group;
		return this;
	}
//	public Contract setGroupId(String groupId) {
//		this.groupId = groupId;
//		return this;
//	}

	public Contract setShop(Shop shop) {
		this.shop = shop;
		return this;
	}
	
//	public Contract setShopId(String shopId) {
//		this.shopId = shopId;
//		return this;
//	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Contract [id=").append(id).append(", groupId=").append(group == null ? "null" : group.getId())
				.append(", shopId=").append(shop == null ? "null" : shop.getId()).append(", contracted=").append(contracted)
				.append("]");
		return builder.toString();
	}

}
