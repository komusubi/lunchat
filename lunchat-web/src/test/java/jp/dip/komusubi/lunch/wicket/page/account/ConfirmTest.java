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
package jp.dip.komusubi.lunch.wicket.page.account;

import jp.dip.komusubi.lunch.module.resolver.DigestResolver;
import jp.dip.komusubi.lunch.service.AccountService;
import jp.dip.komusubi.lunch.service.MockSmtpServer;
import jp.dip.komusubi.lunch.util.Nonce;
import jp.dip.komusubi.lunch.wicket.WicketTesterResource;
import jp.dip.komusubi.lunch.wicket.panel.Profile;
import jp.lunchat.storage.DefaultNonce;
import jp.lunchat.storage.dao.UserDao;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.komusubi.common.protocol.smtp.SmtpServer;
import org.komusubi.common.util.Resolver;
import org.mockito.Mockito;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

public class ConfirmTest {

	private WicketTester tester;
	private UserDao mock;
	private Resolver<Injector> injectBuilder = new Resolver<Injector>() {
		
		public Injector resolve() {
			return ConfirmTest.this.buildInjector();
		}

		public Injector resolve(Injector value) {
			return null;
		}
		
	};
	@Rule
	public WicketTesterResource wicketResource = new WicketTesterResource(injectBuilder);
	
	private Injector buildInjector() {
		return Guice.createInjector(new AbstractModule() {
			
			@Override
			protected void configure() {
				
				bind(UserDao.class).toInstance(mock = Mockito.mock(UserDao.class));
				bind(AccountService.class); 
				bind(new TypeLiteral<Resolver<String>>(){ })
					.annotatedWith(Names.named("digest")).toInstance(new DigestResolver());
				bind(SmtpServer.class).toInstance(new MockSmtpServer());
				bind(Nonce.class).to(DefaultNonce.class);
			}
			
		});
	}
	
	@Ignore
	@Test
	public void renderedSuccess() {
		tester = wicketResource.getTester();
		
		tester.startPage(Registry.class, new PageParameters().set("segment", "value"));
		tester.assertComponent("profile", Profile.class);
		tester.assertRenderedPage(Registry.class);
	}
}
