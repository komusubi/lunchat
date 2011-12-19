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
package jp.dip.komusubi.lunch.wicket.panel;

import jp.dip.komusubi.lunch.model.User;
import jp.dip.komusubi.lunch.module.DefaultNonce;
import jp.dip.komusubi.lunch.module.dao.MockUserDao;
import jp.dip.komusubi.lunch.module.dao.UserDao;
import jp.dip.komusubi.lunch.module.resolver.DigestResolver;
import jp.dip.komusubi.lunch.service.AccountService;
import jp.dip.komusubi.lunch.util.Nonce;
import jp.dip.komusubi.lunch.wicket.WicketTesterResource;
import jp.dip.komusubi.lunch.wicket.page.error.ErrorPage;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.pages.BrowserInfoPage;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Rule;
import org.junit.Test;
import org.komusubi.common.util.Resolver;
import org.mockito.Mockito;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

public class ProfileTest {
	private AccountService mock = Mockito.mock(AccountService.class);
	
	private Resolver<Injector> injectBuilder = new Resolver<Injector>() {

		public Injector resolve() {
			return Guice.createInjector(new AbstractModule() {

				@Override
				protected void configure() {
					bind(UserDao.class).to(MockUserDao.class);
					bind(AccountService.class).toInstance(mock);
					bind(new TypeLiteral<Resolver<String>>(){ })
						.annotatedWith(Names.named("digest")).toInstance(new DigestResolver());
					bind(Nonce.class).to(DefaultNonce.class);
				}
				
			});
		}

		public Injector resolve(Injector value) {
			return null;
		} 
	};
	
	private Nonce nonce = new DefaultNonce();
	private String email = "mail@example.com";
	private String digest = nonce.get(email);
	
	@Rule
	public WicketTesterResource wicketResource = new WicketTesterResource(injectBuilder);

	public static class ProfileTestPage extends WebPage { 
	
		private static final long serialVersionUID = -1836746839650822372L;
		
		public ProfileTestPage(String digest) {
			add(new Profile("profile", digest));
		}
	}
	
	@Test
	public void renderedSuccess() {

		User user = new User("komusubi");
		user.setPassword(new DigestResolver().resolve("vulnerable"))
			.setName("こむすび")
			.setEmail(email)
			.getHealth().setActive(true);
		Mockito.when(mock.create(user)).thenReturn("komusubi");
		
		WicketTester tester = wicketResource.getTester();
		
		tester.getSession().setAttribute(Nonce.class.getName(), nonce);
		
		tester.startPage(new ProfileTestPage(digest));
		
		FormTester formTester = tester.newFormTester("profile:profile.form");
		formTester.setValue("id", "komusubi");
		formTester.setValue("password", "vulnerable");
		formTester.setValue("confirm", "vulnerable");
		formTester.setValue("name", "こむすび");
		formTester.setValue("email", email);
		formTester.submit();
		
		tester.assertRenderedPage(ProfileTestPage.class);
		tester.assertInfoMessages("komusubi registry completed!");
		
		Mockito.verify(mock).create(user);
	}
	
	@Test
	public void renderedErrorPage() {

		WicketTester tester = wicketResource.getTester();
		
		tester.startPage(new ProfileTestPage(digest));
		
		FormTester formTester = tester.newFormTester("profile:profile.form");
		formTester.setValue("id", "komusubi");
		formTester.setValue("password", "vulnerable");
		formTester.setValue("confirm", "vulnerable");
		formTester.setValue("name", "こむすび");
		formTester.setValue("email", email);
		formTester.submit();
		
		tester.assertRenderedPage(BrowserInfoPage.class);
//		tester.
		tester.assertRenderedPage(ErrorPage.class);
	}
	
	@Test
	public void validateRequired() {
		mock = Mockito.mock(AccountService.class);

		WicketTester tester = wicketResource.getTester();
		tester.getSession().setAttribute(Nonce.class.getName(), nonce);
		
		tester.startPage(new ProfileTestPage(digest));
		
		FormTester formTester = tester.newFormTester("profile:profile.form");
		formTester.setValue("id", "");
		formTester.setValue("password", "");
		formTester.setValue("confirm", "");
		formTester.setValue("name", "");
		formTester.setValue("email", "");
		formTester.submit();

		tester.assertErrorMessages("IDを入力してください。",
										"名前を入力してください。",
										"パスワードを入力してください。",
										"確認用パスワードを入力してください。",
										"メールアドレスを入力してください。");
		
		tester.assertRenderedPage(ProfileTestPage.class);
	}
	
	@Test
	public void validateCombination() {
		
		WicketTester tester = wicketResource.getTester();
		tester.getSession().setAttribute(Nonce.class.getName(), nonce);
		
		tester.startPage(new ProfileTestPage(digest));
		
		FormTester formTester = tester.newFormTester("profile:profile.form");
		formTester.setValue("id", "komusubi");
		formTester.setValue("password", "password");
		formTester.setValue("confirm", "passwordd");
		formTester.setValue("name", "こむすび");
		formTester.setValue("email", "dummy@email.com");
		formTester.submit();

		tester.assertErrorMessages("メールアドレスが一致しません。",
										"パスワードが一致していません。");
		
		tester.assertRenderedPage(ProfileTestPage.class);
	}
	
	@Test
	public void submit() {
		WicketTester tester = wicketResource.getTester();
		
		
	}
}
