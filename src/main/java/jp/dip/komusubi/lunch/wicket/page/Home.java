/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package jp.dip.komusubi.lunch.wicket.page;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jp.dip.komusubi.lunch.Configuration;
import jp.dip.komusubi.lunch.model.Group;
import jp.dip.komusubi.lunch.model.Order;
import jp.dip.komusubi.lunch.model.Product;
import jp.dip.komusubi.lunch.model.User;
import jp.dip.komusubi.lunch.module.Basket;
import jp.dip.komusubi.lunch.service.AccountService;
import jp.dip.komusubi.lunch.wicket.WicketApplication;
import jp.dip.komusubi.lunch.wicket.WicketSession;
import jp.dip.komusubi.lunch.wicket.component.ApplicationFrame;
import jp.dip.komusubi.lunch.wicket.component.AuthenticatedLabel;
import jp.dip.komusubi.lunch.wicket.component.FormKey;
import jp.dip.komusubi.lunch.wicket.panel.ChoiceLunch;
import jp.dip.komusubi.lunch.wicket.panel.GroupList;
import jp.dip.komusubi.lunch.wicket.panel.GroupRegistry;
import jp.dip.komusubi.lunch.wicket.panel.OrderLines;
import jp.dip.komusubi.lunch.wicket.panel.UserOrderLines;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.komusubi.common.util.Resolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * home page.
 * @author jun.ozeki
 * @since 2011/12/19
 */
public class Home extends ApplicationFrame {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(Home.class);
    private User user = WicketSession.get().getSignedInUser();
    @SuppressWarnings("unused") private String username = user == null ? "" : user.getName();
    @Inject @Named("calendar") private Resolver<Calendar> resolver;
    private FormKey key;
    private List<Order> orders;

    /**
     * create new instance.
     */
    public Home() {
    }

    /**
     * initialize components.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(getChoiceLunch("select.menu"));
        add(getGroupList("group.list"));
        add(getGroupRegistry("group.registry"));
        add(getOrderLines("order.list"));
        add(new AuthenticatedLabel("greeting", getLocalizer().getString("greeting", this, new Model<Home>(this))));
        // for duble submit key
        this.key = new FormKey(getPageId(), getId(), new Date());
    }

    /**
     * configure compoents.
     */
    @Override
    protected void onConfigure() {
        super.onConfigure();
        WicketSession.get().addFormKey(key);
    }

    /**
     * has joined any groups ?
     * @return
     */
    protected boolean isJoinedGroup() {
        if (WicketSession.get().isSignedIn()) {
            User user = WicketSession.get().getSignedInUser();
            return user.getGroup() != null ? true : false;
        }
        return false;
    }

//    protected boolean hasOrdered() {
//        return getOrdered().size() > 0 ? true : false;
//    }
    /**
     * get ordred products.
     * @return
     */
    protected List<Order> getOrdered() {
        if (orders != null)
            return orders;
        if (!isJoinedGroup()) {
            return orders = Collections.emptyList();
        }
        User user = WicketSession.get().getSignedInUser();
        AccountService account = Configuration.getInstance(AccountService.class);
        return orders = account.getOrderHistory(user, resolver.resolve().getTime());
    }

    /**
     * create choice lunch panel.
     * @param id wicket id
     * @return ChoiceLunch panel.
     */
    protected ChoiceLunch getChoiceLunch(String id) {
        return new ChoiceLunch(id) {

            private static final long serialVersionUID = -8829593828769576050L;

            /**
             * configure components.
             */
            @Override
            protected void onConfigure() {
                // visible in login and join a group or before login or does not order yet..
                boolean visible = true;
                if (WicketSession.get().isSignedIn())
                    visible = isJoinedGroup();
                if (getOrdered().size() > 0)
                    visible = false;

                setVisibilityAllowed(visible);
            }

            /**
             * choice product event.
             */
            @Override
            protected void onChoiceProduct(Product product) {
                // validate double submit
                if (!WicketSession.get().removeFormKey(key)) {
                    logger.info("double submit ChoiceLunch#onChoiceProduct");
                    return;
                }
                WicketSession session = WicketSession.get();
                // did not login or not joined any groups, hold product and back to login page.
                if (!session.isSignedIn() || session.getSignedInUser().getGroup() == null) {
                    Basket basket = session.getBasket();
                    basket.add(product);
                    // need to dirty() ?
                    session.dirty();
                    setResponsePage(new OrderConfirmation(Model.of(product)));
//                    setResponsePage(SecretPage.class);
                } else {
                    setResponsePage(new OrderConfirmation(Model.of(product)));
                }
            }

        };
    }

    /**
     * create group registry panel.
     * @param id wicket id
     * @return group registry panel.
     */
    protected GroupRegistry getGroupRegistry(String id) {
        return new GroupRegistry(id) {
            private static final long serialVersionUID = -3291096546275717623L;

            @Override
            protected void onConfigure() {
                boolean visible = false;
                if (WicketSession.get().isSignedIn())
                    visible = !isJoinedGroup();

                setVisibilityAllowed(visible);
            }

            @Override
            protected void onRegister() {
                setResponsePage(WicketApplication.get().getHomePage());
            }
        };
    }

    /**
     * create group list view panel.
     * @param id wicket id
     * @return group list panel.
     */
    protected GroupList getGroupList(String id) {
        return new GroupList(id) {

            private static final long serialVersionUID = 5252671274395709375L;

            @Override
            protected void onConfigure() {
                boolean visible = false;
                if (WicketSession.get().isSignedIn())
                    visible = !isJoinedGroup();

                setVisibilityAllowed(visible);
            }

            @Override
            protected void onSelectedGroup(Group group) {
                setResponsePage(new Member(group));
            }
        };
    }

    /**
     * create order line panel.
     * @param id wicket id.
     * @return order lines panel.
     */
    protected OrderLines getOrderLines(String id) {
        List<Order> orders = getOrdered();
        Order order;
        if (orders.size() > 0)
            order = orders.get(0);
        else
            order = new Order();

        return new UserOrderLines(id, Model.of(order)) {

            private static final long serialVersionUID = -8946724145728062975L;

            @SuppressWarnings("unchecked")
            @Override
            protected void onConfigure() {
                IModel<Order> model = (IModel<Order>) getDefaultModel();
                setVisibilityAllowed(model.getObject().getOrderLines().size() > 0);
            }

            @Override
            protected void onEat() {
                if (WicketSession.get().removeFormKey(key)) {
                    //
                } else {
                    logger.info("double submit on OrderLines#onEating");
                }
            }

            @Override
            protected void onFinish() {
                if (WicketSession.get().removeFormKey(key)) {
//                    AccountService account = Configuration.getInstance(AccountService.class);
//                    account.
                } else {
                    logger.info("double submit on OrderLines#onFinished");
                }
            }
        };
    }
}
