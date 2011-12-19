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

import java.io.File;

import javax.servlet.ServletContextEvent;

import jp.dip.komusubi.lunch.LunchException;
import jp.dip.komusubi.lunch.MockBootstrap;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.mock.MockServletContext;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.rules.ExternalResource;
import org.komusubi.common.util.Resolver;

import com.google.inject.Injector;

public class WicketTesterResource extends ExternalResource {
	private WicketTester tester;
	private Resolver<Injector> builder;
	private MockServletContext servletContext;
	private WebApplication application;
	
	public WicketTesterResource(Resolver<Injector> builder) {
		this.builder = builder; 
	}
	
	@Override
	public void before() {
		application = new WicketApplication();
		servletContext = new MockServletContext(application, "/src/main/webapp");
	}
	
	@Override
	public void after() {
		if (tester != null)
			tester.destroy();
	}
	
	public WicketTester getTester() {
		MockBootstrap boot = new MockBootstrap() {
			@Override
			protected Injector buildInjector() {
				return builder.resolve();
			}
		};
		boot.contextInitialized(new ServletContextEvent(servletContext));
		tester = new WicketTester(application, servletContext);
		return tester;
	}
	
	protected void configureResource() {
		File webXml = new File(servletContext.getRealPath("WEB-INF/web.xml"));
		if (webXml.exists() && webXml.isFile())
			return;
			
		File webXmlTemplate = new File(servletContext.getRealPath("/WEB-INF/web.xml.template"));
		if (!webXmlTemplate.exists() || !webXmlTemplate.isFile()) {
			throw new LunchException("not found resouce for unit test. : " + webXmlTemplate.getAbsolutePath());
		}
		webXmlTemplate.renameTo(webXml);
	}

}
