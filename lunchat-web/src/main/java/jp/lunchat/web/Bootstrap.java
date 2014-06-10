/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package jp.lunchat.web;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;

import java.io.PrintWriter;
import java.sql.DriverManager;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.sql.DataSource;

import jp.dip.komusubi.lunch.module.resolver.DateResolver;
import jp.dip.komusubi.lunch.module.resolver.DigestResolver;
import jp.dip.komusubi.lunch.module.resolver.Resolvers;
import jp.dip.komusubi.lunch.util.Nonce;
import jp.dip.komusubi.lunch.wicket.DevelopmentFilter;
import jp.dip.komusubi.lunch.wicket.WicketApplication;
import jp.lunchat.core.model.Authentication;
import jp.lunchat.storage.Basket;
import jp.lunchat.storage.DefaultAuthentication;
import jp.lunchat.storage.DefaultNonce;
import jp.lunchat.storage.Transactional;
import jp.lunchat.storage.dao.ContractDao;
import jp.lunchat.storage.dao.GroupDao;
import jp.lunchat.storage.dao.HealthDao;
import jp.lunchat.storage.dao.OrderDao;
import jp.lunchat.storage.dao.OrderLineDao;
import jp.lunchat.storage.dao.ProductDao;
import jp.lunchat.storage.dao.ReceiptDao;
import jp.lunchat.storage.dao.ReceiptLineDao;
import jp.lunchat.storage.dao.ShopDao;
import jp.lunchat.storage.dao.UserDao;
import jp.lunchat.storage.dao.jdbc.JdbcContractDao;
import jp.lunchat.storage.dao.jdbc.JdbcGroupDao;
import jp.lunchat.storage.dao.jdbc.JdbcHealthDao;
import jp.lunchat.storage.dao.jdbc.JdbcOrderDao;
import jp.lunchat.storage.dao.jdbc.JdbcOrderLineDao;
import jp.lunchat.storage.dao.jdbc.JdbcProductDao;
import jp.lunchat.storage.dao.jdbc.JdbcReceiptDao;
import jp.lunchat.storage.dao.jdbc.JdbcReceiptLineDao;
import jp.lunchat.storage.dao.jdbc.JdbcShopDao;
import jp.lunchat.storage.dao.jdbc.JdbcUserDao;
import jp.lunchat.web.Configuration.RuntimeMode;
import jp.lunchat.web.service.AccountService;
import jp.lunchat.web.service.BackOffice;
import jp.lunchat.web.service.Shopping;
import jp.lunchat.web.service.ShoppingResource;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.wicket.protocol.http.WicketFilter;
import org.komusubi.common.protocol.smtp.SmtpServer;
import org.komusubi.common.util.Resolver;
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

/**
 * bootstrap module configuration.
 * @author jun.ozeki
 */
public class Bootstrap extends GuiceServletContextListener {

    /**
     * get injector.
     */
    @Override
    protected Injector getInjector() {
        return buildInjector();
    }

    /**
     * initialize servlet context.
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        Configuration.setServletContext(servletContextEvent.getServletContext());
        super.contextInitialized(servletContextEvent);
    }

    /**
     * build injector.
     * @return
     */
    public Injector buildInjector() {
        Injector injector = Guice.createInjector(new PersistenceModule(), new ServiceModule(), new WebModule());
        return injector;
    }

    /**
     * web module.
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
     * service module.
     * @author jun.ozeki
     * @since 2011/09/24
     */
    public static class ServiceModule extends AbstractModule {
        private SmtpServer getSmtpServer() {
            SmtpServer smtp = new SmtpServer();
            smtp.setUsername(Configuration.getParameter("smtp.user"));
            smtp.setPassword(Configuration.getParameter("smtp.password"));
            smtp.setHost(Configuration.getParameter("smtp.host"));
            smtp.setPort(Integer.parseInt(Configuration.getParameter("smtp.port")));

            smtp.setAuth(true);
            return smtp;
        }

        @Override
        protected void configure() {
            bind(SmtpServer.class).toInstance(getSmtpServer());
            bind(Authentication.class).to(DefaultAuthentication.class);
            bind(new TypeLiteral<Resolver<String>>() {}).annotatedWith(Names.named("digest")).to(DigestResolver.class);
            bind(new TypeLiteral<Resolver<Date>>() {}).annotatedWith(Names.named("date")).to(DateResolver.class);
            bind(new TypeLiteral<Resolver<Calendar>>() {}).annotatedWith(Names.named("calendar")).to(Resolvers.CalendarResolver.class);
            bind(AccountService.class);
            bind(BackOffice.class);
            bind(Nonce.class).to(DefaultNonce.class);
            bind(Shopping.class);
            bind(Basket.class);
        }

    }

    /**
     * persistence module.
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
            bind(ReceiptDao.class).to(JdbcReceiptDao.class);
            bind(ReceiptLineDao.class).to(JdbcReceiptLineDao.class);
            bind(HealthDao.class).to(JdbcHealthDao.class);
            bind(GroupDao.class).to(JdbcGroupDao.class);
            bind(ProductDao.class).to(JdbcProductDao.class);
            bind(ContractDao.class).to(JdbcContractDao.class);
            bind(DataSource.class).toInstance(dataSource);
            bindInterceptor(any(), annotatedWith(Transactional.class), getTransactionInterceptor());
            bind(PlatformTransactionManager.class).toInstance(newTransactionManager());
        }

        protected MethodInterceptor getTransactionInterceptor() {
            return new TransactionInterceptor();
        }
    }

    /**
     * transaction interceptor.
     * @author jun.ozeki
     */
    public static class TransactionInterceptor implements MethodInterceptor {
        private static final Logger logger = LoggerFactory.getLogger(TransactionInterceptor.class);

        public Object invoke(MethodInvocation invocation) throws Throwable {
            Object obj = null;
            PlatformTransactionManager txManager = Configuration.getInstance(PlatformTransactionManager.class);
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
