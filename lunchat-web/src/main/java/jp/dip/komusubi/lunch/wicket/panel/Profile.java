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

import java.util.Date;

import jp.dip.komusubi.lunch.Configuration;
import jp.dip.komusubi.lunch.module.resolver.DigestResolver;
import jp.dip.komusubi.lunch.util.Nonce;
import jp.dip.komusubi.lunch.wicket.WicketSession;
import jp.dip.komusubi.lunch.wicket.panel.util.SpecificBehavior;
import jp.lunchat.LunchatException;
import jp.lunchat.core.model.User;
import jp.lunchat.storage.dao.UserDao;
import jp.lunchat.web.service.AccountService;

import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.StringValidator;
import org.komusubi.common.util.Resolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class Profile extends Panel {

	private static final long serialVersionUID = 8916168233528784779L;
	private static final Logger logger = LoggerFactory.getLogger(Profile.class);
	@Inject private AccountService account;
	@Inject @Named("date") private Resolver<Date> dateResolver;
	@Inject private Nonce nonce;
//	private String fragment;
	
	public Profile(String id) {
		this(id, null, true);
	}
	
	public Profile(String id, IModel<String> model) {
		this(id, model, false);
	}
	
	public Profile(String id, IModel<String> model, boolean modify) {
		super(id, model);
//		this.fragment = fragment;
		if (modify)
			add(new ModificationProfileForm("profile.form"));
		else
			add(new RegistryProfileForm("profile.form", model));
		add(new FeedbackPanel("feedback"));
	}

	/**
	 * profile form. 
	 * @author jun.ozeki
	 */
	private abstract class ProfileForm extends Form<Void> {
		private static final long serialVersionUID = -752103310649689060L;
		private String confirmPassword;
		private TextField<String> nickname;
		private PasswordTextField password;
		private PasswordTextField confirm;
		private EmailTextField email;
		private User user;
		
		// user initialize decide subclass.
		protected abstract User loadUser();
		
		public ProfileForm(String id) {
			super(id);
			user = loadUser();
			this.setDefaultModel(new CompoundPropertyModel<User>(user));
			add(behaveNickname(nickname = new TextField<String>("nickname")));
			add(behaveName(new TextField<String>("name")));
			add(behavePassword(password = new PasswordTextField("password")));
			add(behavePassword(confirm = new PasswordTextField("confirm", 
											new PropertyModel<String>(this, "confirmPassword"))));
			add(behaveEmail(email = new EmailTextField("email")));
			add(passwordMatchValidator());
			add(existsNicknameValidator());
		}

		private FormComponent<String> behaveNickname(TextField<String> text) {
//			new FormComponentFeedbackBorder(text)
			text = SpecificBehavior.behaveIdField(text);
			return text;
		}
		private TextField<String> behaveName(TextField<String> name) {
			name.add(StringValidator.lengthBetween(3, 128))
				.setRequired(true);
			return name;
		}
		private PasswordTextField behavePassword(PasswordTextField password) {
			password = SpecificBehavior.behavePasswordField(password);
			return password;
		}
		private EmailTextField behaveEmail(EmailTextField email) {
			email.setRequired(true);
			return email;
		}

		private AbstractFormValidator passwordMatchValidator() {
			return new AbstractFormValidator() {
				private static final long serialVersionUID = 7808853607014331335L;
				
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
		// exist nickname ?
		private AbstractFormValidator existsNicknameValidator() {
		    return new AbstractFormValidator() {

                private static final long serialVersionUID = -6400952775790379058L;

                @Override
                public FormComponent<?>[] getDependentFormComponents() {
                    return new FormComponent[]{nickname};
                }

                @Override
                public void validate(Form<?> form) {
                    UserDao userDao = Configuration.getInstance(UserDao.class);
                    if (userDao.findByNickname(nickname.getInput()) != null)
                        error(nickname);
                }
                @Override
                public String resourceKey() {
                    return "nickname.has.existed.already";
                }
		        
		    };
		}
		
/*
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
//			protected 
		}
 */			
		protected User getUser() {
			return user;
		}
		protected AccountService getAccountService() {
			return account;
		}
		protected String getConfirmPassword() {
			return confirmPassword;
		}
	}
	
	/**
	 * registry profile form. 
	 * @author jun.ozeki
	 * @since 2011/11/03
	 */
	private class RegistryProfileForm extends ProfileForm {
		
		private static final long serialVersionUID = 6156893782920074142L;
		
		public RegistryProfileForm(String id, IModel<String> model) {
			super(id);
			add(confirmDigestValidator(model));
		}

		protected User loadUser() {
			return new User();
		}
		
		@SuppressWarnings("unchecked")
		private AbstractFormValidator confirmDigestValidator(final IModel<String> model) {
			
			return new AbstractFormValidator() {
				
				private static final long serialVersionUID = 7983637295986009346L;
				// get email field. 
				FormComponent<String> emailField = (FormComponent<String>) get("email");

				public FormComponent<?>[] getDependentFormComponents() {
					return new FormComponent[]{emailField};
				}

				public void validate(Form<?> form) {
					/*
					Nonce nonce = (Nonce) WicketSession.get().getAttribute(Nonce.class.getName());
					if (nonce == null) { // over session time.
						logger.info("nonce is null session time over");
//						error(email);
						setResponsePage(new ErrorPage("session time over"));
						return;
					}
					*/
					// FIXME literal attribute name.
					String salt = (String) WicketSession.get().getAttribute("salt");
					String fragment = model.getObject();
					if (!fragment.equals(nonce.get(emailField.getInput(), salt))) {
						if (logger.isDebugEnabled())
							logger.debug("segment is {}, nonce is {}", 
									fragment, nonce.get(emailField.getInput()));
						error(emailField);	
					}
				}
				@Override
				public String resourceKey() {
					return "unmatched.digest";
				}
			};
		}
		@Override
		public void onSubmit() {
			User user = getUser();
			user.getHealth().setActive(true);
			//TODO  Hash値はここで設定する。saltはどうする？
			user.setPassword(new DigestResolver().resolve(getConfirmPassword()));
			user.setJoined(dateResolver.resolve());
			try {
				getAccountService().create(user);
			} catch (LunchatException e) {
				error(getString("registry.failed"));
				return;
			}
			info(getString("registry.completed", new Model<User>(user)));
		}
	}
	
	/**
	 * modification profile form. 
	 * @author jun.ozeki
	 * @since 2011/11/03
	 */
	private class ModificationProfileForm extends ProfileForm {

		private static final long serialVersionUID = -7126417537628446596L;
		
		public ModificationProfileForm(String id) {
			super(id);
			get("id").setEnabled(false);
		}
	
		protected User loadUser() {
			User user = WicketSession.get().getSignedInUser();
			if (user == null)
				user = new User();
			return user;
		}
		
		@Override
		public void onSubmit() {
			
			logger.info("modification form {}", getUser());
		}
	}
}
