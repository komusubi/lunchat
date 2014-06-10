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

import java.util.Date;
import java.util.List;

import jp.dip.komusubi.lunch.Configuration;
import jp.dip.komusubi.lunch.service.Shopping;
import jp.dip.komusubi.lunch.wicket.WicketApplication;
import jp.dip.komusubi.lunch.wicket.WicketSession;
import jp.dip.komusubi.lunch.wicket.component.AuthorizedPage;
import jp.dip.komusubi.lunch.wicket.component.FormKey;
import jp.dip.komusubi.lunch.wicket.panel.Dialog;
import jp.lunchat.core.model.Order;
import jp.lunchat.core.model.Product;
import jp.lunchat.storage.Basket;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * confirm page for dialog.
 * this page expect for 'date-rel' jquery attrbute is 'dialog'.
 * @author jun.ozeki
 */
public class OrderConfirmation extends AuthorizedPage {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderConfirmation.class);
    private static final long serialVersionUID = 1L;
    private FormKey key;
    private Model<Product> model;

    /**
     * create new instance.
     * @param model
     */
    public OrderConfirmation(Model<Product> model) {
        this.model = model;
//        add(getDialog("confirm", model));
    }

    /**
     * initialize components.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(getDialog("confirm", model));
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
     * product dialog panel.
     * @param id
     * @param model
     * @return
     */
    protected Dialog<Product> getDialog(String id, final Model<Product> model) {
        return new Dialog<Product>(id, model) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onAgree() {
                try {
                    if (WicketSession.get().removeFormKey(key)) {
                        // FIXME shopping with a basket !!
                        Shopping shopping = Configuration.getInstance(Shopping.class);
                        Basket basket = shopping.getBasket(WicketSession.get().getSignedInUser());
                        basket.add(model.getObject());
                        shopping.order(basket);
                        List<Order> orders = basket.getOrders();
                        Order order = orders.get(0);
                        setResponsePage(new OrderComplete(Model.of(order)));
                    } else {
                        error(getString("double.submit"));
                        return;
                    }
                } catch (Exception e) {
                    logger.warn("exception: {}", e);
                    error("order fail. " + model.getObject());
                }
            }

            @Override
            protected void onCancel() {
                setResponsePage(WicketApplication.get().getHomePage());
            }

            @Override
            protected Label getLabel(String id) {
                return new Label(id, getString("order.to", model));
            }
        };
    }

}
