/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package jp.dip.komusubi.lunch.wicket.page.account;

import jp.dip.komusubi.lunch.wicket.component.ApplicationFrame;
import jp.dip.komusubi.lunch.wicket.page.error.ErrorPage;
import jp.dip.komusubi.lunch.wicket.panel.Profile;

import org.apache.wicket.model.Model;
import org.apache.wicket.request.Url.StringMode;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * user registry page.
 * @author jun.ozeki
 */
public class Registry extends ApplicationFrame {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(Registry.class);
    private PageParameters params;

    /**
     * create new instance.
     * @param params
     */
    public Registry(PageParameters params) {
        this.params = params;
    }

    /**
     * initialize components.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        if (params.get("fragment").isEmpty()) {
            logger.info("malformed url {}", getRequest().getClientUrl().toString(StringMode.FULL));
            // FIXME literal word move to resource file.
            setResponsePage(new ErrorPage(getString("illegal.access.message")));
        }
        add(new Profile("profile", Model.of(params.get("fragment").toString())));
    }
    
    /**
     * configure compoentns.
     */
    @Override
    protected void onConfigure() {
        super.onConfigure();
        get("signIn").setVisible(false);
        get("signOut").setVisible(false);
    }
}
