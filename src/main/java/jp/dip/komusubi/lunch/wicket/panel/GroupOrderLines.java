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
package jp.dip.komusubi.lunch.wicket.panel;

import jp.dip.komusubi.lunch.model.Order;

import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jun.ozeki
 * @since 2012/04/22
 */
public class GroupOrderLines extends OrderLines {

    private static final long serialVersionUID = -4269855275734939591L;
    private static final Logger logger = LoggerFactory.getLogger(GroupOrderLines.class); 
    
    public GroupOrderLines(String id, IModel<Order> model) {
        super(id, model);
        
    }

    @Override
    public void onEvent(IEvent<?> event) {
//        event.getP
    }
    
    protected Link<Void> getOrderLink(String id) {
        return new Link<Void>(id) {

            private static final long serialVersionUID = 1434117799245233939L;

            @Override
            public void onClick() {
                onOrder();
            }
        };
    }
    
    protected void onOrder() {
        logger.info("on order event !");
    }
}
