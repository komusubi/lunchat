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
package jp.dip.komusubi.lunch.module.dao.html;

import java.util.Date;
import java.util.List;

import jp.dip.komusubi.lunch.module.dao.ProductDao;
import jp.lunchat.core.model.Product;

public class HtmlProductDao implements ProductDao {

	public Product find(String pk) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Product> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	public String persist(Product instance) {
		// TODO Auto-generated method stub
		return null;
	}

	public void remove(Product instance) {
		// TODO Auto-generated method stub
		
	}

	public void update(Product instance) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Product> findByShopId(String shopId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Product> findBySalable(String shopId, Date date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Product> findByShopIdAndFinishDate(String shopId, Date finishDay) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Product> findByShopIdAndFinishDatetime(String shopId, Date finishDate) {
		// TODO Auto-generated method stub
		return null;
	}
}
