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

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * approval.
 * @author jun.ozeki
 * @since 2012/01/26
 */
public class Approval extends Panel {

	private static final long serialVersionUID = -4176824939454988839L;

	public static abstract class ApprovalBean {
		public String noticeLabel;
		public String applyMessage;
		public String applyButton;
		public String cancelButton;
		public abstract void onApply(); 
		public abstract void onCancel();
	};
	
	public Approval(String id, IModel<ApprovalBean> model) {
		super(id);
		add(new ApprovalForm("approval.form", model));
	}
	
	private static class ApprovalForm extends Form<ApprovalBean> {

		private static final long serialVersionUID = 4010463474401119716L;
		
		public ApprovalForm(String id, IModel<ApprovalBean> model) {
			super(id, model);
			add(getApplyButton("apply", model));
			add(getCancelButton("cancel", model));
		}
		
		private Button getApplyButton(String id, final IModel<ApprovalBean> model) {
//			return new Button(id, Model.of(model.getObject().applyButton)) {
			return new Button(id) {
				
				private static final long serialVersionUID = 7138884209309510709L;
				@Override
				public void onSubmit() {
					// 承認通知(各ユーザー別の通知方法で)行う。hash値利用のこと。
					model.getObject().onApply();
				}
			};
		}
		
		private Button getCancelButton(String id, final IModel<ApprovalBean> model) {
//			return new Button(id, Model.of(model.getObject().cancelButton)) {
			return new Button(id) {
				private static final long serialVersionUID = 3491917688221911493L;
				@Override
				public void onSubmit() {
					model.getObject().onCancel();
				}
			};
		}
	}
}
