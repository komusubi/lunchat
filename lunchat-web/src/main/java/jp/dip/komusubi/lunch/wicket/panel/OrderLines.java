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

import java.util.Date;
import java.util.List;

import jp.dip.komusubi.lunch.Configuration;
import jp.dip.komusubi.lunch.service.AccountService;
import jp.dip.komusubi.lunch.wicket.WicketApplication;
import jp.dip.komusubi.lunch.wicket.WicketSession;
import jp.lunchat.core.model.Order;
import jp.lunchat.core.model.OrderLine;
import jp.lunchat.core.model.User;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.Url;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * order lines.
 * @author jun.ozeki
 * @since 2012/03/10
 */
public class OrderLines extends Panel {

    private static final Logger logger = LoggerFactory.getLogger(OrderLines.class);
    private static final long serialVersionUID = 1L;
    private IModel<Order> model;
    
    /**
     * create new instance.
     * @param id
     * @param model
     */
    public OrderLines(String id, IModel<Order> model) {
        super(id, model);
        this.model = model;
    }

    /**
     * create new instance.
     * @param id
     */
    public OrderLines(String id) {
        this(id, Model.of(getTodayOrder()));
    }
            
    /**
     * initialize components.
     * @see org.apache.wicket.Component#onInitialize()
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(newOrderLineViews("order.line", new PropertyModel<List<OrderLine>>(model, "orderLines")));
    }
    
    /**
     * configure components.
     */
    @Override
    protected void onConfigure() {
        if (!WicketSession.get().isSignedIn()) {
            logger.info("access {} but not login.", new Url());
            setResponsePage(WicketApplication.get().getHomePage());
            return;
        }
        User user = WicketSession.get().getSignedInUser();
        if (user.getGroup() == null) {
            setResponsePage(WicketApplication.get().getHomePage());
            return;
        }
//        IModel<Order> model = (IModel<Order>) getDefaultModel();
//        model.getObject();
        
    }
    
    /**
     * get order from storage.
     * @return
     */
    protected static Order getTodayOrder() {
        if (!WicketSession.get().isSignedIn())
            return new Order();
        User user = WicketSession.get().getSignedInUser();
        return getTodayOrder(user);
    }
    
    /**
     * get order from storage.
     * @param user
     * @return
     */
    protected static Order getTodayOrder(User user) {
        if (user == null)
            return new Order();
        AccountService account = Configuration.getInstance(AccountService.class);
        List<Order> orders = account.getOrderHistory(user, new Date());
        Order order;
        if (orders.size() > 0)
            order = orders.get(0);
        else
            order = new Order();    

        return order;
    }
    
    /**
     * create order lines view.
     * @param id
     * @param model
     * @return
     */
    protected ListView<OrderLine> newOrderLineViews(String id, PropertyModel<List<OrderLine>> model) {

        ListView<OrderLine> listView = new ListView<OrderLine>(id, model) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<OrderLine> item) {
                OrderLine orderLine = item.getModelObject();
                item.add(new Label("name", new PropertyModel<>(orderLine, "product.name")));
                item.add(new Label("quantity", new PropertyModel<>(orderLine, "quantity")));
                item.add(new Label("amount", new PropertyModel<>(orderLine, "amount")));
            }
        };
        return listView;
    }
    
}
