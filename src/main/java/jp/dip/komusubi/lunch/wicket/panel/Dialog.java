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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * dialog component.
 * @author jun.ozeki
 * @since 2012/01/15
 */
public abstract class Dialog<T> extends Panel {

    private static final long serialVersionUID = 6968078827891651580L;

    /**
     * create new instance.
     * @param id wicket id
     * @param model 
     */
    public Dialog(String id, IModel<T> model) {
        super(id, model);
        add(getLabel("message"));
        add(new FeedbackPanel("feedback"));
//		add(new DialogForm("confirm.form"));
        add(getAgreeLink("agree"));
        add(getCancelLink("cancel"));
    }

    /**
     * create new agree event <code>Link</code>.
     * @param id wicket id
     * @return
     */
    protected Link<String> getAgreeLink(String id) {
        return new Link<String>(id) {

            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                onAgree();
            }
        };
    }
    
    /**
     * create new cancel event <code>Link</code>.
     * @param id wicket id
     * @return
     */
    protected Link<String> getCancelLink(String id) {
        return new Link<String>(id) {

            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                onCancel();
            }
        };
    }

    /**
     * action on event of agree.
     */
    protected abstract void onAgree();

    /**
     * action on event of cancel.
     */
    protected abstract void onCancel();

    protected Label getLabel(String id) {
        return new Label(id, "ラベル");
    }
}
