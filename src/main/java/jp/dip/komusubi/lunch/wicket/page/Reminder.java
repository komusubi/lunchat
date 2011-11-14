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

import jp.dip.komusubi.lunch.Configuration;
import jp.dip.komusubi.lunch.LunchException;
import jp.dip.komusubi.lunch.service.AccountService;
import jp.dip.komusubi.lunch.service.NotFoundEmailException;
import jp.dip.komusubi.lunch.util.Nonce;
import jp.dip.komusubi.lunch.wicket.WicketSession;
import jp.dip.komusubi.lunch.wicket.panel.EmailSender;
import jp.dip.komusubi.lunch.wicket.panel.EmailSender.EmailSenderBean;
import jp.dip.komusubi.lunch.wicket.panel.Footer;
import jp.dip.komusubi.lunch.wicket.panel.Header;

import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Reminder extends VariationBase {

	private static final long serialVersionUID = -6010319871215878007L;
	private static final Logger logger = LoggerFactory.getLogger(Reminder.class);
	
	public Reminder() {
		add(new Header("header", Model.of(getDefaultHeaderBean(getString("page.title")))));
		add(new EmailSender("email.reminder", new CompoundPropertyModel<EmailSenderBean>(getEmailSenderBean())));
		add(new Footer("footer"));
	}
	
	private EmailSenderBean getEmailSenderBean() {
		EmailSenderBean bean = new EmailSenderBean() {

			private static final long serialVersionUID = 4984027733469660821L;

			public void onSubmit() {
				String targetPath = getRequestCycle().urlFor(Login.class, null).toString();
				String ownUrl = getRequestCycle().getUrlRenderer().renderFullUrl(getRequest().getClientUrl());
				String url = RequestUtils.toAbsolutePath(ownUrl, targetPath);
				try {
					AccountService account = Configuration.getInstance(AccountService.class);
					Nonce nonce = account.remind(email, url);
					// save nonce in session 
					WicketSession.get().setAttribute(Nonce.class.getName(), nonce);
					
				} catch (NotFoundEmailException e) {
					// sniffing mail address ? or just wrong ?
					logger.info("user might be suspect? wrong input email address: {}", email);
				} catch (LunchException e) {
					error(getLocalizer().getString("send.mail.error", Reminder.this));
					return;
				}
				info(getLocalizer().getString("send.mail.complete", Reminder.this));
			}
			
		};
		bean.message = getLocalizer().getString("message", Reminder.this);
		bean.submitLabel = getLocalizer().getString("submit.label", Reminder.this);
		
		return bean;
	}
}

