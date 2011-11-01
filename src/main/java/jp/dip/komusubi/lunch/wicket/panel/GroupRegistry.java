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

import java.util.Calendar;

import jp.dip.komusubi.lunch.Configuration;
import jp.dip.komusubi.lunch.model.Group;
import jp.dip.komusubi.lunch.module.dao.GroupDao;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupRegistry extends Panel {

	private static final long serialVersionUID = 5865881679606159636L;
	private static final Logger logger = LoggerFactory.getLogger(GroupRegistry.class);
	
	public GroupRegistry(String id) {
		super(id);
		add(new GroupRegistryForm("group.form"));
	}

	private static class GroupRegistryForm extends Form<Void> {

		private static final long serialVersionUID = -7593471899292529129L;
		private Group group = new Group(null);
		
		public GroupRegistryForm(String id) {
			super(id);
			setDefaultModel(new CompoundPropertyModel<Group>(group));
			add(getIdField("id"));
			add(getNameField("name"));
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
			};
		}
		
		@Override
		public void onSubmit() {
			if (group.getLastOrder() == null) {
				// FIXME set last order date(time)				
//				group.setLastOrder();
			}
			GroupDao groupDao = Configuration.getInstance(GroupDao.class);
			groupDao.persist(group);
			logger.info("group: {}", group);
		}
	}

}
