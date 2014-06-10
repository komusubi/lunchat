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
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import jp.dip.komusubi.lunch.model.Group;
import jp.dip.komusubi.lunch.module.dao.GroupDao;
import jp.lunchat.LunchatException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class JdbcGroupDao implements GroupDao {
	
	private static final Logger logger = LoggerFactory.getLogger(JdbcGroupDao.class);
	private static final String COLUMNS = "id, code, name, place, phoneNumber";
	private static final String SELECT_RECORD_QUERY = "select " + COLUMNS + " from groups where id = ?";
	private static final String SELECT_RECORD_ALL = "select " + COLUMNS + " from groups";
	private static final String SELECT_RECORD_BY_CODE = "select " + COLUMNS + " from groups where code = ?";
	private static final String INSERT_RECORD = "insert into groups (" + COLUMNS + ") values (?, ?, ?, ?, ?)";
//	private static final String SELECT_QUERY_CONTRACTS = "select id, shopId from"
//	@Inject private ContractDao contractDao;
	private SimpleJdbcTemplate template;

	@Inject
	public JdbcGroupDao(DataSource dataSource) {
		this.template = new SimpleJdbcTemplate(dataSource);
	}
	
    @Override
    public Group find(Integer pk) {
        Group group = null;
        try {
            group = template.queryForObject(SELECT_RECORD_QUERY, groupRowMapper, pk);
        } catch (EmptyResultDataAccessException e) {
            logger.info("nof found group, pk is {}", pk);
        }
        return group;
    }

    @Override
    public Group findByCode(String code) {
        Group group = null;
        try {
            group = template.queryForObject(SELECT_RECORD_BY_CODE, groupRowMapper, code);
        } catch (EmptyResultDataAccessException e) {
            logger.info("not found group, code is {}", code);
        } 
        return group;
    }
	
	@Override
	public List<Group> findAll() {
		List<Group> list = template.query(SELECT_RECORD_ALL, groupRowMapper);
		if (list == null) {
			logger.info("findAll not find record !");
			list = Collections.emptyList();
		}
		return list;
	}

	@Override
	public Integer persist(Group instance) {
	    Group group = null;
		try {
			template.update(INSERT_RECORD, instance.getId(),
			                                instance.getCode(),
			                                instance.getName(),
			                                null,
			                                instance.getPhoneNumber());
			// FIXME LUNCHAT-18 change sql parameter source query and get auto incremented id.  
			// find group because id is auto increment. 
			group = findByCode(instance.getCode());
		} catch (DataAccessException e) {
			throw new LunchatException(e);
		}
		if (group == null)
		    throw new IllegalStateException("fail persistence, could NOT find Group: " + instance);
		// FIXME ? group's ID is immutable?
		instance.setId(group.getId());
		return group.getId();
	}

	@Override
	public void remove(Group instance) {
		throw new UnsupportedOperationException("groupDao#remove");
	}

	@Override
	public void update(Group instance) {
		throw new UnsupportedOperationException("groupDao#update");
	}
	
	private final RowMapper<Group> groupRowMapper = new RowMapper<Group>() {
		
		@Override
		public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			Group group = new Group(rs.getInt("id"))
			                    .setCode(rs.getString("code"))
			                    .setName(rs.getString("name"))
			                    .setPhoneNumber(rs.getString("phoneNumber"));
			return group;
		}
		
	};
}
