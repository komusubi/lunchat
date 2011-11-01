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
package jp.dip.komusubi.lunch.wicket;

import jp.dip.komusubi.lunch.wicket.page.CompleteOrder;
import jp.dip.komusubi.lunch.wicket.page.Home;
import jp.dip.komusubi.lunch.wicket.page.Login;
import jp.dip.komusubi.lunch.wicket.page.Receipt;
import jp.dip.komusubi.lunch.wicket.page.error.ErrorPage;
import jp.dip.komusubi.lunch.wicket.page.error.ExpiredError;
import jp.dip.komusubi.lunch.wicket.page.error.InternalServerError;
import jp.dip.komusubi.lunch.wicket.page.settings.Account;
import jp.dip.komusubi.lunch.wicket.page.settings.Confirm;

import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.guice.GuiceComponentInjector;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.pages.BrowserInfoPage;

import com.google.inject.Injector;

/**
 * Application object for your web application. If you want to run this
 * application without deploying, run the Start class.
 * 
 * @see jp.dip.komusubi.lunch.Start#main(String[])
 */
public class WicketApplication extends AuthenticatedWebApplication {
	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<Home> getHomePage() {
		return Home.class;
	}

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init() {
		super.init();
		// guice injector
		getComponentInstantiationListeners().add(new GuiceComponentInjector(this,
				(Injector) getServletContext().getAttribute(Injector.class.getName()), false));
		// security
		getSecuritySettings().setEnforceMounts(true);
		// markup
		getMarkupSettings().setDefaultMarkupEncoding("utf-8");
		getMarkupSettings().setCompressWhitespace(true);
		getMarkupSettings().setStripComments(true);
		// resource
		getResourceSettings().addResourceFolder("WEB-INF");
		// application
		getApplicationSettings().setPageExpiredErrorPage(ExpiredError.class); 
		getApplicationSettings().setInternalErrorPage(InternalServerError.class);
//		getApplicationSettings().setAccessDeniedPage(accessDeniedPage)
		// request cycle
		getRequestCycleSettings().setGatherExtendedBrowserInfo(true);	
		getRequestCycleSettings().setResponseRequestEncoding("utf-8");
		// logger
		getRequestLoggerSettings().setRequestLoggerEnabled(true);
		// mount page
		mount();
		// debug setting in development mode.
		if (getConfigurationType().equals(RuntimeConfigurationType.DEVELOPMENT)) {
			getDebugSettings().setOutputComponentPath(true);
			getDebugSettings().setOutputMarkupContainerClassName(true);
			getDebugSettings().setLinePreciseReportingOnAddComponentEnabled(true);
			getDebugSettings().setLinePreciseReportingOnNewComponentEnabled(true);
		}
	}
	
	private void mount() {
		mountPage("/login", Login.class);
		mountPage("/receipt", Receipt.class);
		mountPage("/confirm/${segment}", Confirm.class);
		mountPage("/settings/account", Account.class);
		mountPage("/order", CompleteOrder.class);
		mountPage("/postback", BrowserInfoPage.class);
		// error page
		mountPage("/error", ErrorPage.class);
		mountPage("/error/internal", InternalServerError.class);
		mountPage("/error/expired", ExpiredError.class);
	}
	
	@Override
	protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
		return WicketSession.class;
	}

	@Override
	public Class<? extends WebPage> getSignInPageClass() {
		return Login.class;
	}
	
}