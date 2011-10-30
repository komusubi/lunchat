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

import java.util.regex.Pattern;

import jp.dip.komusubi.lunch.LunchException;
import jp.dip.komusubi.lunch.model.User;
import jp.dip.komusubi.lunch.module.resolver.DigestResolver;
import jp.dip.komusubi.lunch.service.AccountService;
import jp.dip.komusubi.lunch.util.Nonce;
import jp.dip.komusubi.lunch.wicket.WicketSession;
import jp.dip.komusubi.lunch.wicket.page.error.ErrorPage;

import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class Profile extends Panel {

	private static final long serialVersionUID = 8916168233528784779L;
	private static final Logger logger = LoggerFactory.getLogger(Profile.class);
	@Inject 
	private transient AccountService account;
	private String segment;
	private boolean modify = false;
	
	public Profile(String id) {
		this(id, null);
		modify = true;
	}

	public Profile(String id, String segment) {
		super(id);
		this.segment = segment;
		add(new ProfileForm("profile.form"));
		add(new FeedbackPanel("feedback"));
	}

	/**
	 * profile form. 
	 * @author jun.ozeki
	 */
	private class ProfileForm extends Form<Void> {
		private static final long serialVersionUID = -752103310649689060L;
		private String confirmPassword;
		private PasswordTextField password;
		private PasswordTextField confirm;
		private EmailTextField email;
		private User user = new User();
				
		public ProfileForm(String id) {
			super(id);
			if (user == null)
				throw new LunchException("user is null");
			this.setDefaultModel(new CompoundPropertyModel<User>(user));
			add(behaveId(new TextField<String>("id")));
			add(behaveName(new TextField<String>("name")));
			add(behavePassword(password = new PasswordTextField("password")));
			add(behavePassword(confirm = new PasswordTextField("confirm", 
											new PropertyModel<String>(this, "confirmPassword"))));
			add(behaveEmail(email = new EmailTextField("email")));
			add(passwordMatchValidator());
			if (!modify)
				add(confirmDigestValidator());
		}

		private FormComponent<String> behaveId(TextField<String> text) {
//			new FormComponentFeedbackBorder(text)
			text.add(StringValidator.lengthBetween(3, 64))
				.add(new PatternValidator(Pattern.compile("[a-zA-Z0-9\\.']+")))
				.setRequired(true);
			return text;
		}
		private TextField<String> behaveName(TextField<String> name) {
			name.add(StringValidator.lengthBetween(3, 128))
				.setRequired(true);
			return name;
		}
		private PasswordTextField behavePassword(PasswordTextField password) {
			password.add(StringValidator.minimumLength(8))
				.setRequired(true);
			return password;
		}
		private EmailTextField behaveEmail(EmailTextField email) {
			email.setRequired(true);
			return email;
		}
		private AbstractFormValidator passwordMatchValidator() {
			return new AbstractFormValidator() {
				private static final long serialVersionUID = 1L;

				public FormComponent<?>[] getDependentFormComponents() {
					return new FormComponent[]{password, confirm};					
				}

				public void validate(Form<?> form) {
					if (!password.getInput().equals(confirm.getInput()))
						error(confirm);
				}
				@Override
				public String resourceKey() {
					return "unmatched.password";
				}
			};
		}
		private AbstractFormValidator confirmDigestValidator() {
			return new AbstractFormValidator() {

				private static final long serialVersionUID = 7983637295986009346L;

				public FormComponent<?>[] getDependentFormComponents() {
					return new FormComponent[]{email};
				}

				public void validate(Form<?> form) {
					Nonce nonce = (Nonce) WicketSession.get().getAttribute(Nonce.class.getName());
					if (nonce == null) { // over session time.
						logger.info("nonce is null session time over");
//						error(email);
						setResponsePage(new ErrorPage("session time over"));
						return;
					}
					if (!segment.equals(nonce.get(email.getInput()))) {
						if (logger.isDebugEnabled())
							logger.debug("segment is {}, nonce is {}", 
									segment, nonce.get(email.getInput()));
						error(email);	
					}
				}
				@Override
				public String resourceKey() {
					return "unmatched.digest";
				}
			};
			// TODO profile 修正時のValidatorを別途作成
//			private AbstractFormValidator confirm
		}
		@Override
		public void onSubmit() {
			user.getHealth().setActive(true);
			//TODO  Hash値はここで設定する。saltはどうする？
			user.setPassword(new DigestResolver().resolve(confirmPassword));
			account.create(user);
			info(getString("registry.completed", new Model<User>(user)));
		}
		
	}
}
