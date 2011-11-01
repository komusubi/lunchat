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
package jp.dip.komusubi.lunch.module.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import jp.dip.komusubi.lunch.model.Health;
import jp.dip.komusubi.lunch.module.dao.HealthDao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class JdbcHealthDao implements HealthDao {
	
//	private static final Logger logger = LoggerFactory.getLogger(JdbcHealthDao.class);
	private static final String COLUMNS = "userId, login, lastLogin, loginFail, active"; 
	private static final String SELECT_QUERY_PK = "select " + COLUMNS + " from health where userId = ?";
	private static final String UPDATE_QUERY = "update health set login = ?, lastLogin = ?, loginFail = ? where userId = ?";
	private static final String INSERT_QUERY = "insert into health (" + COLUMNS + ") values (?, ?, ?, ?, ?)";
	private SimpleJdbcTemplate template;

	@Inject
	public JdbcHealthDao(DataSource dataSource) {
		template = new SimpleJdbcTemplate(dataSource);
	}
	
	@Override
	public Health find(String pk) {
		Health health = template.queryForObject(SELECT_QUERY_PK, healthRowMapper, pk);
		return health;
	}

	@Override
	public List<Health> findAll() {
		throw new UnsupportedOperationException("findAll not supported.");
	}

	@Override
	public String persist(Health instance) {
		template.update(INSERT_QUERY, instance.getUserId(),
											instance.getLogin(),
											instance.getLastLogin(),
											instance.getLoginFail(),
											instance.isActive());
		return instance.getUserId();									
	}

	@Override
	public void remove(Health instance) {
		throw new UnsupportedOperationException("remove not supported.");
	}

	@Override
	public void update(Health instance) {
		template.update(UPDATE_QUERY, instance.getLogin(),
											instance.getLastLogin(),
											instance.getLoginFail(),
											instance.getUserId());
	}

	private static RowMapper<Health> healthRowMapper = new RowMapper<Health>() {

		@Override
		public Health mapRow(ResultSet rs, int rowNum) throws SQLException {
			Health health = new Health(rs.getString("userId"))
									.setLogin(rs.getInt("login"))
									.setLoginFail(rs.getInt("loginFail"))
									.setLastLogin(rs.getDate("lastLogin"))
									.setActive(rs.getBoolean("active"));
			return health;
		}
		
	};
}