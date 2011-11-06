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

public class Approval extends Panel {

	private static final long serialVersionUID = -4176824939454988839L;

	public Approval(String id) {
		super(id);
		add(new ApprovalForm("approval.form"));
	}
	
	private static class ApprovalForm extends Form<Void> {

		private static final long serialVersionUID = 4010463474401119716L;
		
		public ApprovalForm(String id) {
			super(id);
			add(getApplyButton("apply"));
			add(getCancelButton("cancel"));
		}
		
		private Button getApplyButton(String id) {
			return new Button(id) {
				private static final long serialVersionUID = 7138884209309510709L;
				@Override
				public void onSubmit() {
					// 承認通知(各ユーザー別の通知方法で)行う。hash値利用のこと。
				}
			};
		}
		
		private Button getCancelButton(String id) {
			return new Button(id) {
				private static final long serialVersionUID = 3491917688221911493L;
				@Override
				public void onSubmit() {
//					setResponsePage()
					// page に参照したくないんだけど。event? 
				}
			};
		}
	}
}
