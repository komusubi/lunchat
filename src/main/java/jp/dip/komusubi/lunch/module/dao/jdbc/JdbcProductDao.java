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
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import jp.dip.komusubi.lunch.model.Product;
import jp.dip.komusubi.lunch.module.dao.ProductDao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class JdbcProductDao implements ProductDao {

	private SimpleJdbcTemplate template;
	private static final String SELECT_COLUMNS = "id, refId, shopId, name, amount, start, finish";
	private static final String SELECT_RECORDS_SHOPID = "select " + SELECT_COLUMNS + " from product where shopId = ?";
	private static final String SELECT_RECORD_PK = "select " + SELECT_COLUMNS + " from proeuct where id = ?";
	private static final String SELECT_RECORDS_SHOPID_SALABLE = "select " + SELECT_COLUMNS 
													+ " from product where shopId = ? and start <= ? and finish >= ?";
	@Inject
	public JdbcProductDao(DataSource dataSource) {
		this.template = new SimpleJdbcTemplate(dataSource);
	}
	
	@Override
	public Product find(String pk) {
		Product product = null;
		product = template.queryForObject(SELECT_RECORD_PK, productRowMapper, pk);
		return product;
	}

	@Override
	public List<Product> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String persist(Product instance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(Product instance) {
		// TODO Auto-generated method stub
	}

	@Override
	public void update(Product instance) {
		// TODO Auto-generated method stub
	}

	@Override
	public List<Product> findByShopId(String shopId) {
		List<Product> list;
		try {
			list = template.query(SELECT_RECORDS_SHOPID, productRowMapper, shopId);
		} catch (EmptyResultDataAccessException e) {
			list = Collections.emptyList();
		}
		return list;
	}
	
	@Override
	public List<Product> findBySalable(String shopId, Date date) {
		List<Product> list;
		try {
			Timestamp datetime = new Timestamp(date.getTime());
			list = template.query(SELECT_RECORDS_SHOPID_SALABLE, productRowMapper, shopId, datetime, datetime);
		} catch (EmptyResultDataAccessException e) {
			list = Collections.emptyList();
		}
		return list;
	}
	
	private static RowMapper<Product> productRowMapper = new RowMapper<Product>() {
		@Override
		public Product mapRow(ResultSet rs, int rowNum) throws SQLException {

			Product product = new Product(rs.getString("id"))
											.setRefId(rs.getString("refId"))
											.setShopId(rs.getString("shopId"))
											.setName(rs.getString("name"))
											.setAmount(rs.getInt("amount"))
											.setStart(rs.getDate("start"))
											.setFinish(rs.getDate("finish"));
											
			return product;
		}
	};

}
