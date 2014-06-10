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
package jp.lunchat.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.komusubi.common.protocol.smtp.Destination;

/**
 * user.
 * @author jun.ozeki
 * @since 2011/12/11
 */
public class User implements Serializable, Destination {
	private static final long serialVersionUID = 8305012931385761901L;
	private String email;
	private Integer id;
	private String name;
	private String nickname;
	private String password;
	private Date joined;
	private List<Notice> notices;
	private List<Role> roles = new ArrayList<Role>();
	
	private Health health;

	public User() {
		this.health = new Health(this);
	}

	public User(Health health) {
	    if (health == null)
	        throw new IllegalArgumentException("health must NOT be null.");
		this.health = health;
	}
	
	public User(Integer id) {
		this();
		this.id = id;
	}

	public User addRoles(Role role) {
		roles.add(role);
		return this;
	}

	@Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof User))
            return false;
        User other = (User) obj;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
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
        if (joined == null) {
            if (other.joined != null)
                return false;
        } else if (!joined.equals(other.joined))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (nickname == null) {
            if (other.nickname != null)
                return false;
        } else if (!nickname.equals(other.nickname))
            return false;
        if (notices == null) {
            if (other.notices != null)
                return false;
        } else if (!notices.equals(other.notices))
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

	public String getEmail() {
		return email;
	}

	public Group getGroup() {
		if (health == null)
			return null;
		return health.getGroup();
	}

	public Integer getGroupId() {
//		return groupId;
		Group group = getGroup();
		Integer groupId = null;
		if (group != null)
			groupId = group.getId();
		return groupId;
	}

	public Health getHealth() {
		return health;
	}

	public Integer getId() {
		return id;
	}

	public Date getJoined() {
		return joined;
	}

	public String getName() {
		return name;
	}

	public String getNickname() {
		return nickname;
	}

	public String getPassword() {
		return password;
	}

	public List<Role> getRoles() {
		return roles;
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((health == null) ? 0 : health.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((joined == null) ? 0 : joined.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((nickname == null) ? 0 : nickname.hashCode());
        result = prime * result + ((notices == null) ? 0 : notices.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((roles == null) ? 0 : roles.hashCode());
        return result;
    }

	public boolean hasRole(Role role) {
		return roles.contains(role);
	}

//	public User setId(String id) {
//		this.id = id;
//		this.health.setUserId(id);
//		return this;
//	}

	public User setEmail(String email) {
		this.email = email;
		return this;
	}

	public User setGroup(Group group) {
//		this.group = group;
		health.setGroup(group);
		return this;
	}

	@Deprecated
	public User setGroupId(String groupId) {
//		this.groupId = groupId;
		return this;
	}

	@Deprecated
	public User setHealth(Health health) {
		this.health = health;
		return this;
	}

//	public User setId(Integer id) {
//	    this.id = id;
//	    return this;
//	}

	public User setJoined(Date joined) {
		this.joined = joined;
		return this;
	}

	public User setName(String name) {
		this.name = name;
		return this;
	}

	public User setNickname(String nickname) {
		this.nickname = nickname;
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
		builder.append("User [email=").append(email).append(", id=").append(id).append(", name=")
				.append(name).append(", nickname=").append(nickname).append(", password=")
				.append(password).append(", joined=").append(joined).append(", notices=")
				.append(notices).append(", roles=").append(roles).append(", health=")
				.append(health).append("]");
		return builder.toString();
	}
}
