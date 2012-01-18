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

import jp.dip.komusubi.lunch.model.User;
import jp.dip.komusubi.lunch.wicket.panel.Dialog;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * order confirm page. 
 * @author jun.ozeki
 */
public class Confirmation extends AuthorizedPage {

	private static final Logger logger = LoggerFactory.getLogger(Confirmation.class);
	private static final long serialVersionUID = 6448297553843014369L;
//	private String pageTitle = getString("page.title");
	
	public Confirmation() {
//		add(new Header("header", Model.of(getDefaultHeaderBean(pageTitle))));
//		add(new OrderList("basket", basket));
//		add(new Footer("footer"));
	}
	
	public Confirmation(Model<User> model) {
		this();
		// FIXME localized message move to resource file.
		add(new Label("header.title", "タイトル"));
		add(getDialog("confirm", model));
	}
	
	protected Dialog getDialog(String id, final Model<User> model) {
//		StringResourceModel message = new StringResourceModel("", model, );
		return new Dialog(id, model) {

			private static final long serialVersionUID = 1351931925422304411L;
//			private Member member = new Member(Model.of(model.getObject().getGroup()));
			
			@Override
			protected void onAgree() {
//				AccountService service = Configuration.getInstance(AccountService.class);
//				service.
//				Member member = new Member(Model.of(model.getObject().getGroup()),
//										Model.of(model.getObject().getName() + " さんに承認依頼を送信しました。"));
				Member member = new Member(Model.of(model.getObject().getGroup()));
				setResponsePage(member);
			}

			@Override
			protected void onCancel() {
				logger.info("onCancel event!!");
				Member member = new Member(Model.of(model.getObject().getGroup()));
				setResponsePage(member);
			}
		};
	}
}
