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

import java.util.List;

import jp.dip.komusubi.lunch.model.Group;
import jp.dip.komusubi.lunch.model.User;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * member list.
 * @author jun.ozeki
 * @since 2011/12/11
 */
public class MemberList extends Panel {

	private static final long serialVersionUID = 695094056051480395L;
	private static final Logger logger = LoggerFactory.getLogger(MemberList.class);
	
	public MemberList(String id, Group group) { // IModel<Group> model) {
		super(id);
		add(new FeedbackPanel("feedback"));
		add(new Label("list.name", getString("label.title", Model.of(group))));
		add(new GroupListView("list", getLoadableDetachableModel(group)));
	}

	private LoadableDetachableModel<List<User>> getLoadableDetachableModel(final Group group) {
		return new LoadableDetachableModel<List<User>>() {
		
			private static final long serialVersionUID = 342639502330434200L;
	
			@Override
			public List<User> load() {
//				UserDao userDao = Configuration.getInstance(UserDao.class);
//				return userDao.findByGroupId(groupId);
				return group.getUsers();
			}
		};
	}
	
	private static class GroupListView extends ListView<User> {

		private static final long serialVersionUID = 831545974755510431L;

		public GroupListView(String id, List<? extends User> list) {
			super(id, list);
		}

		public GroupListView(String id, IModel<? extends List<? extends User>> model) {
			super(id, model);
		}

		public GroupListView(String id) {
			super(id);
		}

		@Override
		protected void populateItem(ListItem<User> item) {
			User user = item.getModelObject();
//			item.setDefaultModel(new CompoundPropertyModel<User>(user));
			item.add(getLink("list.item", new CompoundPropertyModel<User>(user)));
		}
		
		protected Link<User> getLink(String id, final IModel<User> model) {
			return new Link<User>(id, model) {
				
				private static final long serialVersionUID = -7932363259153679644L;
				{
					add(new Label("id", model.getObject().getId()));
					add(new Label("name", model.getObject().getName()));
				}
				@Override
				public void onClick() {
					logger.info("onClick: {}", model.getObject());
				}
				
			};
		}
	}
}
