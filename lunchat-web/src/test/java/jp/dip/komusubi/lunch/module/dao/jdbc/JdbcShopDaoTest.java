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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import jp.lunchat.core.model.Shop;
import jp.lunchat.storage.dao.jdbc.JdbcShopDao;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * jdbc shop dao unit test.
 * @author jun.ozeki
 */
public class JdbcShopDaoTest {

    @ClassRule public static DataSourceResource resource = new DataSourceResource();
    private JdbcShopDao target;
    
    @Before
    public void before() {
        target = new JdbcShopDao(resource.getDataSource());
    }
    
    /**
     * find by id. 
     * @throws Exception
     */
    @Test
    public void findById() throws Exception {
        Shop shop = target.find("tamagoya");
        assertNotNull(shop);
    }
    
    /**
     * find all.
     * @throws Exception
     */
    @Test
    public void findAll() throws Exception {
        List<Shop> shops = target.findAll();
        assertEquals(3, shops.size());
    }

}
