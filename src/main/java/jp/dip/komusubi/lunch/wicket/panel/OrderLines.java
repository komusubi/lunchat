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

import java.util.Arrays;
import java.util.List;

import jp.dip.komusubi.lunch.model.Order;
import jp.dip.komusubi.lunch.model.Product;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * order lines.
 * @author jun.ozeki
 * @since 2012/03/10
 */
public class OrderLines extends Panel {
    private static final Logger logger = LoggerFactory.getLogger(OrderLines.class);
    private static final long serialVersionUID = 2352788625691496266L;

    /**
     * @param id
     * @param model
     */
    public OrderLines(String id, IModel<Product> model) {
        super(id);
        add(getProductViews("order.line", Arrays.asList(model.getObject())));
        add(getEating("eat"));
        add(getFinished("finish"));
    }

    // change after order view
    public OrderLines(String id, Order order) {
        super(id);
        throw new UnsupportedOperationException("not supported.");
    }
        
    /**
     * @param model
     * @return
     */
    protected ListView<Product> getProductViews(String id, final List<Product> products) {
        ListView<Product> listView = new ListView<Product>(id, products) {

            private static final long serialVersionUID = 650380468035641103L;

            @Override
            protected void populateItem(ListItem<Product> item) {
                item.setDefaultModel(new CompoundPropertyModel<Product>(item.getModelObject()));
                item.add(new Label("name"));
                item.add(new Label("amount"));
            }
        };
        return listView;
    }

    private Link<Void> getEating(String id) {
        return new Link<Void>(id) {

            private static final long serialVersionUID = -5686997586137367253L;
            
            @Override
            public void onClick() {
                onEating();
                setEnabled(false);
            }
        };
    }
    
    private Link<Void> getFinished(String id) {
        return new Link<Void>(id) {

            private static final long serialVersionUID = 5285883179074101772L;

            @Override
            public void onClick() {
                onFinished();
                setEnabled(false);
            }
            
        };
    }
    
    protected void onEating() {
        logger.info("on eating !");
    }
    
    protected void onFinished() {
        logger.info("on finished !");
    }

}
