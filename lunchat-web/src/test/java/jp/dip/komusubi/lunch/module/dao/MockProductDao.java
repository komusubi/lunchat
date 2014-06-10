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
package jp.dip.komusubi.lunch.module.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jp.lunchat.core.model.Product;
import jp.lunchat.storage.dao.ProductDao;

import org.apache.commons.lang3.time.DateUtils;

public class MockProductDao implements ProductDao {

	@Override
	public Product find(String pk) {
		return null;
	}

	@Override
	public List<Product> findAll() {
		return null;
	}

	@Override
	public String persist(Product instance) {
		return null;
	}

	@Override
	public void remove(Product instance) {

	}

	@Override
	public void update(Product instance) {

	}

	@Override
	public List<Product> findByShopId(String shopId) {
		return null;
	}

	@Override
	public List<Product> findByShopIdAndFinishDate(String shopId, Date finishDate) {
		List<Product> products = new ArrayList<>();
		
		for (Product p: getProductsAll()) {
			if (p.getShop() != null && shopId.equals(p.getShop().getId())
					&& DateUtils.isSameDay(finishDate, p.getFinish())) {
				products.add(p);
			}
		}
		return products;
	}

	@Override
	public List<Product> findByShopIdAndFinishDatetime(String shopId, Date finishDate) {
		List<Product> products = new ArrayList<>();
		for (Product p: getProductsAll()) {
			if (p.getShop() != null && shopId.equals(p.getShop().getId())
					&& finishDate.before(p.getFinish())) {
				products.add(p);
			}
		}
		return products;
	}

	@Override
	public List<Product> findBySalable(String shopId, Date date) {
		return null;
	}

	protected List<Product> getProductsAll() {
		return Collections.emptyList();
	}
}
