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
import jp.lunchat.LunchException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

/**
 * user dao implemented by jdbc.
 * @author jun.ozeki
 * @since 2011/12/11
 */
public class JdbcUserDao implements UserDao {
	private static final Logger logger = LoggerFactory.getLogger(JdbcUserDao.class);
	private static String COLUMNS = "email, id, password, nickname, name, joined";
	private static final String SELECT_RECORD_QUERY = "select " + COLUMNS + " from users where id = ?";
	private static final String INSERT_RECORD_QUERY = "insert into users (" + COLUMNS + ") values (:email, :id, :password, :nickname, :name, :joined)";
	private static final String UPDATE_RECORD_QUERY = "update users set password = ?, name = ?, email = ?, nickname = ?"
					+ " where id = ?";
	private static final String SELECT_RECORD_BY_EMAIL = "select " + COLUMNS + " from users where email = ?";
	private static final String SELECT_RECORD_BY_GROUPID = "select " + COLUMNS + " from users, health"
					+ " where health.groupId = ? and health.userId = id";
	private static final String SELECT_RECORD_BY_NICKNAME = "select " + COLUMNS + " from users where nickname = ?";
    private static final String SELECT_RECORD_BY_ADMITTER = "select " + COLUMNS + " from users, health where admitted = ? and health.userId = id"; 
	private HealthDao healthDao;
	private SimpleJdbcTemplate simple;
	private NamedParameterJdbcTemplate template;
	
	@Inject
	public JdbcUserDao(DataSource dataSource, HealthDao healthDao) {
		simple = new SimpleJdbcTemplate(dataSource);
	    template = new NamedParameterJdbcTemplate(dataSource);
		this.healthDao = healthDao;
	}

    public User findByEmail(String email) {
        User user = null;
        try {
            user = simple.queryForObject(SELECT_RECORD_BY_EMAIL, userRowMapper, email);
        } catch (EmptyResultDataAccessException e) {
            logger.info("not found user email is {}", email);
        }
        return user;
    }

    public User findByNickname(String nickname) {
        User user = null;
        try {
            user = simple.queryForObject(SELECT_RECORD_BY_NICKNAME, userRowMapper, nickname);
        } catch (EmptyResultDataAccessException e) {
            logger.info("not found user nickname is {}", nickname);
        }
        return user;
    }
    
    public List<User> findByGroupId(Integer groupId) {
        List<User> list;
        list = simple.query(SELECT_RECORD_BY_GROUPID, userRowMapper, groupId);
        logger.info("findByGroupId find: {}", list.size());
        return list;
    }
    
    public List<User> findByAdmitter(String name) {
        List<User> users;
        users = simple.query(SELECT_RECORD_BY_ADMITTER, userRowMapper, name);
        logger.info("findBYAdmitter find: {}", users.size());
        return users;
    }
    
	public User find(Integer pk) {
		User user = null;
		try {
			user = simple.queryForObject(SELECT_RECORD_QUERY, userRowMapper, pk);
		} catch (EmptyResultDataAccessException e) {
			logger.info("not found user is {}", pk);
		}
		return user;
	}

	public List<User> findAll() {
		throw new UnsupportedOperationException("findAll");
	}

	public Integer persist(User instance) {
	    GeneratedKeyHolder holder = new GeneratedKeyHolder();
		try {
			validate(instance);
			MapSqlParameterSource sqlParameter = new MapSqlParameterSource()
			                                        .addValue("email", instance.getEmail())
			                                        .addValue("id", instance.getId())
			                                        .addValue("password", instance.getPassword())
			                                        .addValue("nickname", instance.getNickname())
			                                        .addValue("name", instance.getName())
			                                        .addValue("joined", instance.getJoined());
			template.update(INSERT_RECORD_QUERY, sqlParameter, holder);

			// health 
			healthDao.persist(instance.getHealth());
			logger.info("persisted: {}", instance);
		} catch (DataAccessException e) {
			throw new LunchException(e);
		}
		// return to auto boxing
		return holder.getKey().intValue();
	}

	public void remove(User instance) {
		throw new UnsupportedOperationException("remove");
	}

	public void update(User instance) {
		// groupId is nullable.
		Integer groupId = null;
		if (instance.getGroup() != null)
			groupId = instance.getGroup().getId();
		simple.update(UPDATE_RECORD_QUERY, instance.getPassword(), 
		                                    instance.getName(),
		                                    instance.getEmail(),
		                                    instance.getNickname(),
		                                    groupId, 
		                                    instance.getId());
		// update user's health 
		update(instance.getHealth());
	}

	public void update(Health instance) {
		healthDao.update(instance);
	}
	
	private void validate(User user) {
		if (user == null || user.getEmail() == null)
			throw new IllegalArgumentException("user: wrong instance " + user);
	}
	
	private RowMapper<User> userRowMapper = new RowMapper<User>() {
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			Health health = healthDao.find(rs.getInt("id"));
			User user = health.getUser();
			user.setPassword(rs.getString("password"))
				.setName(rs.getString("name"))
				.setNickname(rs.getString("nickname"))
				.setEmail(rs.getString("email"))
				.setJoined(rs.getTimestamp("joined"));
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
