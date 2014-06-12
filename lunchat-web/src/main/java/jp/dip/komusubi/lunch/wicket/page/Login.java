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

import java.util.Date;

import javax.inject.Inject;

import jp.dip.komusubi.lunch.wicket.WicketSession;
import jp.dip.komusubi.lunch.wicket.component.ApplicationFrame;
import jp.dip.komusubi.lunch.wicket.component.FormKey;
import jp.dip.komusubi.lunch.wicket.page.account.Registry;
import jp.dip.komusubi.lunch.wicket.panel.EmailEntry;
import jp.dip.komusubi.lunch.wicket.panel.SignIn;
import jp.lunchat.util.Nonce;
import jp.lunchat.web.service.AccountService;

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
public class Login extends ApplicationFrame {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(Login.class);
    private FormKey key;
    @Inject private Nonce nonce;
    @Inject private AccountService account;
    
    /**
     * create new instance. 
     */
    public Login() {
        this(new PageParameters());
    }

    /**
     * create new instance.
     * @param params page parameters
     */
    public Login(PageParameters params) {
        StringValue activateValue = params.get("activate");
        // FIXME should not change component any condition!
        SignIn signIn;
        if (!activateValue.isEmpty())
            signIn = new SignIn("signInPanel", activateValue.toString());
        else
            signIn = new SignIn("signInPanel");

        add(signIn);
        add(getEmailEntry("registry"));
    }

    /**
     * initialize components.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.key = new FormKey(getPageId(), getId(), new Date());
    }

    /**
     * configure components.
     */
    @Override
    protected void onConfigure() {
        super.onConfigure();
        // this page is login page, so do NOT "login" component. 
        get("signIn").setVisible(false);
        get("signOut").setVisible(false);
        WicketSession.get().addFormKey(key);
    }

    /**
     * 
     * @param id
     * @return
     */
    protected EmailEntry getEmailEntry(String id) {
        return new EmailEntry(id) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onRegister() {
                if (WicketSession.get().removeFormKey(key)) {
                    // confirm page の absolute URLを取得
                	String salt = nonce.salt();
                	PageParameters parameters = new PageParameters().add("fragment", nonce.get(getUser().getEmail(), salt));
                    String targetPath = getRequestCycle().urlFor(Registry.class, parameters).toString();
                    String ownUrl = getRequestCycle().getUrlRenderer().renderFullUrl(getRequest().getClientUrl());
                    String url = RequestUtils.toAbsolutePath(ownUrl, targetPath);
                    logger.info("user register url: {}", url);

                    // already exist email ?
                    if (account.findByEmail(getUser().getEmail()) != null) {
                        error(getLocalizer().getString("already.exist.email", this, "email address already exist."));
                    } else {
                        if (account.apply(getUser(), url)) {
	                        // session に Nonceを保持
                        	// FIXME literal attribute name.
	                        WicketSession.get().setAttribute("salt", salt);
	                        info(getLocalizer().getString("send.confirm", this, "send confirm email."));
                        } else {
                        	error(getLocalizer().getString("failed.confirm", this, "failed confirm email."));
                        }
                    }
                } else {
                    logger.info("double click on EmailEntry#onRegister");
                }
            }
        };
    }
}
