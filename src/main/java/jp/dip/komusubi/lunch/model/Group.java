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
import java.util.HashSet;
import java.util.Set;

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

	private static final long serialVersionUID = 9163879003361362197L;
	private String id;
	private String name;
	private Date lastOrder;
	private Set<Contract> contracts;
	private Set<User> users;

	public Group(String id) {
		this.id = id;
	}

	public Group addContract(Contract contract) {
		if (contracts == null)
			contracts = new HashSet<>();
		contracts.add(contract);
		return this;
	}

	public Set<User> getUsers() {
		if (users == null) {
			// FIXME should fix proxy pattern.
			users = new HashSet<>();
			UserDao userDao = Configuration.getInstance(UserDao.class);
			users.addAll(userDao.findByGroupId(getId()));
		}
		return users;
	}
	
	public Set<Contract> getContracts() {
		if (contracts == null) {
			// FIXME refer to module package. should fix proxy pattern.
			ContractDao contractDao = Configuration.getInstance(ContractDao.class);
			contracts = new HashSet<>();
			contracts.addAll(contractDao.findByGroupId(getId()));
		}
		return contracts;
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

	public Group setContracts(Set<Contract> contracts) {
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Group [id=").append(id).append(", name=").append(name)
				.append(", lastOrder=").append(lastOrder).append(", contracts=").append(contracts)
				.append("]");
		return builder.toString();
	}

}
