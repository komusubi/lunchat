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
package jp.dip.komusubi.lunch.wicket.page;

import jp.dip.komusubi.lunch.module.Basket;
import jp.dip.komusubi.lunch.wicket.panel.Footer;
import jp.dip.komusubi.lunch.wicket.panel.Header;

import org.apache.wicket.model.Model;

/**
 * order confirm page. 
 * @author jun.ozeki
 * @since 2011/11/19
 */
public class Confirm extends AuthorizedPage {

	private static final long serialVersionUID = 6448297553843014369L;
	private String pageTitle = getString("page.title");
	
	public Confirm(Basket basket) {
		add(new Header("header", Model.of(getDefaultHeaderBean(pageTitle))));
//		add(new OrderList("basket", basket));
		add(new Footer("footer"));
	}
}
