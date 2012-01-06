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

import org.komusubi.common.protocol.smtp.Destination;

/**
 * user.
 * @author jun.ozeki
 * @since 2011/12/11
 */
public class User implements Serializable, Destination {
	private static final long serialVersionUID = 8305012931385761901L;
	private String id;
//	private String groupId;
	private Group group;
	private String name;
	private String password;
	private String email;
	private List<Role> roles = new ArrayList<Role>();
	private Health health;

	public User() {
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
//		return groupId;
		String groupId = null;
		if (group != null)
			groupId = group.getId();
		return groupId;
	}

	public Group getGroup() {
		return group;
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

	@Deprecated
	public User setGroupId(String groupId) {
//		this.groupId = groupId;
		return this;
	}

	public User setGroup(Group group) {
		this.group = group;
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
		builder.append("User [id=").append(id).append(", group=").append(group).append(", name=")
				.append(name).append(", password=").append(password).append(", email=")
				.append(email).append(", roles=").append(roles).append(", health=").append(health)
				.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((group == null) ? 0 : group.hashCode());
		result = prime * result + ((health == null) ? 0 : health.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (group == null) {
			if (other.group != null)
				return false;
		} else if (!group.equals(other.group))
			return false;
		if (health == null) {
			if (other.health != null)
				return false;
		} else if (!health.equals(other.health))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (roles == null) {
			if (other.roles != null)
				return false;
		} else if (!roles.equals(other.roles))
			return false;
		return true;
	}
}
