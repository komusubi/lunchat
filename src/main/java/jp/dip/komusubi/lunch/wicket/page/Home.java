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

import jp.dip.komusubi.lunch.model.User;
import jp.dip.komusubi.lunch.wicket.WicketSession;
import jp.dip.komusubi.lunch.wicket.component.AuthenticatedLabel;
import jp.dip.komusubi.lunch.wicket.panel.ChoiceShop;
import jp.dip.komusubi.lunch.wicket.panel.Footer;
import jp.dip.komusubi.lunch.wicket.panel.Header;
import jp.dip.komusubi.lunch.wicket.panel.Header.HeaderBean;

import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Home extends VariationBase {
	
	private static final long serialVersionUID = -5767042157938188479L;
	private static final Logger logger = LoggerFactory.getLogger(Home.class);
	private User user = WicketSession.get().getLoggedInUser();
	private String username = user == null ? "" : user.getName();
	private String pageTitle = getString("page.title");
	
    public Home() {
    	HeaderBean bean = getDefaultHeaderBean(getString("page.title"));
    	add(new Header("header", Model.of(bean), true));
    	add(new ChoiceShop("shop.list"));
    	add(new AuthenticatedLabel("greeting", getLocalizer().getString("greeting", this, new Model<Home>(this))));
    	add(new Footer("footer"));
    }
    
    
}
