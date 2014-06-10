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

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import jp.dip.komusubi.lunch.LunchException;
import jp.lunchat.storage.database.Lunchat;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.SystemUtils;
import org.junit.rules.ExternalResource;

/**
 * data source resource.
 * @author jun.ozeki
 * @since 2012/01/29
 */
public class DataSourceResource extends ExternalResource {

    private BasicDataSource dataSource;
    private static final String DRIVER_CLASS_NAME = "org.apache.empire.db.h2.DBDatabaseDriverH2";
    private static final String JDBC_URL = "jdbc:log4jdbc:h2:" + SystemUtils.JAVA_IO_TMPDIR + "/lunchat";
//    private static final String JDBC_URL = "jdbc:h2:" + SystemUtils.JAVA_IO_TMPDIR + "/lunchat";
    private static final String JDBC_USER = "sa";
    private static final String JDBC_USER_PASSWORD = "";
    
    /**
     * get data source.
     * @return
     */
    public DataSource getDataSource() {
        if (dataSource != null)
            return dataSource;
        return getDataSource(JDBC_URL, JDBC_USER, JDBC_USER_PASSWORD);
    }
    
    /**
     * get data source.
     * @param url
     * @param user
     * @param pass
     * @return
     */
    public DataSource getDataSource(String url, String user, String pass) {
        dataSource = new BasicDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(pass);
        dataSource.setDefaultAutoCommit(false);
        dataSource.setMaxActive(3);
        dataSource.setMaxWait(5);
        return dataSource; 
    }

    /**
     * if does NOT exist table, create it. 
     * @see org.junit.rules.ExternalResource#before()
     */
    @Override
    protected void before() throws Exception {
        Lunchat lunchat = new Lunchat(DRIVER_CLASS_NAME);
        try (Connection con = getDataSource().getConnection()) {
            if (lunchat.existsTables(con))
                return;
        } catch (LunchException e) {
            lunchat.configure(JDBC_URL, JDBC_USER, JDBC_USER_PASSWORD);
        }
    }
    
    @Override
    protected void after() throws LunchException {
        if (dataSource != null)
            try {
                dataSource.close();
            } catch (SQLException e) {
                throw new LunchException(e);
            }
    }

}
