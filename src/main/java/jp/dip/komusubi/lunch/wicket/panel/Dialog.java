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

import java.util.Date;

import jp.dip.komusubi.lunch.model.User;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 * dialog.
 * @author jun.ozeki
 * @since 2012/01/15
 */
public abstract class Dialog extends Panel {
	
	private static final long serialVersionUID = 6968078827891651580L;
//	private static final Logger logger = LoggerFactory.getLogger(Dialog.class);
	
	public Dialog(String id, Model<User> model) {
		super(id);
		add(new Label("message", model.getObject().getName() + "さん にグループ参加の承認依頼通知を送信します。"));
		add(new FeedbackPanel("feedback"));
		// FIXME double click affect prevent to formKey implementation.
//		add(new DialogForm("confirm.form"));
		add(getAgreeLink("agree"));
		add(getCancelLink("cancel"));
	}

	protected Link<String> getAgreeLink(String id) {
		return new Link<String>(id) {

			private static final long serialVersionUID = -2374232038734113706L;

			@Override
			public void onClick() {
				onAgree();
			}
			
			@Override
			public void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				// build fail url in jquery mobile dialog view. just fix.
				tag.put("href", "wicket/" + getURL());
			}
		};
	}
	
	protected Link<String> getCancelLink(String id) {
		return new Link<String>(id) {

			private static final long serialVersionUID = 3048569339880231264L;

			@Override
			public void onClick() {
				onCancel();
			}
		};
	}
	
	/**
	 * dialog form.
	 * @author jun.ozeki
	 * @since 2012/01/15
	 */
	public class DialogForm extends Form<Void> {
		
		private static final long serialVersionUID = -3788490402589716737L;

		public DialogForm(String id) {
			super(id);
			add(getAgreeButton("agree"));
			add(getCancelButton("cancel"));
		}
				
		public Button getAgreeButton(String id) {
			return new Button(id) {
				
				private static final long serialVersionUID = 4427926130247667179L;

				@Override
				public void onSubmit() {
					onAgree();
				}
			};
		}
		
		public Button getCancelButton(String id) {
			return new Button(id) {
				
				private static final long serialVersionUID = 745625158114441901L;

				@Override
				public void onSubmit() {
					onCancel();
				}
			};
		}
	}
	
	protected abstract void onAgree();
	protected abstract void onCancel();
}
