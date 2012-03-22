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

import jp.dip.komusubi.lunch.Configuration;
import jp.dip.komusubi.lunch.service.AccountService;
import jp.dip.komusubi.lunch.wicket.WicketSession;
import jp.dip.komusubi.lunch.wicket.component.FormKey;
import jp.dip.komusubi.lunch.wicket.panel.Approval;
import jp.dip.komusubi.lunch.wicket.panel.Footer;
import jp.dip.komusubi.lunch.wicket.panel.Header;

import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.komusubi.common.util.Resolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * group attendance.
 * @author jun.ozeki
 * @since 2011/11/19
 */
public class Attendance extends AuthorizedPage {

	private static final long serialVersionUID = 1242588429737776047L;
	private static final Logger logger = LoggerFactory.getLogger(Attendance.class);
	@Inject @Named("digest") Resolver<String> digester;
    private FormKey key;
	
	public Attendance(PageParameters params) {
		add(new Header("header", Model.of(getDefaultHeaderBean(getString("page.title")))));
		add(new FeedbackPanel("feedback"));
		add(getApproval("approval", params));
		add(new Footer("footer"));
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
	
	protected Approval getApproval(String id, PageParameters params) {
	    return new Approval("approval", params) {

            private static final long serialVersionUID = -6648237699896515215L;
            private transient AccountService account = Configuration.getInstance(AccountService.class);

            /**
             * admit to become a member of group.
             */
            @Override
            protected void onApproval() {
                if (WicketSession.get().removeFormKey(key)) {
                    // FIXME Home.class can't get "http://localhost:8080/" 
//                    account.approve(getAdmitter(), getApplicant(), getMessageFromAdmitter(), getPageUrl(Home.class));
                    account.approve(getAdmitter(), getApplicant(), getMessageFromAdmitter(), null);
                    logger.info("{} was admitted to become a member of {}", getApplicant().getNickname(), 
                            getAdmitter().getGroup().getCode());
                } else {
                    logger.info("double submit Approval#onApproval");
                }
            }

            @Override
            protected void onCancel() {
                if (WicketSession.get().removeFormKey(key)) {
                    account.decline(getAdmitter(), getApplicant(), getMessageFromAdmitter());
                    logger.info("{} denied {} join a member of {}", 
                            new Object[]{ getAdmitter().getNickname(), 
                                            getApplicant().getNickname(), 
                                            getAdmitter().getGroup().getCode() });
                } else {
                    logger.info("double submit Approval#onCancel");
                }
            }
	    };
	}
	
}
