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
package jp.dip.komusubi.lunch.module.dao.jdo;

import java.util.List;

import jp.dip.komusubi.lunch.model.Health;
import jp.dip.komusubi.lunch.model.User;
import jp.dip.komusubi.lunch.module.dao.UserDao;

public class JdoUserDao implements UserDao {

	public User find(String pk) {
		return null;
	}

	public List<User> findAll() {

		return null;
	}

	public String persist(User instance) {

		return null;
	}

	public void remove(User instance) {

		
	}

	public void update(User instance) {
		
	}

	@Override
	public void update(Health instance) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public User findByEmail(String email) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> findByGroupId(String groupId) {
		// TODO Auto-generated method stub
		return null;
	}

}
