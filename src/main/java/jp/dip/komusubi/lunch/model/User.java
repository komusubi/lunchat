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
import java.util.ArrayList;
import java.util.List;

import jp.dip.komusubi.common.protocol.smtp.Destination;

public class User implements Serializable, Destination {

	private static final long serialVersionUID = 8305012931385761901L;
	private String id;
	private String groupId;
	private String name;
	private String password;
	private String email;
	private List<Role> roles = new ArrayList<Role>();
	private Health health;

	public User() {
//		this(new Health((User) null));
		this.health = new Health(this);
	}

	public User(Health health) {
		this.health = health;
	}
	
	public User(String id) {
		this();
		this.id = id;
	}

	public User addRoles(Role role) {
		roles.add(role);
		return this;
	}

	public String getEmail() {
		return email;
	}

	public String getGroupId() {
		return groupId;
	}

	public Health getHealth() {
		return health;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public boolean hasRole(Role role) {
		return roles.contains(role);
	}

	public User setEmail(String email) {
		this.email = email;
		return this;
	}

	public User setGroupId(String groupId) {
		this.groupId = groupId;
		return this;
	}

	@Deprecated
	public User setHealth(Health health) {
		this.health = health;
		return this;
	}

//	public User setId(String id) {
//		this.id = id;
//		this.health.setUserId(id);
//		return this;
//	}

	public User setName(String name) {
		this.name = name;
		return this;
	}

	public User setPassword(String password) {
		this.password = password;
		return this;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [id=").append(id).append(", groupId=").append(groupId)
				.append(", name=").append(name).append(", password=").append(password)
				.append(", email=").append(email).append(", roles=").append(roles)
				.append(", health=").append(health).append("]");
		return builder.toString();
	}
}
