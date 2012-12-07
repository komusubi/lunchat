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

import jp.dip.komusubi.lunch.model.Order;
import jp.dip.komusubi.lunch.wicket.WicketSession;
import jp.dip.komusubi.lunch.wicket.component.AuthorizedFrame;
import jp.dip.komusubi.lunch.wicket.component.FormKey;
import jp.dip.komusubi.lunch.wicket.panel.OrderLines;
import jp.dip.komusubi.lunch.wicket.panel.UserOrderLines;

import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * order complete.
 * @author jun.ozeki
 */
public class OrderComplete extends AuthorizedFrame {
    private static final Logger logger = LoggerFactory.getLogger(OrderComplete.class);
	private static final long serialVersionUID = -6096514197924442740L;
    private FormKey key;

    /**
     * create new instance.
     * @param model
     */
    public OrderComplete(IModel<Order> model) {
	    add(getOrderLines("order.lines", model));
	}
    
    /**
     * initialize components.
     */
    @Override
	protected void onInitialize() {
	    super.onInitialize();
        this.key = new FormKey(getPageId(), getId(), new Date());
	}
    
	/**
	 * configure components.
	 */
	@Override
	protected void onConfigure() {
	    super.onConfigure();
	    WicketSession.get().addFormKey(key);
	}
	
	/**
	 * @param id  wicket id.
	 * @param model Order. 
	 * @return
	 */
	protected OrderLines getOrderLines(String id, IModel<Order> model) {
	    return new UserOrderLines(id, model);
	    /*
	    return new UserOrderLines(id, model) {
	        private static final long serialVersionUID = 7930054187191919478L;

            @Override
	        protected void onEat() {
                if (WicketSession.get().removeFormKey(key)) {
                    logger.info("いただきまーす");
                } else {
                    logger.info("double submit onEat()");
                }
	        }

	        @Override
	        protected void onFinish() {
	            if (WicketSession.get().removeFormKey(key)) {
	                logger.info("ごちそうさま");
	            } else {
	                logger.info("double submit onFinish()");
	            }
	        }
	        
//	        @Override
	        protected void onCancel() {
	            if (WicketSession.get().removeFormKey(key)) {
	                logger.info("キャンセル");
//	                AccountService account = Configuration.getInstance(AccountService.class);
	                Shopping shopping = Configuration.getInstance(Shopping.class);
	                Order order = ((IModel<Order>) getDefaultModel()).getObject(); 
//	                shopping.cancel(order);
	            } else {
	                logger.info("double submit onCancel()");
	            }
	        }
	    };
	     */
	}
	
	
}
