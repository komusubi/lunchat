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

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import jp.dip.komusubi.lunch.Configuration;
import jp.dip.komusubi.lunch.LunchException;
import jp.dip.komusubi.lunch.model.Group;
import jp.dip.komusubi.lunch.model.Shop;
import jp.dip.komusubi.lunch.model.User;
import jp.dip.komusubi.lunch.module.dao.ShopDao;
import jp.dip.komusubi.lunch.service.AccountService;
import jp.dip.komusubi.lunch.wicket.WicketSession;
import jp.dip.komusubi.lunch.wicket.component.TimeField;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
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
import org.apache.wicket.request.Response;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.DateValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * group registry.
 * @author jun.ozeki
 * @since 2011/12/11
 */
public abstract class GroupRegistry extends Panel {

	private static final long serialVersionUID = 5865881679606159636L;
	private static final Logger logger = LoggerFactory.getLogger(GroupRegistry.class);
	
	public GroupRegistry(String id) {
		super(id);
		add(getMarkupContainer("group.collapsed"));
	}

	private WebMarkupContainer getMarkupContainer(String id) {
		WebMarkupContainer container = new WebMarkupContainer(id);
		container.add(new AttributeModifier("data-collapsed", "false"));
		container.add(new FeedbackPanel("feedback"));
		container.add(new GroupRegistryForm("group.form"));
		return container;
	}

	@Override
	public void onConfigure() {
		boolean visible = false;
		if (WicketSession.get().isSignedIn()) {
			User user = WicketSession.get().getLoggedInUser();
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
	protected class GroupRegistryForm extends Form<Void> {

		private static final long serialVersionUID = -7593471899292529129L;
		private Group group = new Group(null);
		private List<Shop> selection;
		
		public GroupRegistryForm(String id) {
			super(id);
			setDefaultModel(new CompoundPropertyModel<Group>(group));
			add(getIdField("id"));
			add(getNameField("name"));
			add(getLastOrderField("lastOrder"));
//			add(getShopList("shop.item"));
			add(getShopMultiChoice("choice.shop"));
		}

		private TextField<String> getIdField(String id) {
			TextField<String> text = new TextField<String>(id);
			text.setRequired(true)
				.add(new StringValidator.LengthBetweenValidator(2, 64))
				.add(getSpecificWordValidator());
			return text;
		}
		
		private TextField<String> getNameField(String id) {
			TextField<String> text = new TextField<String>(id);
			text.setRequired(true)
				.add(new StringValidator.LengthBetweenValidator(2, 64));
			return text;
		}

		private AbstractValidator<String> getSpecificWordValidator() {
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
		
		private TextField<Date> getLastOrderField(String id) {
			AbstractValidator<Date> validator;
			String[] formats = {"H:mm"};
			try {
				Date minimum = DateUtils.parseDate("5:00", formats);
				Date maximum = DateUtils.parseDate("11:00", formats);
				validator = DateValidator.range(minimum, maximum, formats[0]);
			} catch (ParseException e) {
				throw new IllegalStateException(e);
			}
			TimeField timeField = new TimeField(id);
			timeField.setRequired(true)
					.add(validator);
			return timeField;
		}
		
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
			ListMultipleChoice<Shop> multipleShop = new ListMultipleChoice<Shop>(id, 
					new PropertyModel<List<Shop>>(GroupRegistryForm.this, "selection"), 
					shops, 
					new ChoiceRenderer<Shop>("name", "id"));

			return multipleShop;
		}
		
		@Override
		public void onSubmit() {
			User user = WicketSession.get().getLoggedInUser();
			try {
				for (Shop shop: selection) {
					if (logger.isDebugEnabled())
						logger.debug("selection shop:{}", shop);
					group.addContract(shop);
				}
				AccountService accountService = Configuration.getInstance(AccountService.class);
				user.setGroup(group);
				accountService.referTo(user);
				// notice session
				WicketSession.get().dirty();
				onRegistered();
			} catch (LunchException e) {
				// group set null again.
				user.setGroup(null); 
				error(getLocalizer().getString("registry.failed", GroupRegistry.this));
				return;
			}
			info(getLocalizer().getString("registry.completed", GroupRegistry.this, Model.of(group)));
		}
	}

	protected abstract void onRegistered();

}
