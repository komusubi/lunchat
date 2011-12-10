/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package jp.dip.komusubi.lunch.model;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Shop implements Serializable {

//	/**
//	 * contract.
//	 * @author jun.ozeki
//	 * @since 2011/12/04
//	 */
//	public static class Contract implements Serializable {
//
//		private static final long serialVersionUID = 2158869251958947749L;
//		private int id;
//		private Group group;
//		private Date contracted;
//
//		public Contract(int id) {
//			this.id = id;
//		}
//		
//		public Date getContracted() {
//			return contracted;
//		}
//
//		public Group getGroup() {
//			return group;
//		}
//
//		public int getId() {
//			return id;
//		}
//
//		public void setContracted(Date contracted) {
//			this.contracted = contracted;
//		}
//
//		public void setGroup(Group group) {
//			this.group = group;
//		}
//
//		@Override
//		public String toString() {
//			StringBuilder builder = new StringBuilder();
//			builder.append("Contract [id=").append(id).append(", group=").append(group)
//					.append(", contracted=").append(contracted).append("]");
//			return builder.toString();
//		}
//
//	}
	private static final long serialVersionUID = -1020122183352301083L;
	private String id;
	private String name;
	// private GeoLocation geoLocation;
	private String url;
	private String phoneNumber;
	private Date lastOrder;
	private Set<Contract> contracts;

	public Shop(String id) {
		this.id = id;
	}

	public Shop addContracts(Contract contract) {
		if (contracts == null)
			contracts = new HashSet<>();
		this.contracts.add(contract);
		return this;
	}

	public Set<Contract> getContracts() {
		if (contracts == null)
			contracts = new HashSet<>();
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

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getUrl() {
		return url;
	}

	public URL getURL() {
		URL url = null;
		try {
			url = new URL(this.url);
		} catch (MalformedURLException e) {
			// ignore
		}
		return url;
	}

	public Shop setContracts(Set<Contract> contracts) {
		this.contracts = contracts;
		return this;
	}

	public Shop setLastOrder(Date lastOrder) {
		this.lastOrder = lastOrder;
		return this;
	}

	public Shop setName(String name) {
		this.name = name;
		return this;
	}

	public Shop setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
		return this;
	}

	public Shop setUrl(String url) {
		this.url = url;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Shop [id=").append(id).append(", name=").append(name).append(", url=")
				.append(url).append(", phoneNumber=").append(phoneNumber).append(", lastOrder=")
				.append(lastOrder).append(", contracts=").append(contracts).append("]");
		return builder.toString();
	}
}
