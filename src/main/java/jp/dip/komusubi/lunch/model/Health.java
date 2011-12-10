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

	@Deprecated
	public Health(String userId) {
//		this.userId = userId;
		this(new User(userId));
	}

	public Health(User user) {
		this.user = user;
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

	@Deprecated
	public String getUserId() {
//		return userId;
		if (user == null)
			return null;
		return user.getId();
	}

	public User getUser() {
		return user;
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
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Health [login=").append(login).append(", lastLogin=").append(lastLogin)
				.append(", loginFail=").append(loginFail).append(", active=").append(active)
				.append(", user=").append(user).append("]");
		return builder.toString();
	}

}
