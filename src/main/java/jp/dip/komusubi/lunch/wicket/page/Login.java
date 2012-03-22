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
import jp.dip.komusubi.lunch.util.Nonce;
import jp.dip.komusubi.lunch.wicket.WicketSession;
import jp.dip.komusubi.lunch.wicket.component.FormKey;
import jp.dip.komusubi.lunch.wicket.page.account.Registry;
import jp.dip.komusubi.lunch.wicket.panel.EmailEntry;
import jp.dip.komusubi.lunch.wicket.panel.Footer;
import jp.dip.komusubi.lunch.wicket.panel.Header;
import jp.dip.komusubi.lunch.wicket.panel.SignIn;

import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * login page.
 * @author jun.ozeki
 * @since 2011/11/19
 */
public class Login extends VariationBase {

    private static final Logger logger = LoggerFactory.getLogger(Login.class);
	private static final long serialVersionUID = -5101224283988545642L;
    private FormKey key;
	
	public Login() {
		this(new PageParameters());
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
	
	public Login(PageParameters params) {
		StringValue activateValue = params.get("activate");
		// FIXME should not change component any condition!
		SignIn signIn;
		if (!activateValue.isEmpty())
			signIn = new SignIn("signInPanel", activateValue.toString());
		else
			signIn = new SignIn("signInPanel");
		
		add(signIn);
		add(new Header("header", Model.of(getDefaultHeaderBean(getString("page.title"))), false));
		add(getEmailEntry("registry")); 
		add(new Footer("footer"));
	}
	
	protected EmailEntry getEmailEntry(String id) {
	    return new EmailEntry(id) {
	        private static final long serialVersionUID = 3560368059107652338L;

            @Override
	        protected void onRegister() {
                if (WicketSession.get().removeFormKey(key)) {
                    // confirm page の absolute URLを取得
                    String targetPath = getRequestCycle().urlFor(Registry.class, null).toString();
                    String ownUrl = getRequestCycle().getUrlRenderer().renderFullUrl(getRequest().getClientUrl());
                    String url = RequestUtils.toAbsolutePath(ownUrl, targetPath);
                    
                    // already exist email ?  
                    AccountService account = Configuration.getInstance(AccountService.class);
                    if (account.findByEmail(getUser().getEmail()) != null) {
                        error(getLocalizer().getString("already.exist.email", this, "email address already exist."));
                    } else {
                        Nonce nonce = account.apply(getUser(), url);
                        // session に Nonceを保持
                        WicketSession.get().setAttribute(Nonce.class.getName(), nonce);
                        info(getLocalizer().getString("send.confirm", this, "send confirm email."));
                    }
                } else {
                    logger.info("double click on EmailEntry#onRegister");
                }
	        }
	    };
	}
}
