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
package jp.dip.komusubi.lunch;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class MockBootstrap extends GuiceServletContextListener {
	
	@Override
	protected Injector getInjector() {
		return buildInjector();
	}

	public static Configuration getConfigration(ServletContext context) {
		Configuration.setServletContext(context);
		return Configuration.INSTANCE; 
	}
	
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) { 
		super.contextInitialized(servletContextEvent);
		Configuration.setServletContext(servletContextEvent.getServletContext());
	}

	protected Injector buildInjector() {
		return Guice.createInjector(
				new AbstractModule() {
					@Override
					protected void configure() {
//						bind(UserDao.class).to(MockUserDao.class);
//						bind(new TypeLiteral<Resolver<String>>(){ })
//							.annotatedWith(Names.named("digest")).toInstance(new DigestResolver());
//						bind(AccountService.class).to(MockAccountService.class);
					}
				}
				);
	}
//	private static class MockAccountService extends AccountService {
//		
//		private static final long serialVersionUID = 3667337760150155737L;
//		
//		public MockAccountService() {
//			super(null);
//		}
//		public MockAccountService(UserDao userDao) {
//			super(userDao);
//		}
//	}

}
