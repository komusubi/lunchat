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

import jp.dip.komusubi.lunch.wicket.WicketSession;
import jp.dip.komusubi.lunch.wicket.component.AuthorizedFrame;
import jp.dip.komusubi.lunch.wicket.component.FormKey;
import jp.dip.komusubi.lunch.wicket.panel.MemberList;
import jp.lunchat.core.model.Group;
import jp.lunchat.core.model.User;

import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * member page.
 * @author jun.ozeki
 * @since 2011/11/16
 */
public class Member extends AuthorizedFrame {

    private static final Logger logger = LoggerFactory.getLogger(Member.class);
    private static final long serialVersionUID = 1L;
    private FormKey key;

    /**
     * create new instance.
     * @param group
     */
    public Member(Group group) {
        add(getMemberList("member.list", group));
    }

    /**
     * create new instance.
     * @param model
     */
    public Member(Model<Group> model) {
        this(model.getObject());
    }

    /**
     * create new instance.
     * @param groupModel
     * @param stringModel
     */
    public Member(Model<Group> groupModel, Model<String> stringModel) {
        this(groupModel);
        info(stringModel.getObject());
    }

    /**
     * initialize components.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.key = new FormKey(getPageId(), getId(), new Date());
    }

    /**
     * configure compoennts.
     */
    @Override
    protected void onConfigure() {
        super.onConfigure();
        WicketSession.get().addFormKey(key);
    }

    /**
     * get member list. 
     * @param id
     * @param group
     * @return
     */
    protected MemberList getMemberList(String id, Group group) {
        return new MemberList(id, group) {

            private static final long serialVersionUID = 1L;

            /**
             * event of select member.
             */
            @Override
            protected void onSelectedMember(User user) {
                if (WicketSession.get().removeFormKey(key)) {
//    				Confirmation page = new Confirmation(Model.of(user));
                    setResponsePage(new MemberConfirmation(Model.of(user)));
                } else {
                    error(getString("double.submit.message"));
                    logger.info("double submit on MemberList#getMemberList");
                }
            }

        };
    }
}
