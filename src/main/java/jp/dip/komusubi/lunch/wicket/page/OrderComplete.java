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

import java.util.Date;

import jp.dip.komusubi.lunch.model.Product;
import jp.dip.komusubi.lunch.wicket.WicketSession;
import jp.dip.komusubi.lunch.wicket.component.FormKey;
import jp.dip.komusubi.lunch.wicket.panel.OrderLines;

import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderComplete extends AuthorizedPage {
    private static final Logger logger = LoggerFactory.getLogger(OrderComplete.class);
	private static final long serialVersionUID = -6096514197924442740L;
    private FormKey key;
//	@Inject
//	private transient OrderDao orderDao;
//	private transient Shopping shopping;
	
	public OrderComplete(Model<Product> model) {
	    add(getOrderLines("order.lines", model));
	}

//	public OrderComplete(Model<List<OrderLine>> orderModel) {
	    
//	}
    @Override
	public void onInitialize() {
	    super.onInitialize();
        this.key = new FormKey(getPageId(), getId(), new Date());
	}
	
	@Override
	public void onConfigure() {
	    super.onConfigure();
	    WicketSession.get().addFormKey(key);
	}
	
	/**
	 * @param string
	 * @param model
	 * @return
	 */
	private OrderLines getOrderLines(String id, Model<Product> model) {
	    return new OrderLines(id, model) {
	        private static final long serialVersionUID = 7930054187191919478L;

            @Override
	        protected void onEating() {
                if (WicketSession.get().removeFormKey(key)) {
                    logger.info("いただきまーす");
                } else {
                    logger.info("double submit onEating()");
                }
	        }
	        
	        @Override
	        protected void onFinished() {
	            if (WicketSession.get().removeFormKey(key)) {
	                logger.info("ごちそうさま");
	            } else {
	                logger.info("double submit onFinished()");
	            }
	            
	        }
	    };
	}
	
	
}
