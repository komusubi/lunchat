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
import java.util.Date;

import jp.dip.komusubi.lunch.model.Product;
import jp.dip.komusubi.lunch.model.User;
import jp.dip.komusubi.lunch.wicket.WicketSession;
import jp.dip.komusubi.lunch.wicket.component.AuthenticatedLabel;
import jp.dip.komusubi.lunch.wicket.component.FormKey;
import jp.dip.komusubi.lunch.wicket.panel.ChoiceLunch;
import jp.dip.komusubi.lunch.wicket.panel.Footer;
import jp.dip.komusubi.lunch.wicket.panel.GroupList;
import jp.dip.komusubi.lunch.wicket.panel.GroupRegistry;
import jp.dip.komusubi.lunch.wicket.panel.Header;
import jp.dip.komusubi.lunch.wicket.panel.Header.HeaderBean;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.komusubi.common.util.Resolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * home page.
 * @author jun.ozeki
 * @since 2011/12/19
 */
public class Home extends VariationBase {
	
	private static final long serialVersionUID = -5767042157938188479L;
	private static final Logger logger = LoggerFactory.getLogger(Home.class);
	private User user = WicketSession.get().getLoggedInUser();
	private String username = user == null ? "" : user.getName();
	private String pageTitle = getString("page.title");
	@Inject @Named("calendar") private Resolver<Calendar> resolver;
    private FormKey key;
//	@Inject transient private OrderDao orderDao;
	
    public Home() {
    	HeaderBean bean = getDefaultHeaderBean(getLocalizer().getString("page.title", this));
    	add(new Header("header", Model.of(bean), true));
    	add(getChoiceLunch("select.menu"));
    	add(new GroupList("group.list"));
    	add(getGroupRegistry("group.registry"));
    	// FIXME temporary set label component.
    	add(new Label("order.list").setVisible(false));
    	add(new AuthenticatedLabel("greeting", getLocalizer().getString("greeting", this, new Model<Home>(this))));
    	add(new Footer("footer"));
    }
    
    public void onInitialize() {
        super.onInitialize();
        this.key = new FormKey(getPageId(), getId(), new Date());
    }
    
    public void onConfigure() {
        super.onConfigure();
        WicketSession.get().addFormKey(key);
    }
    
    protected ChoiceLunch getChoiceLunch(String id) {
    	return new ChoiceLunch(id) {

			private static final long serialVersionUID = -8829593828769576050L;

			@Override
			protected void onChoiceProduct(Product product) {
			    if (!WicketSession.get().removeFormKey(key)) {
			        // double submit
			        logger.info("double submit onChoiceProduct");
			        return;
			    }
			    WicketSession session = WicketSession.get();
				if (!session.isSignedIn()) {
					// FIXME it should hold in choice product.
					setResponsePage(Login.class);
				} else {
				    if (session.getLoggedInUser().getGroup() == null){
				        setResponsePage(Home.this);
				    } else {
				        setResponsePage(new OrderConfirmation(Model.of(product)));
				    }
				}
			}
    		
    	};
    }
    
    protected GroupRegistry getGroupRegistry(String id) {
    	return new GroupRegistry(id) {
    		private static final long serialVersionUID = -3291096546275717623L;

			@Override
    		protected void onRegistered() {
    			setResponsePage(Home.this);
    		}
    	};
    }
}
