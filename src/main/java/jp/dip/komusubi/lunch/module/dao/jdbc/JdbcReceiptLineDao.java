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

import jp.dip.komusubi.lunch.TransactionException;
import jp.dip.komusubi.lunch.model.ReceiptLine;
import jp.dip.komusubi.lunch.model.ReceiptLine.ReceiptLineKey;
import jp.dip.komusubi.lunch.module.dao.ProductDao;
import jp.dip.komusubi.lunch.module.dao.ReceiptLineDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * receipt line jdbc dao. 
 * @author jun.ozeki
 */
public class JdbcReceiptLineDao implements ReceiptLineDao {
    
    private static final Logger logger = LoggerFactory.getLogger(JdbcReceiptLineDao.class);
    private static final String COLUMNS = "receiptId, no, productId, quantity, amount, memo, datetime";
    private static final String INSERT_QUERY = "insert into receiptlines ( " + COLUMNS + " ) " +
    		"values ( :receiptId, :no, :productId, :quantity, :amount, :memo, :datetime )";
    private NamedParameterJdbcTemplate template;
    @Inject private ProductDao productDao;
    
    @Inject
    public JdbcReceiptLineDao(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }
    
    @Override
    public ReceiptLine find(ReceiptLineKey pk) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ReceiptLine> findAll() {
        throw new UnsupportedOperationException("JdbcReceiptLineDao#findAll");
    }

    @Override
    public ReceiptLineKey persist(ReceiptLine instance) {
        MapSqlParameterSource sqlParameter = buildSqlParamterSource(instance);
        try {
            template.update(INSERT_QUERY, sqlParameter);
        } catch (DataAccessException e) {
            throw new TransactionException(e);
        }
        return instance.getPrimaryKey();
    }

    @Override
    public void remove(ReceiptLine instance) {
        // TODO Auto-generated method stub

    }

    @Override
    public void update(ReceiptLine instance) {
        // TODO Auto-generated method stub

    }

    private MapSqlParameterSource buildSqlParamterSource(ReceiptLine instance) {
        MapSqlParameterSource sqlParameter = new MapSqlParameterSource()
                                                .addValue("receiptId", instance.getPrimaryKey().getReceiptId())
                                                .addValue("no", instance.getPrimaryKey().getNo())
                                                .addValue("quantity", instance.getQuantity())
                                                .addValue("amount", instance.getAmount())
                                                .addValue("memo", instance.getMemo())
                                                .addValue("datetime", instance.getDatetime());
        return sqlParameter;
    }
    
    private RowMapper<ReceiptLine> receiptLineRowMapper = new RowMapper<ReceiptLine>() {

        @Override
        public ReceiptLine mapRow(ResultSet rs, int rowNum) throws SQLException {
            ReceiptLineKey primaryKey = new ReceiptLineKey(rs.getInt("receiptId"), rs.getInt("no"));
            ReceiptLine receiptLine = new ReceiptLine(primaryKey)
                                        .setProduct(productDao.find("productId"))
                                        .setQuantity(rs.getInt("quantity"))
                                        .setAmount(rs.getInt("amount"))
                                        .setMemo(rs.getString("memo"))
                                        .setDatetime(rs.getDate("datetime"));
            return receiptLine;
        }
        
    };
}
