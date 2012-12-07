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

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import jp.dip.komusubi.lunch.Configuration;
import jp.dip.komusubi.lunch.LunchException;
import jp.dip.komusubi.lunch.model.Group;
import jp.dip.komusubi.lunch.model.Shop;
import jp.dip.komusubi.lunch.model.User;
import jp.dip.komusubi.lunch.module.dao.GroupDao;
import jp.dip.komusubi.lunch.module.dao.ShopDao;
import jp.dip.komusubi.lunch.service.AccountService;
import jp.dip.komusubi.lunch.wicket.WicketSession;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.komusubi.common.util.Resolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * register group.
 * @author jun.ozeki
 * @since 2011/12/11
 */
public abstract class GroupRegistry extends Panel {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(GroupRegistry.class);

    /**
     * create new instance.
     * @param id
     */
    public GroupRegistry(String id) {
        super(id);
    }

    /**
     * initialize components.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        WebMarkupContainer container = new WebMarkupContainer("group.collapsed");
        container.add(new FeedbackPanel("feedback"));
        container.add(new AttributeModifier("data-collapsed", "false"));
        container.add(new GroupRegistryForm("group.form"));
        add(container);
    }

    /**
     * configure components.
     */
    @Override
    protected void onConfigure() {
        boolean visible = false;
        if (WicketSession.get().isSignedIn()) {
            User user = WicketSession.get().getSignedInUser();
            if (user.getGroup() == null)
                visible = true;
        }
        setVisibilityAllowed(visible);
    }

    /**
     * group registry form.
     * @author jun.ozeki
     * @since 2011/12/26
     */
    protected class GroupRegistryForm extends Form<Group> {

        private static final long serialVersionUID = -7593471899292529129L;
        private Group group = new Group();
        private List<Shop> selection;
        @Inject @Named("date") private Resolver<Date> dateResolver;

        /**
         * create new instance.
         * @param id
         */
        public GroupRegistryForm(String id) {
            super(id);
        }

        /**
         * initialize components.
         */
        @Override
        protected void onInitialize() {
            super.onInitialize();
            setDefaultModel(new CompoundPropertyModel<Group>(group));
            add(getCodeField("code"));
            add(getNameField("name"));
            add(getShopMultiChoice("choice.shop"));
        }

        /**
         * 
         * @param id
         * @return
         */
        private TextField<String> getCodeField(String id) {
            TextField<String> text = new TextField<String>(id);
            text.setRequired(true)
                    .add(new StringValidator.LengthBetweenValidator(2, 128))
                    .add(new PatternValidator(Pattern.compile("[a-zA-Z0-9\\.']+")))
                    .add(specificWordValidator())
                    .add(existsGroupValidator());
            return text;
        }

        /**
         * 
         * @param id
         * @return
         */
        private TextField<String> getNameField(String id) {
            TextField<String> text = new TextField<String>(id);
            text.setRequired(true)
                .add(new StringValidator.LengthBetweenValidator(2, 128));
            return text;
        }

        // specific word validator
        private AbstractValidator<String> specificWordValidator() {
            return new AbstractValidator<String>() {

                private static final long serialVersionUID = -3110131646353247925L;

                @Override
                protected void onValidate(IValidatable<String> validatable) {
                    if ("default".equals(validatable.getValue()))
                        error(validatable);
                }

                @Override
                public String resourceKey() {
                    return "wrong.value";
                }
            };
        }

        private AbstractValidator<String> existsGroupValidator() {
            return new AbstractValidator<String>() {

                private static final long serialVersionUID = 6452463346794985429L;

                @Override
                protected void onValidate(IValidatable<String> validatable) {
                    GroupDao groupDao = Configuration.getInstance(GroupDao.class);
                    if (groupDao.findByCode(validatable.getValue()) != null)
                        error(validatable);
                }

                @Override
                public String resourceKey() {
                    return "exist.group.already";
                }
            };
        }

//		private TextField<Date> getLastOrderField(String id) {
//			AbstractValidator<Date> validator;
//			String[] formats = {"H:mm"};
//			try {
//				Date minimum = DateUtils.parseDate("5:00", formats);
//				Date maximum = DateUtils.parseDate("11:00", formats);
//				validator = DateValidator.range(minimum, maximum, formats[0]);
//			} catch (ParseException e) {
//				throw new IllegalStateException(e);
//			}
//			TimeField timeField = new TimeField(id);
//			timeField.setRequired(true)
//					.add(validator);
//			return timeField;
//		}

        private ListMultipleChoice<Shop> getShopMultiChoice(String id) {
            ShopDao shopDao = Configuration.getInstance(ShopDao.class);
            List<Shop> shops;
//			if (group.getContracts().size() == 0)
            shops = shopDao.findAll();
//			else
//				shops = group.getContractedShops();
            // set multiple choice message.
            String message = getLocalizer().getString("select.shop.message", GroupRegistry.this);
            shops.add(0, new Shop("").setName(message));
            ListMultipleChoice<Shop> multipleShop = 
                    new ListMultipleChoice<Shop>(id, 
                            new PropertyModel<List<Shop>>(GroupRegistryForm.this, "selection"), shops, new ChoiceRenderer<Shop>("name", "id"));

            multipleShop.add(selectLeastValidator());

            return multipleShop;
        }

        // validator select must shop
        private AbstractValidator<List<Shop>> selectLeastValidator() {
            return new AbstractValidator<List<Shop>>() {

                private static final long serialVersionUID = 1L;

                @Override
                protected void onValidate(IValidatable<List<Shop>> validatable) {
                    if (validatable.getValue() == null || validatable.getValue().size() == 0)
                        error(validatable);
                }

                @Override
                public String resourceKey() {
                    return "must.select.shop";
                }

            };
        }

        @Override
        public void onSubmit() {
            User user = WicketSession.get().getSignedInUser();
            try {
                // clear ozeki
                for (Shop shop: selection) {
                    if (logger.isDebugEnabled())
                        logger.debug("selection shop:{}", shop);
                    group.addContract(shop);
                }
                AccountService accountService = Configuration.getInstance(AccountService.class);
                user.getHealth().setGroup(group);
                user.getHealth().setGroupJoined(dateResolver.resolve());
                accountService.referTo(user);
                // notice session
                WicketSession.get().dirty();
                onRegister();
            } catch (LunchException e) {
                // group set null again.
                user.getHealth().setGroup(null);
                user.getHealth().setGroupJoined(null);
                error(getLocalizer().getString("registry.failed", GroupRegistry.this));
                return;
            }
            info(getLocalizer().getString("registry.completed", GroupRegistry.this, Model.of(group)));
        }
    }

    /**
     * event of register.
     */
    protected abstract void onRegister();

}
