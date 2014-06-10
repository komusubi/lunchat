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

import java.text.ParseException;
import java.util.Date;

import jp.dip.komusubi.lunch.module.DefaultNonce;
import jp.dip.komusubi.lunch.module.dao.MockContractDao;
import jp.dip.komusubi.lunch.module.dao.MockGroupDao;
import jp.dip.komusubi.lunch.module.dao.MockUserDao;
import jp.dip.komusubi.lunch.module.resolver.DigestResolver;
import jp.dip.komusubi.lunch.service.AccountService;
import jp.dip.komusubi.lunch.util.Nonce;
import jp.dip.komusubi.lunch.wicket.WicketTesterResource;
import jp.dip.komusubi.lunch.wicket.page.error.ErrorPage;
import jp.lunchat.core.model.User;
import jp.lunchat.storage.dao.ContractDao;
import jp.lunchat.storage.dao.GroupDao;
import jp.lunchat.storage.dao.UserDao;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.Url;
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
					bind(ContractDao.class).to(MockContractDao.class);
					bind(GroupDao.class).to(MockGroupDao.class);
					bind(new TypeLiteral<Resolver<String>>(){ })
						.annotatedWith(Names.named("digest")).toInstance(new DigestResolver());
					bind(new TypeLiteral<Resolver<Date>>(){ })
						.annotatedWith(Names.named("date")).toInstance(new SpecificDateResolver());
					bind(Nonce.class).to(DefaultNonce.class);
				}
				
			});
		}

		public Injector resolve(Injector value) {
			return null;
		} 
	};
	
	/**
	 * specific date resolver. 
	 * @author jun.ozeki
	 */
	private class SpecificDateResolver implements Resolver<Date> {

        @Override
        public Date resolve() {
            try {
                return DateUtils.parseDate(ymd, new String[]{"yyyy/MM/dd HH:mm:ss"});
            } catch (ParseException e) {
                return null;
            }
        }

        @Override
        public Date resolve(Date value) {
            // TODO Auto-generated method stub
            return null;
        }
	    
	}
	
	private Nonce nonce = new DefaultNonce();
	private String email = "mail@example.com";
	private String digest = nonce.get(email);
	private String ymd = "2012/02/04 18:01:00";
	private static final String wicketFormId = "profile:profile.form";
	
	@Rule
	public WicketTesterResource wicketResource = new WicketTesterResource(injectBuilder);

	// dummy page for unit test
	public static class ProfileTestPage extends WebPage { 
	
		private static final long serialVersionUID = -1836746839650822372L;
		
		public ProfileTestPage(final String digest) {
			add(new Profile("profile", Model.of(digest)));
		}
	}
	
	@Test
	public void registerSuccess() {
	    Integer id = new Integer(1);
		User user = new User((Integer) null);
		user.setPassword(new DigestResolver().resolve("vulnerable"))
			.setName("こむすび")
			.setNickname("komusubi")
			.setEmail(email)
			.setJoined(new SpecificDateResolver().resolve())
			.getHealth().setActive(true);
		
		Mockito.when(mock.create(user)).thenReturn(id);
		
		WicketTester tester = wicketResource.getTester();
		
		tester.getSession().setAttribute(Nonce.class.getName(), nonce);
		
		tester.startPage(new ProfileTestPage(digest));

		FormTester formTester = tester.newFormTester(wicketFormId);
		formTester.setValue("nickname", "komusubi");
		formTester.setValue("password", "vulnerable");
		formTester.setValue("confirm", "vulnerable");
		formTester.setValue("name", "こむすび");
		formTester.setValue("email", email);
		formTester.submit();
		
		tester.assertRenderedPage(ProfileTestPage.class);
		tester.assertInfoMessages("こむすびさん、登録が完了しました。ログイン画面よりご利用ください。");
		
		Mockito.verify(mock).create(user);
	}
	
	@Test
	public void renderedErrorPageNoNonce() {

		WicketTester tester = wicketResource.getTester();
		
		tester.startPage(new ProfileTestPage(digest));
		
		FormTester formTester = tester.newFormTester(wicketFormId);
		formTester.setValue("nickname", "komusubi");
		formTester.setValue("password", "vulnerable");
		formTester.setValue("confirm", "vulnerable");
		formTester.setValue("name", "こむすび");
		formTester.setValue("email", email);
		formTester.submit();
		// follow onload submit event 
		tester.executeAjaxUrl(Url.parse("../postback/"));
		tester.assertRenderedPage(ErrorPage.class);
	}
	
	@Test
	public void validateRequired() {
		mock = Mockito.mock(AccountService.class);

		WicketTester tester = wicketResource.getTester();
		tester.getSession().setAttribute(Nonce.class.getName(), nonce);
		
		tester.startPage(new ProfileTestPage(digest));
		
		FormTester formTester = tester.newFormTester(wicketFormId);
		formTester.setValue("nickname", "");
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
		
		FormTester formTester = tester.newFormTester(wicketFormId);
		formTester.setValue("nickname", "komusubi");
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
