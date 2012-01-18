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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jp.dip.komusubi.lunch.Configuration;
import jp.dip.komusubi.lunch.module.dao.ContractDao;
import jp.dip.komusubi.lunch.module.dao.UserDao;

/**
 * group.
 * 
 * @author jun.ozeki
 * @since 2011/12/03
 */
public class Group implements Serializable {

	public static final String DEFAULT_ID = "default";
	private static final long serialVersionUID = 9163879003361362197L;
	private String id;
	private String name;
	private Date lastOrder;
	private String phoneNumber;
	private List<Contract> contracts;
	private List<User> users;

	public Group() {
		this(DEFAULT_ID);
	}
	
	public Group(String id) {
		this.id = id;
	}

	public Group addContract(Shop shop) {
		if (contracts == null)
			contracts = new ArrayList<>();
		Contract contract = new Contract();
		contract.setGroup(this)
				.setShop(shop)
				.setContracted(new Date());
		addContract(contract);
		return this;
	}
	
	public Group addContract(Contract contract) {
		if (contracts == null)
//			contracts = new HashSet<>();
			contracts = new ArrayList<>();
		contracts.add(contract);
		return this;
	}

	public List<Contract> getContracts() {
		if (contracts == null) {
			// FIXME refer to module package. should fix proxy pattern.
			ContractDao contractDao = Configuration.getInstance(ContractDao.class);
			contracts = contractDao.findByGroupId(getId());
//			contracts = new HashSet<>();
//			contracts.addAll(contractDao.findByGroupId(getId()));
		}
		return contracts;
	}

	public List<Shop> getContractedShops() {
		List<Shop> shops = null;
		for (Contract contract: getContracts()) {
			if (shops == null)
				shops = new ArrayList<>();
			shops.add(contract.getShop());
		}
		if (shops == null)
			shops = Collections.emptyList();
		return shops;
	}
	
	public String getId() {
		return id;
	}

	public Date getLastOrder() {
		return lastOrder;
	}

	public String getName() {
		return name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public List<User> getUsers() {
		if (users == null) {
			// FIXME should fix proxy pattern.
			UserDao userDao = Configuration.getInstance(UserDao.class);
			users = userDao.findByGroupId(getId());
//			users.addAll(userDao.findByGroupId(getId()));
		}
		return users;
	}

	public Group setContracts(List<Contract> contracts) {
		this.contracts = contracts;
		return this;
	}

	public Group setLastOrder(Date lastOrder) {
		this.lastOrder = lastOrder;
		return this;
	}

	public Group setName(String name) {
		this.name = name;
		return this;
	}

	public Group setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Group [id=").append(id).append(", name=").append(name)
				.append(", lastOrder=").append(lastOrder).append(", phoneNumber=")
				.append(phoneNumber).append(", contracts=").append(contracts).append(", users=")
				.append(users).append("]");
		return builder.toString();
	}
}
