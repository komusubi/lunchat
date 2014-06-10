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

import java.io.Serializable;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class EmailSender extends Panel {

	private static final long serialVersionUID = -1701535364518049051L;
//	private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);

	/**
	 * email sender bean. 
	 * @author jun.ozeki
	 * @since 2011/11/06
	 */
	public static abstract class EmailSenderBean implements Serializable {

		private static final long serialVersionUID = -4627063923299447361L;
		public String email;
		public String message;
		public String submitLabel;
		public abstract void onSubmit();
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("EmailSenderBean [email=").append(email).append(", message=")
					.append(message).append(", submitValue=").append(submitLabel).append("]");
			return builder.toString();
		}
	}
	
	public EmailSender(String id, IModel<EmailSenderBean> model) {
		super(id, model);
		add(new FeedbackPanel("feedback"));
		add(new EmailSenderForm("email.form", model));
		add(new Label("message"));
	}

	/**
	 * email form. 
	 * @author jun.ozeki
	 * @since 2011/11/06
	 */
	private static class EmailSenderForm extends Form<EmailSenderBean> {

		private static final long serialVersionUID = 939338139087563720L;
		
		public EmailSenderForm(String id, IModel<EmailSenderBean> model) {
			super(id, model);
			add(new EmailTextField("email"));
			add(new Button("submit", Model.of(model.getObject().submitLabel)));
		}
		@Override
		public void onSubmit() {
			getModel().getObject().onSubmit();
		}
	}
}
