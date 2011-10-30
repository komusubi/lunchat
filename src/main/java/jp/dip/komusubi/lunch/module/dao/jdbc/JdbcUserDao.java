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

import jp.dip.komusubi.lunch.model.Health;
import jp.dip.komusubi.lunch.model.User;
import jp.dip.komusubi.lunch.module.dao.HealthDao;
import jp.dip.komusubi.lunch.module.dao.UserDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

public class JdbcUserDao extends SimpleJdbcDaoSupport implements UserDao {
	private static final Logger logger = LoggerFactory.getLogger(JdbcUserDao.class);
	private static final String SELECT_RECORD_QUERY = 
			"select id, password, name, email from user where id = ?";
	private static final String INSERT_RECORD_QUERY = 
			"insert into user (id, name, password, email) values (?, ?, ?, ?)";
	private static final String UPDATE_RECORD_QUERY = 
			"update user set password = ?, name = ?, email = ? where id = ?";
	private HealthDao healthDao;
	
	@Inject
	public JdbcUserDao(DataSource dataSource, HealthDao healthDao) {
		setDataSource(dataSource);
		this.healthDao = healthDao;
	}
	
	public User find(String pk) {
		User user = null;
		try {
			user = getSimpleJdbcTemplate().queryForObject(SELECT_RECORD_QUERY, userRowMapper, pk);
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
		getSimpleJdbcTemplate().update(INSERT_RECORD_QUERY, 
				instance.getId(), instance.getName(),	
				instance.getPassword(), instance.getEmail());
		healthDao.persist(instance.getHealth());
		return instance.getId();
	}

	public void remove(User instance) {
		throw new UnsupportedOperationException("remove");
	}

	public void update(User instance) {
		getSimpleJdbcTemplate().update(UPDATE_RECORD_QUERY, 
				instance.getPassword(), instance.getName(),
				instance.getEmail(), instance.getId());
	}

	public void update(Health instance) {
		healthDao.update(instance);
	}
	
	public List<User> findByEmail(String email) {
		throw new UnsupportedOperationException("findByEmail");
	}

	private RowMapper<User> userRowMapper = new RowMapper<User>() {
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User(rs.getString("id"))
							.setHealth(healthDao.find(rs.getString("id")))
							.setPassword(rs.getString("password"))
							.setName(rs.getString("name"))
							.setEmail(rs.getString("email"));
			return user;
		}
	};
}
