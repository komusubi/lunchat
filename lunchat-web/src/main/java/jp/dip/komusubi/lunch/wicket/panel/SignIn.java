/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package jp.dip.komusubi.lunch.wicket.panel;

import java.text.MessageFormat;

import jp.dip.komusubi.lunch.Configuration;
import jp.dip.komusubi.lunch.service.AccountService;
import jp.dip.komusubi.lunch.util.Nonce;
import jp.dip.komusubi.lunch.wicket.WicketSession;
import jp.dip.komusubi.lunch.wicket.page.Reminder;
import jp.dip.komusubi.lunch.wicket.panel.util.SpecificBehavior;
import jp.lunchat.core.model.User;

import org.apache.commons.lang3.Validate;
import org.apache.wicket.authroles.authentication.panel.SignInPanel;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * sign in.
 * @author jun.ozeki
 * @since 2011/12/06
 */
public class SignIn extends SignInPanel {

	private static final long serialVersionUID = 4834818596146852372L;
	private static final Logger logger = LoggerFactory.getLogger(SignIn.class);
	private String requestedNonce;
	private boolean activate;
	
	public SignIn(String id) {
		super(id, false);
	}
	
	public SignIn(String id, String requestedNonce) {
		this(id);
		Validate.notEmpty(requestedNonce);
		this.requestedNonce = requestedNonce;
		this.activate = true;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void onInitialize() {

		// wicket id from sign in component, but actual value is email address. 
		FormComponent<String> text = (FormComponent<String>) getForm().get("username");
		// TODO change email field, cann't understand swap model of "username".
//		getForm().remove(text);
//		EmailTextField email;
//		getForm().add(email = new EmailTextField("username", Model.of(getUsername())));
		FormComponent<String> password = (FormComponent<String>) getForm().get("password");
		// remember cookie
		setRememberMe(false);
		
		SpecificBehavior.behaveEmailField(text);
//		SpecificBehavior.behaveEmailField(email);
		SpecificBehavior.behavePasswordField(password);
		// activate again ?
		if (activate) {
			text.add(new ActivateValidator(requestedNonce));
		} 
		super.onInitialize();
	}
	
	/**
	 * activate validator.
	 * @author jun.ozeki
	 * @since 2011/11/12
	 */
	private static class ActivateValidator extends AbstractValidator<String> {

		private static final long serialVersionUID = 2006243876877470070L;

		private String requestedNonce;
		public ActivateValidator(String requestedNonce) {
			this.requestedNonce = requestedNonce;
		}
		
		@Override
		protected void onValidate(IValidatable<String> validatable) {
			AccountService accountService = Configuration.getInstance(AccountService.class);
			Nonce nonce = (Nonce) WicketSession.get().getAttribute(Nonce.class.getName());
			String value = validatable.getValue();
			if (!accountService.activate(value, nonce, requestedNonce)) {
				error(validatable);
			}
			// clear nonce object in session. prevent for replay attacks.
			WicketSession.get().setAttribute(Nonce.class.getName(), null);
		}
		@Override
		protected String resourceKey() {
			return "activate.failed";
		}
	}
	
	@Override
	protected void onSignInFailed() {
		AccountService accountService = Configuration.getInstance(AccountService.class);
		User user = accountService.find(getUsername());
		if (user != null && !user.getHealth().isActive()) {
			get("feedback").setEscapeModelStrings(false);
			
			String path = urlFor(Reminder.class, null).toString();
			String urlBase = getRequestCycle().getUrlRenderer().renderFullUrl(getRequest().getClientUrl());
			String url = RequestUtils.toAbsolutePath(urlBase, path);
			error(MessageFormat.format(getString("user.not.active"), user.getName(), url));
		} else if (activate) {
			error(getLocalizer().getString("login.failed.after.activate.sccessed", SignIn.this));
		} else {
			super.onSignInFailed();
		}
	}
}
