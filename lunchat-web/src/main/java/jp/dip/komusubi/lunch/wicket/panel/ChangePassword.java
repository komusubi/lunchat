/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package jp.dip.komusubi.lunch.wicket.panel;

import jp.dip.komusubi.lunch.Configuration;
import jp.dip.komusubi.lunch.module.resolver.DigestResolver;
import jp.dip.komusubi.lunch.wicket.WicketSession;
import jp.dip.komusubi.lunch.wicket.panel.util.SpecificBehavior;
import jp.lunchat.core.model.User;
import jp.lunchat.web.service.AccountService;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.komusubi.common.util.Resolver;

/**
 * change password panel.
 * @author jun.ozeki
 */
public class ChangePassword extends Panel {

    private static final long serialVersionUID = 1L;

    /**
     * create new instance.
     * @param id
     */
    public ChangePassword(String id) {
        super(id);
        add(new FeedbackPanel("feedback"));
        add(new ChangePasswordForm("password.form"));
    }

    /**
     * change password form.
     * @author jun.ozeki
     */
    private class ChangePasswordForm extends Form<Void> {

        private static final long serialVersionUID = -2270743351527003637L;
        private User user = WicketSession.get().getSignedInUser();
        private String password;
        private String confirm;
        private PasswordTextField currentField;
        private PasswordTextField passwordField;
        private PasswordTextField confirmField;

        /**
         * create new instance.
         * @param id
         */
        public ChangePasswordForm(String id) {
            super(id);
            add(currentField = newCurrentPasswordTextField("password", new PropertyModel<String>(user, "password")));
            add(passwordField = newPasswordTextField("new.password", Model.of(password)));
            add(confirmField = newPasswordTextField("confirm", Model.of(confirm)));
            add(passwordMatchValidator());
        }

        /**
         * current password field.
         * @param id
         * @param model
         * @return
         */
        private PasswordTextField newCurrentPasswordTextField(String id, IModel<String> model) {
            PasswordTextField field = newPasswordTextField(id, model);
            field.add(new AbstractValidator<String>() {

                private static final long serialVersionUID = 1L;

                @Override
                protected void onValidate(IValidatable<String> validatable) {
                    Resolver<String> resolver = new DigestResolver();
                    if (!user.getPassword().equals(resolver.resolve(validatable.getValue())))
                        error(validatable);
                }

                @Override
                protected String resourceKey() {
                    return "unmatched.current.password";
                }
            });
            return field;
        }

        /**
         * new password field.
         * @param id
         * @param model
         * @return
         */
        private PasswordTextField newPasswordTextField(String id, IModel<String> model) {
            PasswordTextField field = new PasswordTextField(id, model);
            SpecificBehavior.behavePasswordField(field);
            return field;
        }

        /**
         * validate password.
         * @return
         */
        private AbstractFormValidator passwordMatchValidator() {
            return new AbstractFormValidator() {
                private static final long serialVersionUID = 1519344365026880048L;

                @Override
                public FormComponent<?>[] getDependentFormComponents() {
                    return new FormComponent[] { passwordField, confirmField };
                }

                @Override
                public void validate(Form<?> form) {
                    if (!passwordField.getInput().equals(confirmField.getInput()))
                        error(confirmField);
                }

                @Override
                protected String resourceKey() {
                    return "unmatched.password";
                }
            };
        }

        /**
         * submit event.
         */
        @Override
        protected void onSubmit() {
            AccountService accountService = Configuration.getInstance(AccountService.class);
            // TODO call direct DigestResolver
            user.setPassword(new DigestResolver().resolve(user.getPassword()));
            accountService.modify(user);
            info(getLocalizer().getString("password.change.succeeded", ChangePassword.this));
        }

    }
}
