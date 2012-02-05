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

import java.text.MessageFormat;

import jp.dip.komusubi.lunch.model.User;
import jp.dip.komusubi.lunch.wicket.panel.Approval;
import jp.dip.komusubi.lunch.wicket.panel.Approval.ApprovalBean;
import jp.dip.komusubi.lunch.wicket.panel.Footer;
import jp.dip.komusubi.lunch.wicket.panel.Header;

import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * group attendance.
 * @author jun.ozeki
 * @since 2011/11/19
 */
public class Attendance extends AuthorizedPage {

	private static final long serialVersionUID = 1242588429737776047L;
	private final User applyTo;
	
	public Attendance(User applyTo) {
		this.applyTo = applyTo;
		add(new Header("header", Model.of(getDefaultHeaderBean(getString("page.title")))));
		add(new Approval("approval", new CompoundPropertyModel<ApprovalBean>(getApprovalBean())));
		add(new Footer("footer"));
	}
	
	public Attendance(PageParameters params) {
		applyTo = null;
	}
	
	// 1. 既にグループに所属していないか？(複数人に参加要請をした場合の考慮)(別のグループの場合あり？)

	protected ApprovalBean getApprovalBean() {
		ApprovalBean bean = new ApprovalBean() {

			@Override
			public void onApply() {
				
				
			}

			@Override
			public void onCancel() {
				
			}
			
		};
		
		bean.noticeLabel = MessageFormat.format(
				getLocalizer().getString("message", Attendance.this),
				applyTo.getName(), "");
//		bean.applyButton = getLocalizer().getString("", Participation.this);
//		bean.cancelButton = getLocalizer().getString("", Participation.this);
		
		return bean;
	}
}
