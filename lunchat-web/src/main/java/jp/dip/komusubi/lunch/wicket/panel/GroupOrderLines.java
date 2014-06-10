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

import java.util.List;

import jp.lunchat.core.model.Order;
import jp.lunchat.core.model.OrderLine;
import jp.lunchat.web.service.AccountService;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author jun.ozeki
 * @since 2012/04/22
 */
public class GroupOrderLines extends Panel {

    private static final long serialVersionUID = -4269855275734939591L;
    private static final Logger logger = LoggerFactory.getLogger(GroupOrderLines.class); 
    @Inject private AccountService account;
    
    public GroupOrderLines(String id, IModel<String> model) {
        super(id, model);
        for (Order order: account.getGroupOrderHistory(model.getObject()))
            add(new OrderListFragment("order.by.shop", Model.of(order)));
        // TODO remove link after configured page. 
        add(getOrderLink("order"));
        add(new Label("notice", "notice"));
    }
    
//    public GroupOrderLines(String id, IModel<Group> group) {
//        super(id);
//        for (Order order: account.getOrderHistory(group.getObject())) 
//            add(new OrderListFragment("order.by.shop", Model.of(order)));
//        add(getOrderLink("order"));
//        add(new Label("notice", "notice"));
//    }
    
    public GroupOrderLines(String id, List<Order> orderList) {
//       super(id, model);
        super(id);
        // FIXME change argument to IModel but do NOT unpack in constructor.
        for (Order order: orderList)
           add(new OrderListFragment("order.by.shop", Model.of(order)));
        add(getOrderLink("order"));
        add(new Label("notice", "notice"));
    }

    
    // TODO change to onEvent method, it will takes near future. 
//    @Override
//    public void onEvent(IEvent<?> event) {
////        event.getP
//    }

    protected ListView<OrderLine> getOrderLineList(String id, IModel<List<OrderLine>> orderLines) {
        return new ListView<OrderLine>(id, orderLines) {

            private static final long serialVersionUID = 3748625873687238436L;

            @Override
            protected void populateItem(ListItem<OrderLine> item) {
                item.add(new Label("name"));
                item.add(new Label("quantity"));
                item.add(new Label("amount"));
            }
            @Override
            protected IModel<OrderLine> getListItemModel(IModel<? extends List<OrderLine>> listViewModel, int index) {
                OrderLine orderLine = listViewModel.getObject().get(index);
                return new CompoundPropertyModel<OrderLine>(orderLine);
            }
        };
    }
//    protected ListView<Order> getOrderList(String id, IModel<List<Order>> model) {
//        return new ListView<Order>(id, model) {
//
//            private static final long serialVersionUID = 8468557919631378672L;
//            @Override
//            protected void populateItem(ListItem<Order> item) {
//                Order order = item.getModelObject();
//                item.add(getOrderLineList("products", order.getOrderLines()));
//                item.add(new Label("shop.name", new PropertyModel<Order>(order, "shop.name")));
//                item.add(new Label("total.quantity", new PropertyModel<Order>(order, "quantity")));
//                item.add(new Label("total.amount", new PropertyModel<Order>(order, "amount")));
//            }
//        };
//    }

    protected class OrderListFragment extends Fragment {

        private static final long serialVersionUID = 1105523442621699293L;
        
        public OrderListFragment(String id, IModel<Order> model) {
            super(id, "orderline.fragment", GroupOrderLines.this, model);
            add(new Label("shop.name", new PropertyModel<Order>(model, "shop.name")));
            add(new Label("total.quantity", new PropertyModel<Order>(model, "quantity")));
            add(new Label("total.amount", new PropertyModel<Order>(model, "amount")));
            add(getOrderLineList("products", new PropertyModel<List<OrderLine>>(model, "orderLine")));
            add(getOrderShopLink("order.shop"));
        }
        
        public Link<Void> getOrderShopLink(String id) {
            return new Link<Void>(id) {

                private static final long serialVersionUID = 1687658796990624730L;

                @Override
                public void onClick() {
                    logger.info("on click order shop");
                }                
                
                @Override
                protected void onConfigure() {
                    // TODO set visible when just only a order history or before order list.
                    setVisibilityAllowed(true);
                    super.onConfigure();
                }
                
            };
        }
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
