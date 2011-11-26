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

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;

import java.io.PrintWriter;
import java.sql.DriverManager;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.sql.DataSource;

import jp.dip.komusubi.common.protocol.smtp.SmtpServer;
import jp.dip.komusubi.common.util.Resolver;
import jp.dip.komusubi.lunch.Configuration.RuntimeMode;
import jp.dip.komusubi.lunch.model.Authentication;
import jp.dip.komusubi.lunch.module.Basket;
import jp.dip.komusubi.lunch.module.DefaultAuthentication;
import jp.dip.komusubi.lunch.module.DefaultNonce;
import jp.dip.komusubi.lunch.module.Transactional;
import jp.dip.komusubi.lunch.module.dao.GroupDao;
import jp.dip.komusubi.lunch.module.dao.HealthDao;
import jp.dip.komusubi.lunch.module.dao.OrderDao;
import jp.dip.komusubi.lunch.module.dao.OrderLineDao;
import jp.dip.komusubi.lunch.module.dao.ProductDao;
import jp.dip.komusubi.lunch.module.dao.ShopDao;
import jp.dip.komusubi.lunch.module.dao.UserDao;
import jp.dip.komusubi.lunch.module.dao.jdbc.JdbcGroupDao;
import jp.dip.komusubi.lunch.module.dao.jdbc.JdbcHealthDao;
import jp.dip.komusubi.lunch.module.dao.jdbc.JdbcOrderDao;
import jp.dip.komusubi.lunch.module.dao.jdbc.JdbcOrderLineDao;
import jp.dip.komusubi.lunch.module.dao.jdbc.JdbcProductDao;
import jp.dip.komusubi.lunch.module.dao.jdbc.JdbcShopDao;
import jp.dip.komusubi.lunch.module.dao.jdbc.JdbcUserDao;
import jp.dip.komusubi.lunch.module.resolver.DateResolver;
import jp.dip.komusubi.lunch.module.resolver.DigestResolver;
import jp.dip.komusubi.lunch.service.AccountService;
import jp.dip.komusubi.lunch.service.Shopping;
import jp.dip.komusubi.lunch.service.ShoppingResource;
import jp.dip.komusubi.lunch.util.Nonce;
import jp.dip.komusubi.lunch.wicket.DevelopmentFilter;
import jp.dip.komusubi.lunch.wicket.WicketApplication;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.wicket.protocol.http.WicketFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class Bootstrap extends GuiceServletContextListener {

	@Override 
	protected Injector getInjector() {
		return buildInjector();
	}
	
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		Configuration.setServletContext(servletContextEvent.getServletContext());
		super.contextInitialized(servletContextEvent);
	}
	
	public Injector buildInjector() {
		Injector injector = Guice.createInjector(
				new PersistenceModule(),
				new ServiceModule(),
				new WebModule());
		return injector;
	}

	/**
	 * 
	 * @author jun.ozeki
	 * @version $Id: Bootstrap.java 1356 2010-12-31 05:13:01Z jun $
	 * @since 2010/12/30
	 */
	public static class WebModule extends ServletModule {
		@Override
		protected void configureServlets() {
			// rest resource
			bind(ShoppingResource.class);
			
			Map<String, String> param = new HashMap<String, String>(2);
			param.put(WicketFilter.FILTER_MAPPING_PARAM, "/*");
			param.put("applicationClassName", WicketApplication.class.getName());
			param.put("wicket.configuration", Configuration.mode().name());
			if (RuntimeMode.DEPLOYMENT.equals(Configuration.mode())) {
				bind(WicketFilter.class).in(Singleton.class);
				filter("/*").through(WicketFilter.class, param);
			} else {
				bind(DevelopmentFilter.class).in(Singleton.class);
				filter("/*").through(DevelopmentFilter.class, param);
			}
			serve("/v1", "/v2").with(GuiceContainer.class);
		}
	}

	/**
	 * 
	 * @author jun.ozeki
	 * @since 2011/09/24
	 */
	public static class ServiceModule extends AbstractModule {
		private SmtpServer getSmtpServer() {
			SmtpServer smtp = new SmtpServer();
			smtp.setUsername(Configuration.getParameter("smtp.user"));
			smtp.setPassword(Configuration.getParameter("smtp.password"));
			smtp.setHost(Configuration.getParameter("smtp.host"));
			smtp.setPort(Integer.parseInt(
					Configuration.getParameter("smtp.port")));
			
			smtp.setAuth(true);
			return smtp;
		}
		
		@Override
		protected void configure() {
			bind(SmtpServer.class).toInstance(getSmtpServer());
			bind(Authentication.class).to(DefaultAuthentication.class);
			bind(new TypeLiteral<Resolver<String>>(){ })
				.annotatedWith(Names.named("digest")).to(DigestResolver.class);
			bind(new TypeLiteral<Resolver<Date>>(){ })
				.annotatedWith(Names.named("date")).to(DateResolver.class);
			bind(AccountService.class);
			bind(Nonce.class).to(DefaultNonce.class);
			bind(Shopping.class);
			bind(Basket.class);
		}

	}
	
	/**
	 * 
	 * @author jun.ozeki
	 * @version $Id: Bootstrap.java 1356 2010-12-31 05:13:01Z jun $
	 * @since 2010/12/26
	 */
	public static class PersistenceModule extends AbstractModule {
		private DataSource dataSource;
		
		public PersistenceModule() {
			this.dataSource = newDataSource();
		}
		
		private DataSource newDataSource() {
			// debug 
			if (RuntimeMode.DEVELOPMENT.equals(Configuration.mode()))
				DriverManager.setLogWriter(new PrintWriter(System.out));
			
			BasicDataSource dataSource = new BasicDataSource();
			dataSource.setUrl(Configuration.getParameter("jdbc.url"));
			dataSource.setUsername(Configuration.getParameter("database.user"));
			dataSource.setPassword(Configuration.getParameter("database.password"));
			dataSource.setDefaultAutoCommit(false);
			dataSource.setMaxActive(20);
			dataSource.setMaxWait(5);
			return dataSource;
		}
		private DataSourceTransactionManager newTransactionManager() {
			DataSourceTransactionManager manager = new DataSourceTransactionManager(dataSource);
			return manager;
		}
		@Override
		protected void configure() {
			bind(UserDao.class).to(JdbcUserDao.class);
			bind(ShopDao.class).to(JdbcShopDao.class);
			bind(OrderDao.class).to(JdbcOrderDao.class);
			bind(OrderLineDao.class).to(JdbcOrderLineDao.class);
			bind(HealthDao.class).to(JdbcHealthDao.class);
			bind(GroupDao.class).to(JdbcGroupDao.class);
			bind(ProductDao.class).to(JdbcProductDao.class);
			bind(DataSource.class).toInstance(dataSource);
			bindInterceptor(any(), annotatedWith(Transactional.class), getTransactionInterceptor());
			bind(PlatformTransactionManager.class).toInstance(newTransactionManager());
		}
		
		protected MethodInterceptor getTransactionInterceptor() {
			return new TransactionInterceptor();
		}
	}
	
	public static class TransactionInterceptor implements MethodInterceptor {
		private static final Logger logger = LoggerFactory.getLogger(TransactionInterceptor.class);
		
		public Object invoke(MethodInvocation invocation) throws Throwable {
			Object obj = null;
			PlatformTransactionManager txManager = 
					Configuration.getInstance(PlatformTransactionManager.class);
			TransactionStatus status = null;
			try {

				status = txManager.getTransaction(null);
				
				obj = invocation.proceed();
				
				txManager.commit(status);

			} catch (Exception e) {
				logger.warn("database rollback: {}", e);
				txManager.rollback(status);
				throw e;
			}
			return obj;
		}
		
	}
}

