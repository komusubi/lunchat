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
package jp.dip.komusubi.lunch.wicket.page;

import jp.dip.komusubi.lunch.wicket.component.AuthorizedFrame;
import jp.dip.komusubi.lunch.wicket.panel.GroupList;
import jp.lunchat.LunchException;

import org.apache.wicket.markup.html.basic.Label;

/**
 * choice group. 
 * @author jun.ozeki
 * @since 2011/11/16
 */
public class Grouping extends AuthorizedFrame { 

	private static final long serialVersionUID = -8681405849328505396L;
	
	public Grouping() {
//		add(new Header("header", Model.of(getDefaultHeaderBean(getString("page.title")))));
		add(new Label("page.message", "後でメッセージ設定する"));
		add(new GroupList("group.list"));
//		add(new GroupRegistry("group.registry"));
//		add(new Footer("footer"));
		throw new LunchException("Header and Footer 削除対応により例外スロー");
	}
	
}
