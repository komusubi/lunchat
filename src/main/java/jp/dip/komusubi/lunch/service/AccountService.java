/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package jp.dip.komusubi.lunch.service;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.inject.Named;

import jp.dip.komusubi.common.protocol.smtp.MailContent;
import jp.dip.komusubi.common.protocol.smtp.MailMessage;
import jp.dip.komusubi.common.protocol.smtp.SmtpServer;
import jp.dip.komusubi.common.util.Resolver;
import jp.dip.komusubi.common.util.XmlResourceBundle;
import jp.dip.komusubi.common.util.XmlResourceBundleControl;
import jp.dip.komusubi.lunch.Configuration;
import jp.dip.komusubi.lunch.model.User;
import jp.dip.komusubi.lunch.module.Transactional;
import jp.dip.komusubi.lunch.module.dao.UserDao;
import jp.dip.komusubi.lunch.util.Nonce;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountService implements Serializable {
	private static final long serialVersionUID = -9045008918138414477L;
	private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
	private static final String BUNDLE_NAME = "jp.dip.komusubi.lunch.service.AccountService"; //$NON-NLS-1$
	private static final ResourceBundle RESOURCE_BUNDLE 
							= XmlResourceBundle.getBundle(BUNDLE_NAME, new XmlResourceBundleControl());
	protected static String format(String key, Object... args) {
		return MessageFormat.format(getString(key), args);
	}

	protected static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	private transient UserDao userDao;
	private SmtpServer smtp;
	private transient Resolver<String> resolver;
	@Inject
	@Named("date")
	private transient Resolver<Date> dateResolver;
	
	@Inject
	public AccountService(UserDao userDao, 
							@Named("digest") Resolver<String> resolver, 
							SmtpServer smtp) {
		this.userDao = userDao;
		this.resolver = resolver;
		this.smtp = smtp;
	}

	@Transactional
	public String create(User user) {
		String id = userDao.persist(user);
		logger.info("created user, id:{}, name:{}", user.getId(), user.getName());
		return id;
	}

	public void remove(String id) {
		userDao.remove(new User(id));
	}
	
	public User find(String id) {
		return userDao.find(id);
	}
	
	@Transactional
	public boolean signIn(String id, String password) {
		boolean evaluate = false;
		User user = userDao.find(id);
		if (user == null)
			return evaluate;
		
		if (logger.isDebugEnabled())
			logger.debug("user is {}", user);
		
		if (!user.getHealth().isActive()) {
			logger.info("account locked, id:{}, name:{}", user.getId(), user.getName());
			return evaluate;
		}
		
		// account lock when login fail over 5 times.  
		if (user.getHealth().getLoginFail() >= 5) {
			user.getHealth().setActive(false);
		}
		
		if (id.equals(user.getId()) && 
				resolver.resolve(password).equals(user.getPassword())) {
			// clear user health, after authenticated 
			user.getHealth().incrementLogin()
								.setLastLogin(dateResolver.resolve())
								.setLoginFail(0);
			evaluate = true;
		} else {
			logger.info("password unmatch: id:{}, password:{}", id, password);
			user.getHealth().incrementLoginFail();
		}
		userDao.update(user.getHealth());
		return evaluate;
	}
	
	public Nonce apply(User user, String url) {
		Nonce nonce = Configuration.getInstance(Nonce.class);
		StringBuilder builder = new StringBuilder(url);
		if (!url.endsWith("/"))
			builder.append("/");
		builder.append(nonce.get(user.getEmail()));
		try {
			MailContent content = new MailContent();
			User from = new User();
			from.setName(getString("confirm.mail.from.name")) //$NON-NLS-1$
				.setEmail(getString("confirm.mail.from.address")); //$NON-NLS-1$
			content.setSubject(getString("confirm.mail.title")); //$NON-NLS-1$
			content.setBody(format("confirm.mail.body", builder.toString())); //$NON-NLS-1$
			MailMessage mail = new MailMessage();
			mail.setContent(content);
			mail.setFrom(from);
			mail.addToRecipient(user);
			smtp.send(mail);
			logger.info("send to {}, subject is {},", user.getEmail(), getString("confirm.mail.title"));
			return nonce;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	public Nonce activate(User user, String url) {
		return null;
	}
}
