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
package jp.dip.komusubi.lunch.wicket.page;

import java.util.List;

import jp.dip.komusubi.lunch.service.Shopping;
import jp.lunchat.core.model.Order;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

public class Notice extends WebPage {

	private static final long serialVersionUID = -2697331747934595L;
	private List<Order> orders;
	private Shopping shopping;

	public Notice(Shopping shopping, List<Order> orders) {
		this.shopping = shopping;
		this.orders = orders;
		add(new NoticeForm("notice.form", null));
	}

	private class NoticeForm extends Form<Void> {

		private static final long serialVersionUID = 5359022433370058734L;

		public NoticeForm(String id, IModel<Void> model) {
			super(id, model);
		}

		@Override
		public void onSubmit() {
//			shopping.order(true);
		}

	}
}
