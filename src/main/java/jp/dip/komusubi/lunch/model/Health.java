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

public class Health implements Serializable {

	private static final long serialVersionUID = -5968243531502612079L;
	private String userId;
	private int login;
	private Date lastLogin;
	private int loginFail;
	private boolean active;
	
	public Health(String userId) {
		this.userId = userId;
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

	public String getUserId() {
		return userId;
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

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Health [userId=").append(userId).append(", login=").append(login)
				.append(", lastLogin=").append(lastLogin).append(", loginFail=").append(loginFail)
				.append(", active=").append(active).append("]");
		return builder.toString();
	}

}
