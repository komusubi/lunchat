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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.inject.Named;

import jp.dip.komusubi.lunch.Configuration;
import jp.dip.komusubi.lunch.LunchException;
import jp.dip.komusubi.lunch.model.Contract;
import jp.dip.komusubi.lunch.model.Group;
import jp.dip.komusubi.lunch.model.Order;
import jp.dip.komusubi.lunch.model.User;
import jp.dip.komusubi.lunch.module.Transactional;
import jp.dip.komusubi.lunch.module.dao.ContractDao;
import jp.dip.komusubi.lunch.module.dao.GroupDao;
import jp.dip.komusubi.lunch.module.dao.UserDao;
import jp.dip.komusubi.lunch.util.Nonce;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.komusubi.common.protocol.smtp.MailContent;
import org.komusubi.common.protocol.smtp.MailMessage;
import org.komusubi.common.protocol.smtp.SmtpServer;
import org.komusubi.common.util.Resolver;
import org.komusubi.common.util.XmlResourceBundle;
import org.komusubi.common.util.XmlResourceBundleControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * account service.
 * @author jun.ozeki
 * @since 2011/12/04
 */
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
	private transient Resolver<String> digester;
	@Inject @Named("date") private transient Resolver<Date> dateResolver;
	@Inject private GroupDao groupDao;
	@Inject private ContractDao contractDao;
	private User authedUser;
	
	@Inject
	public AccountService(UserDao userDao, 
							@Named("digest") Resolver<String> resolver, 
							SmtpServer smtp) {
		this.userDao = userDao;
		this.digester = resolver;
		this.smtp = smtp;
	}

	// for unit test
	protected AccountService(UserDao userDao,
							@Named("digest") Resolver<String> resolver,
							SmtpServer smtp,
							@Named("date") Resolver<Date> dateResolver) {
		this(userDao, resolver, smtp);
		this.dateResolver = dateResolver;
	}
	
	@Transactional
	public Integer create(User user) {
		Integer id = userDao.persist(user);
		logger.info("created user, id:{}, name:{}", user.getId(), user.getName());
		return id;
	}

	@Transactional
	public Integer referTo(User user) {
		Group group = user.getGroup();
		if (group == null)
			throw new IllegalArgumentException("user's group is null");
		Integer id = groupDao.persist(group);
		// relation to groupId
		userDao.update(user.getHealth());
		for (Contract contract: group.getContracts()) {
			if (contract.getId() == Contract.DEFAULT_ID)
				contractDao.persist(contract);
		}
		return id;
	}
	
	@Transactional
	public void remove(User user) {
		userDao.remove(user);
		// FIXME when group has nobody, delete same time.
	}
	
	@Transactional
	public void modify(User user) {
		userDao.update(user);
	}
	
	public User find(String email) {
		User user = null;
		if (authedUser != null && authedUser.getEmail().equals(email))
			user = authedUser;
		else
			user = userDao.findByEmail(email);
		return user;
	}
	
	public boolean isDeadline(User user, Date now) {
		boolean result = false;
		if (user.getGroupId() == null)
			return result;
		Group group = groupDao.find(user.getGroupId());
		if (group.getLastOrder().before(now))
			result = true;
		return result;
	}
	
	public boolean isDeadline(User user) {
		return isDeadline(user, dateResolver.resolve());
	}
	
	public User readByEmail(String email) {
		return userDao.findByEmail(email);
	}
	
	protected void temporarySupply() {
		// FIXME supply product from back office here. 
		BackOffice backOffice = Configuration.getInstance(BackOffice.class);
		backOffice.supplyProduct();
	}
	
	@Transactional
	public boolean signIn(String email, String password) {
		temporarySupply();
		
		boolean evaluate = false;
//		User user = userDao.find(id);
		User user = userDao.findByEmail(email);
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
		
		if (email.equals(user.getEmail()) &&
				digester.resolve(password).equals(user.getPassword())) {
			// clear user health, after authenticated 
			user.getHealth().incrementLogin()
								.setLastLogin(dateResolver.resolve())
								.setLoginFail(0);
			evaluate = true;
		} else {
			logger.info("password unmatch, email:{}, password:{}", email, password);
			user.getHealth().incrementLoginFail();
		}
		userDao.update(user.getHealth());
		authedUser = user;
		return evaluate;
	}
	
	/**
	 * apply to create a account for new user.
	 * @param user for registry.
	 * @param url temporary url for authentication.
	 * @return
	 */
	public Nonce apply(User user, String url) {
		Nonce nonce = Configuration.getInstance(Nonce.class);
		StringBuilder builder = new StringBuilder(url);
		if (!url.endsWith("/"))
			builder.append("/");
		builder.append(nonce.get(user.getEmail()));
		try {
			MailContent content = new MailContent();
//			admin.setName(getString("confirm.mail.from.name")) //$NON-NLS-1$
//				.setEmail(getString("confirm.mail.from.address")); //$NON-NLS-1$
			content.setSubject(getString("confirm.mail.title")); //$NON-NLS-1$
			content.setBody(format("confirm.mail.body", builder.toString())); //$NON-NLS-1$
			MailMessage mail = new MailMessage();
			mail.setContent(content);
			mail.setFrom(getAdminUser());
			mail.addToRecipient(user);
			smtp.send(mail);
			logger.info("send to {}, subject is {},", user.getEmail(), getString("confirm.mail.title"));
			return nonce;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * apply to be a member of group.
	 * @param who admit to member of group.
	 * @param whom request to member of group.
	 * @param url 
	 */
	@Transactional
	public void apply(User who, User whom, String url) {
		StringBuilder builder = new StringBuilder(url);
		if (!url.endsWith("/"))
			builder.append("/");
		try {
			// nonce ?
			Date stamp = dateResolver.resolve();
		    String fragment = digest(who, whom, stamp);
			builder.append(fragment);
			MailContent content = new MailContent();
//			admin.setName(getString("admit.mail.from.name"))
//				.setEmail(getString("admit.mail.from.address"));
			content.setSubject(getString("admit.mail.title"));
			content.setBody(format("admit.mail.body",
					who.getName(),
					who.getGroup().getName(),
					whom.getName(),
					whom.getNickname(),
					builder.toString()));
			MailMessage mail = new MailMessage();
			mail.setContent(content);
			mail.setFrom(getAdminUser());
			mail.addToRecipient(who);
			smtp.send(mail);
			logger.info("send to {}, subject is {}.", who.getEmail(), getString("admit.mail.title"));
			
			// persist after mail sent.
			// set fragment requester's admitter in temporary.
			whom.getHealth().setAdmitter(fragment);
			whom.getHealth().setGroupJoined(stamp);
//			updateHealth(whom.getHealth());
			userDao.update(whom.getHealth());
		} catch (IllegalStateException e) {
		    throw e;
		} catch (Exception e) {
	        throw new IllegalStateException(e);
		}		
	}
	
	/**
	 * approve applicant to join a member of group. 
	 * @param admitter 
	 * @param applicant
	 * @param message 
	 */
	@Transactional
	public void approve(User admitter, User applicant, String message, String url) {
	    try {
    	    applicant.getHealth().setGroup(admitter.getGroup());
    	    applicant.getHealth().setGroupJoined(dateResolver.resolve());
    	    applicant.getHealth().setAdmitter(admitter.getId().toString());
    	    userDao.update(applicant.getHealth());
    	    // mail
    	    MailContent content = new MailContent();
    	    content.setSubject(getString("approve.mail.subject"));
    	    content.setBody(format("approve.mail.body",
    	                            applicant.getName(),
    	                            applicant.getGroup().getName(),
    	                            applicant.getGroup().getCode(),
    	                            admitter.getNickname(),
    	                            message,
    	                            url));
    	    MailMessage mail = new MailMessage();
    	    mail.setContent(content);
    	    mail.setFrom(getAdminUser());
    	    mail.addToRecipient(applicant);
    	    smtp.send(mail);
    	    logger.info("send to {}, subject is {}", applicant.getEmail(), getString("approve.mail.subject"));
    	    
	    } catch (IllegalStateException e) {
	        throw e;
	    } catch (Exception e) {
	        throw new IllegalStateException(e);
	    }
	}
	
	/**
	 * decline to join a member of group.
	 * @param admitter
	 * @param applicant
	 * @param message
	 */
	public void decline(User admitter, User applicant, String message) {
	    try {
	        MailContent content = new MailContent();
	        content.setSubject(getString("deny.mail.subject"));
	        content.setBody(format("deny.mail.body",
	                            applicant.getName(),
	                            admitter.getGroup().getName(),
	                            admitter.getGroup().getCode(),
	                            admitter.getNickname(),
	                            message));
	        MailMessage mail = new MailMessage();
	        mail.setContent(content);
	        mail.setFrom(getAdminUser());
	        mail.addToRecipient(applicant);
	        smtp.send(mail);
	        logger.info("send to {}, subject is {}", applicant.getEmail(), getString("deny.mail.subject"));
	        
	    } catch (IllegalStateException e) {
	        throw e;
	    } catch (Exception e) {
	        throw new IllegalStateException(e);
	    }
	}
	
	private User getAdminUser() {
	    return new User()
	                .setName(getString("admin.name"))
	                .setEmail(getString("admin.email"));
	}
	
//	@Transactional
//	public void updateHealth(Health health) {
//		userDao.update(health);
//	}
	
	public Nonce remind(String email, String url) {
		User to = userDao.findByEmail(email);
		if (to == null) 
			throw new NotFoundEmailException("not found email: " + email);
		StringBuilder urlBuilder = new StringBuilder(url);
		if (!url.endsWith("?"))
			urlBuilder.append("?");
		Nonce nonce = Configuration.getInstance(Nonce.class);
		urlBuilder.append("activate=")
					.append(nonce.get(email));
		try {
			MailContent content = new MailContent();
			User from = new User();
			from.setName(getString("remind.mail.from.name"))
				.setEmail(getString("remind.mail.from.address"));
			content.setSubject(getString("remind.mail.title"));
			content.setBody(format("remind.mail.body", to.getName(), urlBuilder.toString()));
			MailMessage mail = new MailMessage();
			mail.setContent(content);
			mail.setFrom(from);
			mail.addToRecipient(to);
			smtp.send(mail);
			logger.info("send to {}, subject is {}", to.getEmail(), getString("remind.mail.title"));
			return nonce;
		} catch (Exception e) {
			throw new LunchException(e);
		}
	}
	
	@Transactional
	public boolean activate(String email, Nonce nonce, String requestedNonce) {
		boolean result = false;
		if (email == null || nonce == null || requestedNonce == null) {
			logger.info("can't activate again, id is:{}, nonce:{}, requested:{}", 
					new Object[]{email, nonce, requestedNonce});
			return result;
		}
		User user = userDao.findByEmail(email);
		if (user == null)
			return result;
		
		if (requestedNonce.equals(nonce.get(user.getEmail()))) {
			user.getHealth().setActive(true)
								.setLoginFail(0);
			userDao.update(user.getHealth());
			result = true;
			logger.info("activate again success:{}", user);
		}
		return result;
	}
	
//	public List<Shop> getContractedShops(User user) {
////		List<Shop> list = new ArrayList<>();
////		Group group = groupDao.find(user.getGroupId());
////		for (Contract c: group.getContracts()) {
////			list.add(shopDao.find(c.getShopId()));
////		}
////		return list;
//		if (user.getGroup() == null)
//			return Collections.emptyList();
//		
//		List<Contract> contracts = user.getGroup().getContracts();
//		List<Shop> shops = new ArrayList<>();
//		for (Contract contract: contracts)
//			shops.add(contract.getShop());
//
//		return shops;
//	}
	
	public List<Order> getOrders(User user, Date date) {
//		List<Order> orders = orderDao.findByUserAndDate(user.getId(), date);
		List<Order> orders = new ArrayList<>();
		return orders;
	}
	
	public String digest(User who, User whom, Date stamp) {
	    String groupCode = who.getGroup() != null ? who.getGroup().getCode() : "";
	    String admitterEmail = who.getEmail();
	    String timeStamp = DateFormatUtils.format(stamp, "yyyyMMddHHmmss");
	    String applicantEmail = whom.getEmail();
	    
        String fragment = digester.resolve(new StringBuilder(groupCode)
                                                .append(admitterEmail)
                                                .append(timeStamp)
                                                .append(applicantEmail).toString());
        logger.info("digest value is groupCode:{}, admitter email:{}, stamp:{}, applicant email:{}",
                new Object[]{ groupCode, admitterEmail, timeStamp, applicantEmail });

        return fragment;
	}
}
