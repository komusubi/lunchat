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
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * health.
 * @author jun.ozeki
 * @since 2011/12/05
 */
public class Health implements Serializable {

	private static final long serialVersionUID = -5968243531502612079L;
	private static final Logger logger = LoggerFactory.getLogger(Health.class);
//	private String userId;
	private int login;
	private Date lastLogin;
	private int loginFail;
	private boolean active;
	private User user;
	private String admitter;
	private Group group;
	private Date groupJoined;

//	@Deprecated
//	public Health(String userId) {
//		this.userId = userId;
//		this(new User(userId));
//	}

	public Health() {
	    
	}
	
	public Health(User user) {
		this.user = user;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Health))
			return false;
		Health other = (Health) obj;
		if (active != other.active)
			return false;
		if (admitter == null) {
			if (other.admitter != null)
				return false;
		} else if (!admitter.equals(other.admitter))
			return false;
		if (group == null) {
			if (other.group != null)
				return false;
		} else if (!group.equals(other.group))
			return false;
		if (groupJoined == null) {
			if (other.groupJoined != null)
				return false;
		} else if (!groupJoined.equals(other.groupJoined))
			return false;
		if (lastLogin == null) {
			if (other.lastLogin != null)
				return false;
		} else if (!lastLogin.equals(other.lastLogin))
			return false;
		if (login != other.login)
			return false;
		if (loginFail != other.loginFail)
			return false;
		return true;
	}

	public String getAdmitter() {
		return admitter;
	}

	public Group getGroup() {
		return group;
	}

	public Date getGroupJoined() {
		return groupJoined;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public int getLogin() {
		return login;
	}

	public int getLoginFail() {
		return loginFail;
	}

	public User getUser() {
		return user;
	}

	@Deprecated
	public Integer getUserId() {
//		return userId;
		if (user == null)
			return null;
		return user.getId();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (active ? 1231 : 1237);
		result = prime * result + ((admitter == null) ? 0 : admitter.hashCode());
		result = prime * result + ((group == null) ? 0 : group.hashCode());
		result = prime * result + ((groupJoined == null) ? 0 : groupJoined.hashCode());
		result = prime * result + ((lastLogin == null) ? 0 : lastLogin.hashCode());
		result = prime * result + login;
		result = prime * result + loginFail;
		return result;
	}

	public Health incrementLogin() {
		setLogin(getLogin() + 1);
		return this;
	}

	public Health incrementLoginFail() {
		setLoginFail(getLoginFail() + 1);
		return this;
	}

	public boolean isActive() {
		return active;
	}

	public Health setActive(boolean active) {
		this.active = active;
		return this;
	}

	public Health setAdmitter(String admitter) {
		this.admitter = admitter;
		return this;
	}

	public Health setGroup(Group group) {
		this.group = group;
		return this;
	}

	public Health setGroupJoined(Date groupJoined) {
		this.groupJoined = groupJoined;
		return this;
	}

	public Health setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
		return this;
	}

	public Health setLogin(int login) {
		this.login = login;
		return this;
	}

	public Health setLoginFail(int loginFail) {
		this.loginFail = loginFail;
		return this;
	}

	@Deprecated
	public void setUserId(String userId) {
//		this.userId = userId;
		// nothing to do
		logger.warn("nothing to do Health#setUserId:{}", userId);
		throw new IllegalArgumentException("can NOT set userId : " + userId);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Health [login=").append(login).append(", lastLogin=").append(lastLogin)
				.append(", loginFail=").append(loginFail).append(", active=").append(active)
				.append(", admitter=").append(admitter).append(", group=").append(group)
				.append(", groupJoined=").append(groupJoined).append("]");
		return builder.toString();
	}

}
