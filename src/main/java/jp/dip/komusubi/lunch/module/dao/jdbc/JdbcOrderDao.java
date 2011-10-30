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
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import jp.dip.komusubi.lunch.model.Order;
import jp.dip.komusubi.lunch.module.dao.OrderDao;
import jp.dip.komusubi.lunch.module.dao.ProductDao;
import jp.dip.komusubi.lunch.module.dao.UserDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class JdbcOrderDao implements OrderDao {
	
	private static final Logger logger = LoggerFactory.getLogger(JdbcOrderDao.class);
	private static final String INSERT_QUERY = "insert into ordered (userId, productId, quantity, amount, datetime) "
													+ "values (?, ?, ?, ?, ?)";
	private static final String SELECT_QUERY_ORDER_DATE = "select id, userId, productId, quantity, amount, datetime "
														+ "from ordered where datetime = ?";
	private SimpleJdbcTemplate template;
	@Inject
	private UserDao userDao;
	@Inject
	private ProductDao productDao;
	
	@Inject
	public JdbcOrderDao(DataSource dataSource) {
		this.template = new SimpleJdbcTemplate(dataSource);
	}
	
	public Order find(Integer pk) {
		throw new UnsupportedOperationException("find(pk)");
	}

	public List<Order> findAll() {
		throw new UnsupportedOperationException("findAll()");
	}
	
	public Integer persist(Order instance) {
		template.update(INSERT_QUERY , instance.getUser().getId(),
											instance.getProduct().getId(),
											instance.getQuantity(),
											instance.getAmount(),
											instance.getDate());
		logger.info("persisted: {}", instance);
		// return to auto boxing 
		return instance.getId();
	}

	public void remove(Order instance) {
		throw new UnsupportedOperationException("#remove not supported");		
	}

	public void update(Order instance) {
		throw new UnsupportedOperationException("#update not supported");
	}

	public List<Order> findByDate(Date date) {
		List<Order> list;
		list = template.query(SELECT_QUERY_ORDER_DATE, orderRowMap, date);
		logger.info("findByDate({}): {}", date, list.size());
		return list;
	}

	private RowMapper<Order> orderRowMap = new RowMapper<Order>() {
		
		@Override
		public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
			Order order = new Order(rs.getInt("id"))
								.setQuantity(rs.getInt("quantity"))
//								.setAmount(rs.getInt("amount"))
								.setDate(rs.getDate("datetime"))
								.setUser(userDao.find(rs.getString("userId")))
								.setProduct(productDao.find(rs.getString("productId")));
			return order;
		}
		
	};

	@Override
	public List<Order> findByUserAndDate(String userId, Date date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Order> findByUser(String userIde) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Order> findByProduct(String productId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Order> findByUserAndProductAndDate(String userId, String productId, Date date) {
		// TODO Auto-generated method stub
		return null;
	}

}
