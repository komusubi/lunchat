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
package jp.dip.komusubi.lunch.wicket.panel;

import jp.lunchat.core.model.Order;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * user's order lines view.
 * @author jun.ozeki
 * @since 2012/04/22
 */
public class UserImpression extends Panel {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(UserImpression.class);
    private String impressionText; // for property model

    /**
     * create new intance.
     * @param id
     * @param model
     */
    public UserImpression(String id, IModel<Order> model) {
        super(id, model);
    }

    /**
     * create new instance.
     * @param id
     */
    public UserImpression(String id) {
        super(id);
    }

    /**
     * initialize components.
     * @see org.apache.wicket.Component#onInitialize()
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new ImpressionForm("impressions"));
    }

    /**
     * user impression form.
     * @author jun.ozeki
     */
    protected class ImpressionForm extends Form<Void> {

        private static final long serialVersionUID = 1L;

        /**
         * create new instance.
         * @param id
         */
        public ImpressionForm(String id) {
            super(id);
        }

        /**
         * initialize components.
         * @see org.apache.wicket.Component#onInitialize()
         */
        @Override
        protected void onInitialize() {
            super.onInitialize();
            add(newImpressionInput("impression"));
            add(newFinishButton("finish"));
            add(newCancelButton("cancel"));
        }

        /**
         * create finish button.
         * @param id
         * @return
         */
        protected Button newFinishButton(String id) {
            return new Button(id) {

                private static final long serialVersionUID = 1L;

                /**
                 * event of finish button.
                 * @see org.apache.wicket.markup.html.form.Button#onSubmit()
                 */
                @Override
                public void onSubmit() {
                    onFinish();
                }

            };
        }

        /**
         * create cancel button.
         * @param id
         * @return
         */
        protected Button newCancelButton(String id) {
            return new Button(id) {

                private static final long serialVersionUID = 1L;

                /**
                 * event of cancel button.
                 * @see org.apache.wicket.markup.html.form.Button#onSubmit()
                 */
                @Override
                public void onSubmit() {
                    onCancel();
                }

            };
        }

        /**
         * create text area for impression.
         * @param id
         * @return
         */
        protected TextArea<String> newImpressionInput(String id) {
            return new TextArea<String>(id, new PropertyModel<String>(UserImpression.this, "impressionText"));
        }
    }

//    protected Link<Void> newCancelLink(String id) {
//        return new Link<Void>(id) {
//
//            private static final long serialVersionUID = 1L;
//
//            @Override
//            public void onClick() {
//                onCancel();
//                setEnabled(false);
//            }
//
//            @Override
//            protected void onConfigure() {
//                // TODO did not order of group and receiving not yet
//                boolean visible = false;
//                Order order = (Order) getDefaultModel();
//                if (order == null)
//                    return;
//                List<Receipt> receipts = account.getReceiptsByOrderId(order.getId());
//                // found receipt and it must not cancel, so invisible.
//                if (receipts.size() != 0) {
//                    visible = false;
//                } else {
//                    User user = WicketSession.get().getSignedInUser();
////                    account.g
//                }
////                account.
////                if (user.getGroup().getLastOrder().before(resolver.resolve()))
////                    visible = true;
//                setVisibilityAllowed(visible);
//            }
//        };
//    }

    /*
     * event of eat.
     */
//    protected void onEat() {
//        logger.info("on eating !");
//    }

    /**
     * event of finish.
     */
    protected void onFinish() {
        logger.info("on finish !");
    }

    /**
     * event of cancel.
     */
    protected void onCancel() {
        logger.info("on cancel !");
    }

    /**
     * get user's impression text.
     * @return
     */
    protected String getImpression() {
        return impressionText;
    }
}
