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

import java.util.Date;

import jp.dip.komusubi.lunch.model.Product;
import jp.dip.komusubi.lunch.wicket.WicketSession;
import jp.dip.komusubi.lunch.wicket.component.FormKey;
import jp.dip.komusubi.lunch.wicket.panel.Dialog;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * confirm page. 
 * @author jun.ozeki
 */
public class OrderConfirmation extends Confirmation {

	private static final Logger logger = LoggerFactory.getLogger(OrderConfirmation.class);
	private static final long serialVersionUID = 6448297553843014369L;
	private FormKey key;

	public OrderConfirmation(Model<Product> model) {
		add(new Label("header.title", "タイトル"));
		add(getDialog("confirm", model));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		this.key = new FormKey(getPageId(), getId(), new Date());
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		WicketSession.get().addFormKey(key);
	}
	
	protected Dialog<Product> getDialog(String id, final Model<Product> model) {
		return new Dialog<Product>(id, model) {

			private static final long serialVersionUID = 1351931925422304411L;
			
			@Override
			protected void onAgree() {
				try {
					if (WicketSession.get().removeFormKey(key)) {
					    setResponsePage(new OrderComplete(model));
					} else {
						// FIXME warning double submit.
						error("double submit!!");
						return;
					}
				} catch (Exception e) {
					logger.warn("exception: {}", e);
					error("order fail. " + model.getObject());
				}
			}

			@Override
			protected void onCancel() {
			    logger.info("order canceled.");
			}
		};
	}

}