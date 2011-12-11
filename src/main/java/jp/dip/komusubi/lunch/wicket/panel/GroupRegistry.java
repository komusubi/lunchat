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

import java.util.ArrayList;
import java.util.Calendar;
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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * group registry.
 * @author jun.ozeki
 * @since 2011/12/11
 */
public class GroupRegistry extends Panel {

	private static final long serialVersionUID = 5865881679606159636L;
	private static final Logger logger = LoggerFactory.getLogger(GroupRegistry.class);
	
	public GroupRegistry(String id) {
		super(id);
		add(new FeedbackPanel("feedback"));
		add(new GroupRegistryForm("group.form"));
	}

	private class GroupRegistryForm extends Form<Void> {

		private static final long serialVersionUID = -7593471899292529129L;
		private Group group = new Group(null);
		
		public GroupRegistryForm(String id) {
			super(id);
			setDefaultModel(new CompoundPropertyModel<Group>(group));
			add(getIdField("id"));
			add(getNameField("name"));
			add(getLastOrderField("lastOrder"));
			add(getShopList("shop.item"));
		}

		private TextField<String> getIdField(String id) {
			TextField<String> text = new TextField<String>(id);
			text.setRequired(true)
				.add(new StringValidator.LengthBetweenValidator(3, 64))
				.add(getSpecificWordValidator());
			return text;
		}
		
		private TextField<String> getNameField(String id) {
			TextField<String> text = new TextField<String>(id);
			text.setRequired(true)
				.add(new StringValidator.LengthBetweenValidator(3, 64));
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
			TextField<Date> dateField = new TextField<Date>(id);
			dateField.setRequired(true);
			return dateField;
		}
		
		private ListView<Shop> getShopList(String id) {
			ShopDao shopDao = Configuration.getInstance(ShopDao.class);
			List<Shop> shops = shopDao.findAll();
			return new ListView<Shop>(id, shops) {

				private static final long serialVersionUID = -1533834060618470935L;

				@Override
				protected void populateItem(ListItem<Shop> item) {
					Shop shop = item.getModelObject();
					item.add(new Label("shop.name", shop.getName()));
				}
				
			}.setReuseItems(true);
		}
		
		@Override
		public void onSubmit() {
			if (group.getLastOrder() == null) {
				// FIXME set last order date(time)
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, 10);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				group.setLastOrder(cal.getTime());
			}
			User user = WicketSession.get().getLoggedInUser();
			try {
				AccountService accountService = Configuration.getInstance(AccountService.class);
				user.setGroup(group);
				accountService.referTo(user);
			} catch (LunchException e) {
				// group set null again.
				user.setGroup(null); 
				error(getLocalizer().getString("registry.failed", GroupRegistry.this));
				return;
			}
			info(getLocalizer().getString("registry.completed", GroupRegistry.this, Model.of(group)));
		}
	}

}
