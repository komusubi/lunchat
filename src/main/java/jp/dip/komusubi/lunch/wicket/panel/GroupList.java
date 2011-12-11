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

import jp.dip.komusubi.lunch.Configuration;
import jp.dip.komusubi.lunch.model.Group;
import jp.dip.komusubi.lunch.module.dao.GroupDao;
import jp.dip.komusubi.lunch.wicket.page.Member;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * group list.
 * @author jun.ozeki
 * @since 2011/12/11
 */
public class GroupList extends Panel {

	private static final long serialVersionUID = -728544767659111573L;
	private static final Logger logger = LoggerFactory.getLogger(GroupList.class);
	
	public GroupList(String id, Class<? extends WebPage> nextPage) {
		super(id);
		add(getGroupList("list.item", nextPage));
	}

	private LoadableDetachableModel<List<Group>> ldmodel = new LoadableDetachableModel<List<Group>>() {
		private static final long serialVersionUID = -371898900116480064L;

		@Override
		public List<Group> load() {
			GroupDao groupDao = Configuration.getInstance(GroupDao.class);
			List<Group> groups = groupDao.findAll();
			if (groups.size() == 0)
				groups.add(new Group("dummy")
								.setName("グループが1件も存在しませんでした。"));
			return groups;
		}
	};
	
	private ListView<Group> getGroupList(String id, final Class<? extends WebPage> page) {
		return new ListView<Group>(id, ldmodel) {

			private static final long serialVersionUID = -423055326223010011L;

			@Override
			protected void populateItem(ListItem<Group> item) {
				Group group = item.getModelObject();
				item.add(getGroupLink("item.link", group));
			}
			
			private Link<String> getGroupLink(final String id, final Group group) {
				return new Link<String>(id) {
					private static final long serialVersionUID = 6166112156309130232L;
					
					@Override
					protected void onInitialize() {
						add(new Label("link.label", group.getName()));
						super.onInitialize();
					}
					@Override
					public void onClick() {
						logger.info("onClick to {}", group);
//						setResponsePage(page);
						// FIXME reference to page package.
						setResponsePage(new Member(group));
					}
					@Override
					public boolean isEnabled() {
						if ("dummy".equals(group.getId()))
							return false;
						return true;
					}
				};
			}
		};
	}
}
