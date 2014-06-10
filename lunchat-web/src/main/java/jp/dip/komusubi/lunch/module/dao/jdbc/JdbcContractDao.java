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
package jp.dip.komusubi.lunch.module.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import jp.dip.komusubi.lunch.model.Contract;
import jp.dip.komusubi.lunch.module.dao.ContractDao;
import jp.dip.komusubi.lunch.module.dao.GroupDao;
import jp.dip.komusubi.lunch.module.dao.ShopDao;
import jp.lunchat.LunchatException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class JdbcContractDao implements ContractDao {

	private static final Logger logger = LoggerFactory.getLogger(JdbcContractDao.class);
	private static final String COLUMNS = "id, groupId, shopId, contracted";
	private static final String SELECT_RECORD_QUERY = "select " + COLUMNS + " from contracts where = ?";
	private static final String INSERT_RECORD_QUERY = "insert into contracts (groupId, shopId, contracted) values"
	        + " ((select id from groups where code = ?), ?, ?)";
	private static final String DELETE_RECORD_QUERY = "delete from contracts where id = ?";
	private static final String UPDATE_RECORD_QUERY = 
			"update contracts set groupId = ?, shopId = ?, contracted = ? where id = ?";
	private static final String SELECT_RECORD_GROUPID_AND_SHOPID = "select " + COLUMNS + " from contracts where groupId = ? and shopId = ?"; 
	private static final String SELECT_RECORD_GROUPCODE_AND_SHOPID = 
			"select " + COLUMNS + " from contracts where groupId = (select id from groups where code = ?) and shopId = ?";
	private static final String SELECT_QUERY_GROUPID = "select " + COLUMNS + " from contracts where groupId = ?";
	private static final String SELECT_QUERY_SHOPID = "select " + COLUMNS + " from contracts where shopId = ?";
	@Inject	private ShopDao shopDao;
	@Inject private GroupDao groupDao;
	private SimpleJdbcTemplate template;

	@Inject
	public JdbcContractDao(DataSource dataSource) {
		this.template = new SimpleJdbcTemplate(dataSource);
	}

	@Override
	public Contract find(Integer pk) {
		Contract contract = null;
		try {
			contract = template.queryForObject(SELECT_RECORD_QUERY, contractRowMapper, pk);
		} catch (EmptyResultDataAccessException e) {
			logger.info("not found contract, pk is :{}", pk);
		}
		return contract;
	}

	@Override
	public List<Contract> findAll() {
		throw new UnsupportedOperationException("#findAll");
	}

    @Override
    public List<Contract> findByGroupId(Integer groupId) {
//	      Set<Contract> set = new HashSet<>();
        List<Contract> list;
        try {
//	          set.addAll(template.query(SELECT_QUERY_GROUPID, contractRowMapper, groupId));
            list = template.query(SELECT_QUERY_GROUPID, contractRowMapper, groupId);
        } catch (EmptyResultDataAccessException e) {
            logger.info("not found contract, groupId:{}", groupId);
            list = Collections.emptyList();
        }
        return list;
    }

    @Override
    public List<Contract> findByShopId(String shopId) {
//	      Set<Contract> set = new HashSet<>();
        List<Contract> list;
        try {
//	          set.addAll(template.query(SELECT_QUERY_SHOPID, contractRowMapper, shopId));
            list = template.query(SELECT_QUERY_SHOPID, contractRowMapper, shopId);
        } catch (EmptyResultDataAccessException e) {
            logger.info("not found contract, shopId:{}", shopId);
            list = Collections.emptyList();
        }
        return list;
    }

    @Override
    public Contract findByGroupIdAndShopId(Integer groupId, String shopId) {
        Contract contract = null;
        try {
            contract = template.queryForObject(SELECT_RECORD_GROUPID_AND_SHOPID, contractRowMapper, groupId, shopId);
        } catch (EmptyResultDataAccessException e) {
            logger.info("not found contract arguments are groupId:{} and shopId:{}", groupId, shopId);
        }
        return contract;
    }

    @Override
    public Contract findByGroupCodeAndShopId(String groupCode, String shopId) {
        Contract contract = null;
        try {
            contract = template.queryForObject(SELECT_RECORD_GROUPCODE_AND_SHOPID, contractRowMapper, groupCode, shopId);
        } catch (EmptyResultDataAccessException e) {
            logger.info("not found contract arguments are groupCode:{}, and shopId:{}", groupCode, shopId);
        }
        return contract;
    }
	@Override
	public Integer persist(Contract instance) {
	    validate(instance);
	    Contract contract = null;
		try {
			template.update(INSERT_RECORD_QUERY, instance.getGroup().getCode(), 
										        instance.getShop().getId(), 
										        instance.getContracted());
			
			contract = findByGroupCodeAndShopId(instance.getGroup().getCode(), instance.getShop().getId());
		} catch (DataAccessException e) {
			throw new LunchatException(e);
		}
	    if (contract == null)
	        throw new IllegalStateException("file presistence, could NOT find Contract: " + instance);
		return contract.getId();
	}

	@Override
	public void remove(Contract instance) {
	    validate(instance);
		try {
			template.update(DELETE_RECORD_QUERY, instance.getId());
		} catch (DataAccessException e) {
			throw new LunchatException(e);
		}
	}

	@Override
	public void update(Contract instance) {
	    validate(instance);
		try {
			template.update(UPDATE_RECORD_QUERY, instance.getGroup().getId(), 
										instance.getShop().getId(), 
										instance.getContracted(),
										instance.getId());
		} catch (DataAccessException e) {
			throw new LunchatException(e);
		}
	}

	private void validate(Contract contract) {
	    if (contract == null || contract.getId() == null)
	        throw new IllegalArgumentException("contract: wrong instance " + contract);
	}
	
	private final RowMapper<Contract> contractRowMapper = new RowMapper<Contract>() {
		
		@Override
		public Contract mapRow(ResultSet rs, int rowNum) throws SQLException {
			Contract contract = new Contract(rs.getInt("id"))
								.setGroup(groupDao.find(rs.getInt("groupId")))
								.setShop(shopDao.find(rs.getString("shopId")))
								.setContracted(rs.getDate("contracted"));
			return contract;
		}
		
	};
	
}
