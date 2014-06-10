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

import jp.dip.komusubi.lunch.Configuration;
import jp.dip.komusubi.lunch.model.User;
import jp.dip.komusubi.lunch.service.AccountService;
import jp.dip.komusubi.lunch.wicket.WicketSession;
import jp.dip.komusubi.lunch.wicket.component.AuthorizedPage;
import jp.dip.komusubi.lunch.wicket.component.FormKey;
import jp.dip.komusubi.lunch.wicket.panel.Dialog;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * confirmation to user of the group member.
 * @author jun.ozeki
 * @since 2012/02/26
 */
public class MemberConfirmation extends AuthorizedPage {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(MemberConfirmation.class);
    private FormKey key;
    private Model<User> model;

    /**
     * create new instance.
     * @param model
     */
    public MemberConfirmation(Model<User> model) {
        this.model = model;
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
     * confirm dialog.
     * @param id
     * @param model
     * @return
     */
    protected Dialog<User> getDialog(String id, final Model<User> model) {

        return new Dialog<User>(id, model) {

            private static final long serialVersionUID = 1L;

            /**
             * event on send confirmation message.
             */
            @Override
            protected void onAgree() {
                Member member;
                try {
                    if (WicketSession.get().removeFormKey(key)) {
                        AccountService service = Configuration.getInstance(AccountService.class);
                        String url = getPageUrl(Attendance.class);

                        service.apply(model.getObject(), WicketSession.get().getSignedInUser(), url);
                        member = new Member(Model.of(model.getObject().getGroup()), 
                                        Model.of(getLocalizer().getString("sent.confirmatino.title", MemberConfirmation.this, model)));
                    } else {
                        error(getLocalizer().getString("double.submit", MemberConfirmation.this)); 
                        return;
                    }
                } catch (Exception e) {
                    logger.warn("exception: {}", e);
                    member = new Member(Model.of(model.getObject().getGroup()), 
                            Model.of(getLocalizer().getString("confirm.error.title", MemberConfirmation.this, model)));
                }
                setResponsePage(member);
            }

            /**
             * event on cancel to send confirmation.
             */
            @Override
            protected void onCancel() {
                Member member = new Member(Model.of(model.getObject().getGroup()));
                setResponsePage(member);
            }

            /**
             * get dialog message.
             */
            @Override
            protected Label getLabel(String id) {
                return new Label(id, getLocalizer().getString("dialog.message", MemberConfirmation.this, model));
            }
        };
    }

}
