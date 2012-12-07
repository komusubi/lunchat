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
package jp.dip.komusubi.lunch.wicket.page.settings;

import jp.dip.komusubi.lunch.wicket.component.ApplicationFrame;
import jp.dip.komusubi.lunch.wicket.panel.Profile;

import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * account page.
 * @author jun.ozeki
 */
public class Account extends ApplicationFrame {

    private static final long serialVersionUID = 5109190494409699152L;

    /**
     * create new instance.
     * @param params
     */
    public Account(PageParameters params) {
        
    }

    /**
     * initialize components.
     */
    @Override
    protected void onInitialize() {
        add(new Profile("profile"));
    }
}
