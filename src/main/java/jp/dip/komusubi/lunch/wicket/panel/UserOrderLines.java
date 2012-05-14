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

import jp.dip.komusubi.lunch.Configuration;
import jp.dip.komusubi.lunch.model.Order;
import jp.dip.komusubi.lunch.model.Receipt;
import jp.dip.komusubi.lunch.model.User;
import jp.dip.komusubi.lunch.service.AccountService;
import jp.dip.komusubi.lunch.wicket.WicketSession;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jun.ozeki
 * @since 2012/04/22
 */
public class UserOrderLines extends OrderLines {

    private static final long serialVersionUID = 1827987171914310539L;
    private static final Logger logger = LoggerFactory.getLogger(UserOrderLines.class);
        
    public UserOrderLines(String id, IModel<Order> model) {
        super(id, model);
        add(getEatLink("eat"));
        add(getFinishedLink("finish"));
        add(getCancelLink("cancel"));
     }

    private Link<Void> getEatLink(String id) {
        
        return new Link<Void>(id) {

            private static final long serialVersionUID = -5686997586137367253L;
          
            @Override
            public void onConfigure() {
                AccountService account = Configuration.getInstance(AccountService.class);
                boolean visible = true;
                List<Order> orders = account.getOrderHistory(WicketSession.get().getSignedInUser());
                for (Order order: orders) {
                    List<Receipt> receipts = account.getReceiptsByOrderId(order.getId());
                }
//                    visible = true;
//                if (account.hasFinished(WicketSession.get().getSignedInUser()))
//                    setVisibilityAllowed(allowed)
            }
            
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
                // TODO did not order of group and receiving not yet
                boolean visible = false;
                AccountService account = Configuration.getInstance(AccountService.class);
                Order order = (Order) getDefaultModel();
                if (order == null)
                    return;
                List<Receipt> receipts = account.getReceiptsByOrderId(order.getId());
                // found receipt and it must not cancel, so invisible.
                if (receipts.size() != 0) {
                    visible = false;
                } else {
                    User user = WicketSession.get().getSignedInUser();
//                    account.g
                }
//                account.
//                if (user.getGroup().getLastOrder().before(resolver.resolve()))
//                    visible = true;
               setVisibilityAllowed(visible);
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
