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
package jp.lunchat.storage.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import jp.dip.komusubi.lunch.TransactionException;
import jp.dip.komusubi.lunch.module.dao.GroupDao;
import jp.dip.komusubi.lunch.module.dao.ReceiptDao;
import jp.dip.komusubi.lunch.module.dao.ReceiptLineDao;
import jp.dip.komusubi.lunch.module.dao.ShopDao;
import jp.dip.komusubi.lunch.module.dao.UserDao;
import jp.lunchat.core.model.Receipt;
import jp.lunchat.core.model.ReceiptLine;
import jp.lunchat.core.model.ReceiptLine.ReceiptLineKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;


/**
 * receipt jdbc dao.
 * @author jun.ozeki
 * @since 2012/04/09
 */
public class JdbcReceiptDao implements ReceiptDao {

    private static final Logger logger = LoggerFactory.getLogger(JdbcReceiptDao.class);
    private static final String COLUMNS = "id, orderId, userId, groupId, shopId, amount, datetime";
    private static final String INSERT_QUERY = "insert into receive ( " + COLUMNS + " ) values ( :id, :orderId, :userId, :groupId, :shopId, :amount, :datetime)";
    private static final String SELECT_RECORDS_USER_AND_DATE = "select " + COLUMNS + " from receipts where userId = :userId and date(datetime) = :datetime";
    private static final String SELECT_RECORDS_ORDER_ID = "select " + COLUMNS + " from receipts where orderId = :orderId";
    private NamedParameterJdbcTemplate template;
    @Inject private GroupDao groupDao;
    @Inject private ShopDao shopDao;
    @Inject private UserDao userDao;
    @Inject private ReceiptLineDao receiptLineDao;
    
    @Inject
    public JdbcReceiptDao(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }
    
    @Override
    public Receipt find(Integer pk) {
        throw new UnsupportedOperationException("JdbcReceiptDao#find");
    }
    
    @Override
    public List<Receipt> findByUserAndDate(Integer id, Date date) {
        List<Receipt> receipts;
        try {
            MapSqlParameterSource sqlParameter = new MapSqlParameterSource()
                                                    .addValue("userId", id)
                                                    .addValue("datetime", date);
            receipts = template.query(SELECT_RECORDS_USER_AND_DATE, sqlParameter, receiptRowMapper);
        } catch (DataAccessException e) {
            receipts = new ArrayList<>();
            logger.warn("findByUserAndDate: exception", e);
        }
        logger.info("findByUserAndDate: user id:{}, date:{}, find:{}", new Object[]{id, date, receipts.size()});
        return receipts;
    }
    
    @Override
    public List<Receipt> findByOrderId(Integer orderId) {
        List<Receipt> receipts;
        try {
            receipts = template.query(SELECT_RECORDS_ORDER_ID, new MapSqlParameterSource("orderId", orderId), receiptRowMapper);
        } catch (DataAccessException e) {
            receipts = new ArrayList<>();
            logger.warn("findByOrderId", e);
        }
        return receipts;
    }
    
    @Override
    public List<Receipt> findAll() {
        throw new UnsupportedOperationException("JdbcReceiptDao#findAll");
    }
    
    @Override
    public Integer persist(Receipt instance) {
        GeneratedKeyHolder holder = new GeneratedKeyHolder();
        try {
            template.update(INSERT_QUERY, new BeanPropertySqlParameterSource(instance), holder);
            
            int i = 1;
            for (ReceiptLine receiptLine: instance) {
                receiptLine.setPrimaryKey(new ReceiptLineKey(holder.getKey().intValue(), i++));
                receiptLineDao.persist(receiptLine);
            }
        } catch (DataAccessException e) {
            throw new TransactionException(e);
        }
        return holder.getKey().intValue();
    }
    
    @Override
    public void remove(Receipt instance) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void update(Receipt instance) {
        // TODO Auto-generated method stub
        
    }

    private MapSqlParameterSource buildSqlParameter(Receipt instance) {
        MapSqlParameterSource sqlParameter = new MapSqlParameterSource();
//                                                .addValue("id",  instance.);
        throw new UnsupportedOperationException("JdbcReceiptDao#buildSqlParameter");
//        return sqlParameter;
    }
    
    private RowMapper<Receipt> receiptRowMapper = new RowMapper<Receipt>() {
        
        @Override
        public Receipt mapRow(ResultSet rs, int rowNum) throws SQLException {
            Receipt receipt = new Receipt(rs.getInt("id"))
                                .setOrderId(rs.getInt("orderId"))
                                .setUser(userDao.find(rs.getInt("userId")))
                                .setGroup(groupDao.find(rs.getInt("groupId")))
                                .setShop(shopDao.find(rs.getString("shopId")))
                                .setAmount(rs.getInt("amount"))
                                .setDatetime(rs.getDate("datetime"));
            return receipt;
        }
    };
}
