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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import jp.dip.komusubi.lunch.model.Product;
import jp.dip.komusubi.lunch.model.Shop;
import jp.dip.komusubi.lunch.module.dao.ContractDao;
import jp.dip.komusubi.lunch.module.dao.ShopDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class JdbcShopDao implements ShopDao {

	private static final Logger logger = LoggerFactory.getLogger(JdbcShopDao.class);
	private static final String COLUMNS = "id, name, geoId, url, phoneNumber, lastOrder";
	private static final String SELECT_RECORD_QUERY = "select " + COLUMNS + " from shops where id = ?";
	private static final String SELECT_ALL_RECORD = "select " + COLUMNS + " from shops";
	private SimpleJdbcTemplate template;
	@Inject private ContractDao contractDao;

	@Inject
	public JdbcShopDao(DataSource dataSource) {
		template = new SimpleJdbcTemplate(dataSource);
	}

	@Override
	public Shop find(String pk) {
		Shop shop = null;
		try {
			template.queryForObject(SELECT_RECORD_QUERY, shopRowMapper, pk);
		} catch (EmptyResultDataAccessException e) {
			logger.info("not found shop is {}", pk);
		}
		return shop;
	}

	@Override
	public List<Shop> findAll() {
		List<Shop> shops = template.query(SELECT_ALL_RECORD, shopRowMapper);
		if (shops == null)
			shops = Collections.emptyList();
		return shops;
	}

	@Override
	public String persist(Shop instance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(Shop instance) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Shop instance) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Product> findBySalable(String shopId, Date date) {
		return null;
	}

	private final RowMapper<Shop> shopRowMapper = new RowMapper<Shop>() {
		
		@Override
		public Shop mapRow(ResultSet rs, int rowNum) throws SQLException {
			Shop shop = new Shop(rs.getString("id"))
								.setName(rs.getString("name"))
								.setPhoneNumber(rs.getString("phoneNumber"))
								.setUrl(rs.getString("url"))
//								.setLastOrder(rs.getTime("lastOrder"))
								.setLastOrder(JdbcDateConverter.toCurrentDate(rs.getTime("lastOrder")))
								.setContracts(contractDao.findByShopId(rs.getString("id")));
			return shop;
		}
	};
}
