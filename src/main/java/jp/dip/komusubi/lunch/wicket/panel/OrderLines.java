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
import jp.dip.komusubi.lunch.model.Order;
import jp.dip.komusubi.lunch.model.OrderLine;
import jp.dip.komusubi.lunch.model.User;
import jp.dip.komusubi.lunch.service.AccountService;
import jp.dip.komusubi.lunch.wicket.WicketApplication;
import jp.dip.komusubi.lunch.wicket.WicketSession;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
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
    private static final long serialVersionUID = 2352788625691496266L;
    
    /**
     * @param id
     * @param model
     */
    public OrderLines(String id, IModel<Order> model) {
        super(id, model);
        add(getOrderLineViews("order.line", new PropertyModel<List<OrderLine>>(model, "orderLines")));
        add(new Label("total.amount", new PropertyModel<String>(model, "amount")));
        add(getEatLink("eat"));
        add(getFinishedLink("finish"));
        add(getCancelLink("cancel"));
    }

    public OrderLines(String id) {
        this(id, Model.of(getTodayOrder()));
    }
            
    @Override
    protected void onConfigure() {
        if (!WicketSession.get().isSignedIn()) {
            logger.info("access {} but not login.", new Url());
            setResponsePage(WicketApplication.get().getHomePage());
            return;
        }
        User user = WicketSession.get().getLoggedInUser();
        if (user.getGroup() == null) {
            setResponsePage(WicketApplication.get().getHomePage());
            return;
        }
        IModel<Order> model = (IModel<Order>) getDefaultModel();
        model.getObject();
        
    }
    
    private static Order getTodayOrder() {
        if (!WicketSession.get().isSignedIn())
            return new Order();
        User user = WicketSession.get().getLoggedInUser();
        return getTodayOrder(user);
    }
    
    private static Order getTodayOrder(User user) {
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
    
    protected ListView<OrderLine> getOrderLineViews(String id, PropertyModel<List<OrderLine>> model) {

        ListView<OrderLine> listView = new ListView<OrderLine>(id, model) {

            private static final long serialVersionUID = 8658283409782022862L;

            @Override
            protected void populateItem(ListItem<OrderLine> item) {
                OrderLine orderLine = item.getModelObject();
                item.add(new Label("name", orderLine.getProduct().getName()));
                StringBuilder builder = new StringBuilder();
                builder.append(orderLine.getQuantity()).append("個  ")
                        .append(orderLine.getAmount()).append("円");
                item.add(new Label("amount", builder.toString()));
            }
        };
        return listView;
    }
    
    private Link<Void> getEatLink(String id) {
        return new Link<Void>(id) {

            private static final long serialVersionUID = -5686997586137367253L;
            
            @Override
            public void onClick() {
                onEat();
                setEnabled(false);
            }
        };
    }
    
    private Link<Void> getFinishedLink(String id) {
        return new Link<Void>(id) {

            private static final long serialVersionUID = 5285883179074101772L;

            @Override
            public void onClick() {
                onFinish();
                setEnabled(false);
            }
        };
    }
    
//    @Inject @Named("date") private Resolver<Date> resolver = Configuration.getInstance(Resolver.class);
    
    protected Link<Void> getCancelLink(String id) {
        return new Link<Void>(id) {

            private static final long serialVersionUID = 4670287317434201760L;

            @Override
            public void onClick() {
                onCancel();
                setEnabled(false);
            }
            
            @Override
            protected void onConfigure() {
                User user = WicketSession.get().getLoggedInUser();
                boolean visible = false;
//                if (user.getGroup().getLastOrder().before(resolver.resolve()))
//                    visible = true;
//                setVisibilityAllowed(visible);
            }

        };
    }
    
    protected void onEat() {
        logger.info("on eating !");
    }
        
    protected void onFinish() {
        logger.info("on finish !");
    }

    protected void onCancel() {
        logger.info("on cancel !");
    }
}
