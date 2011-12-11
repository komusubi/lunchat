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

import java.util.Calendar;

import jp.dip.komusubi.common.util.Resolver;
import jp.dip.komusubi.lunch.Configuration;
import jp.dip.komusubi.lunch.model.User;
import jp.dip.komusubi.lunch.service.AccountService;
import jp.dip.komusubi.lunch.wicket.WicketSession;
import jp.dip.komusubi.lunch.wicket.component.AuthenticatedLabel;
import jp.dip.komusubi.lunch.wicket.panel.ChoiceShop;
import jp.dip.komusubi.lunch.wicket.panel.Footer;
import jp.dip.komusubi.lunch.wicket.panel.GroupList;
import jp.dip.komusubi.lunch.wicket.panel.GroupRegistry;
import jp.dip.komusubi.lunch.wicket.panel.Header;
import jp.dip.komusubi.lunch.wicket.panel.Header.HeaderBean;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class Home extends VariationBase {
	
	private static final long serialVersionUID = -5767042157938188479L;
	private static final Logger logger = LoggerFactory.getLogger(Home.class);
	private User user = WicketSession.get().getLoggedInUser();
	private String username = user == null ? "" : user.getName();
	private String pageTitle = getString("page.title");
	@Inject @Named("calendar") Resolver<Calendar> resolver;
	
    public Home() {
    	HeaderBean bean = getDefaultHeaderBean(getLocalizer().getString("page.title", this));
    	add(new Header("header", Model.of(bean), true));
    	add(getMainPanel("main.panel"));
    	add(getSubPanel("sub.panel"));
    	add(new AuthenticatedLabel("greeting", getLocalizer().getString("greeting", this, new Model<Home>(this))));
    	add(new Footer("footer"));
    }
    
    protected Panel getMainPanel(String id) {
    	AccountService accountService = Configuration.getInstance(AccountService.class);
    	Panel panel = null;
    	Calendar current = resolver.resolve();
    	// login ?
    	if (WicketSession.get().isSignedIn()) {
    		if (user.getGroup() == null) {
    			panel = new GroupList(id, null);
    		} else if (accountService.getContractedShops(user).size() == 0) {
    			panel = new GroupRegistry(id);
    		} else if (accountService.getOrders(user, current.getTime()).size() > 0) {
    			// have order already ?
    			// FIXEME 食べ終わってない注文があったら。メソッド修正必須。
//    			panel = new OrderList(id, orders);
    		}
    	}
    	if (panel == null)
    		panel = new ChoiceShop(id);
    	return panel;
    }
    
    protected Panel getSubPanel(String id) {
    	GroupRegistry groupRegistry = new GroupRegistry(id) {
    		
    		private static final long serialVersionUID = -9153627644004536343L;

    		@Override
			public boolean isVisible() {
    			if (!WicketSession.get().isSignedIn() || user.getGroup() != null)
    				return false;
    			return true;
    		}
    	};
    	return groupRegistry;
    }
}
