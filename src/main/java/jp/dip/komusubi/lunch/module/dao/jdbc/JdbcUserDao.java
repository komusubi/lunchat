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
package jp.dip.komusubi.lunch.module.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import jp.dip.komusubi.lunch.LunchException;
import jp.dip.komusubi.lunch.model.Health;
import jp.dip.komusubi.lunch.model.User;
import jp.dip.komusubi.lunch.module.dao.GroupDao;
import jp.dip.komusubi.lunch.module.dao.HealthDao;
import jp.dip.komusubi.lunch.module.dao.UserDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * user dao implemented by jdbc.
 * @author jun.ozeki
 * @since 2011/12/11
 */
public class JdbcUserDao implements UserDao {
	private static final Logger logger = LoggerFactory.getLogger(JdbcUserDao.class);
	private static String COLUMNS = "id, groupId, password, name, email";
	private static final String SELECT_RECORD_QUERY = "select " + COLUMNS + " from users where id = ?";
	private static final String INSERT_RECORD_QUERY = "insert into users (" + COLUMNS + ") values (?, ?, ?, ?, ?)";
	private static final String UPDATE_RECORD_QUERY = "update users set password = ?, name = ?, email = ?, groupId = ?"
					+ " where id = ?";
	private static final String SELECT_RECORD_BY_EMAIL = "select " + COLUMNS + " from users where email = ?";
	private static final String SELECT_RECORD_BY_GROUPID = "select " + COLUMNS + " from users where groupId = ?";
	@Inject private HealthDao healthDao;
	@Inject private GroupDao groupDao;
	private SimpleJdbcTemplate template;
	
	@Inject
	public JdbcUserDao(DataSource dataSource, HealthDao healthDao) {
		template = new SimpleJdbcTemplate(dataSource);
		this.healthDao = healthDao;
	}
	
	public User find(String pk) {
		User user = null;
		try {
			user = template.queryForObject(SELECT_RECORD_QUERY, userRowMapper, pk);
		} catch (EmptyResultDataAccessException e) {
			logger.info("not found user is {}", pk);
		}
		return user;
	}

	public List<User> findAll() {
		throw new UnsupportedOperationException("findAll");
	}

	public String persist(User instance) {
		if (instance == null || instance.getId() == null)
			throw new IllegalArgumentException("user: wrong instance " + instance);
		try {
			template.update(INSERT_RECORD_QUERY, 
					instance.getId(), 
					instance.getGroupId(),
					instance.getPassword(),
					instance.getName(),	
					instance.getEmail());
			healthDao.persist(instance.getHealth());
		} catch (DataAccessException e) {
			throw new LunchException(e);
		}
		return instance.getId();
	}

	public void remove(User instance) {
		throw new UnsupportedOperationException("remove");
	}

	public void update(User instance) {
		// groupId is nullable.
		String groupId = null;
		if (instance.getGroup() != null)
			groupId = instance.getGroup().getId();
		template.update(UPDATE_RECORD_QUERY, 
				instance.getPassword(), instance.getName(),
				instance.getEmail(), groupId, instance.getId());
		// update user's health 
		update(instance.getHealth());
	}

	public void update(Health instance) {
		healthDao.update(instance);
	}
	
	public User findByEmail(String email) {
		User user = null;
		try {
			user = template.queryForObject(SELECT_RECORD_BY_EMAIL, userRowMapper, email);
		} catch (EmptyResultDataAccessException e) {
			logger.info("not found user email is {}", email);
		}
		return user;
	}

	public List<User> findByGroupId(String groupId) {
		List<User> list;
		list = template.query(SELECT_RECORD_BY_GROUPID, userRowMapper, groupId);
		logger.info("findByGroupId find: {}", list.size());
		return list;
	}
	
	private RowMapper<User> userRowMapper = new RowMapper<User>() {
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			Health health = healthDao.find(rs.getString("id"));
			User user = health.getUser();
			user.setGroup(groupDao.find(rs.getString("groupId")))
				.setPassword(rs.getString("password"))
				.setName(rs.getString("name"))
				.setEmail(rs.getString("email"));
//			User user = new User(rs.getString("id"))
//							.setGroupId(rs.getString("groupId"))
//							.setGroup(groupDao.find(rs.getString("groupId")))
//							.setHealth(healthDao.find(rs.getString("id")))
//							.setPassword(rs.getString("password"))
//							.setName(rs.getString("name"))
//							.setEmail(rs.getString("email"));
			return user;
		}
	};
}
