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
package jp.dip.komusubi.lunch.wicket;

import jp.dip.komusubi.lunch.Configuration;
import jp.dip.komusubi.lunch.model.User;
import jp.dip.komusubi.lunch.service.AccountService;
import jp.dip.komusubi.lunch.wicket.component.SimpleBrowserInfoPage;

import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WicketSession extends AuthenticatedWebSession {

	public static final String VARIATION_JQUERY_MOBILE = "jquery";
	private static final long serialVersionUID = 2537313227105289690L;
	private static final Logger logger = LoggerFactory.getLogger(WicketSession.class);
	private transient AccountService accountService = Configuration.getInstance(AccountService.class);
	private User loggedInUser;

	public static WicketSession get() {
		return (WicketSession) Session.get();
	}
	
	public WicketSession(Request request) {
		super(request);
	}

	@Override
	public boolean authenticate(final String username, final String password) {
		return accountService.signIn(username, password);
	}

	@Override
	public Roles getRoles() {
		if (isSignedIn()) {
			return new Roles(Roles.ADMIN);
		}
		return null;
	}
	
	public User getLoggedInUser() {
		if (!get().isSignedIn()) {
			if (logger.isDebugEnabled())
				logger.debug("user didn't login yet.");
			loggedInUser = null;
			return loggedInUser;
		}
		if (loggedInUser != null)
			return loggedInUser;
		String[] values = getApplication().getSecuritySettings()
							.getAuthenticationStrategy().load();
		loggedInUser = accountService.find(values[0]);
		return loggedInUser;
	}

	@Override
	public WebPage newBrowserInfoPage() {
		return new SimpleBrowserInfoPage();
	}

}
