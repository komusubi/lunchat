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
import org.apache.wicket.model.ResourceModel;

/**
 * dialog component.
 * @author jun.ozeki
 * @since 2012/01/15
 */
public abstract class Dialog<T> extends Panel {

    private static final long serialVersionUID = 1L;

    /**
     * create new instance.
     * @param id wicket id
     * @param model 
     */
    public Dialog(String id, IModel<T> model) {
        super(id, model);
        add(getLabel("message"));
        add(new FeedbackPanel("feedback"));
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

            /**
             * event on click.
             */
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

            /**
             * event on click.
             */
            @Override
            public void onClick() {
                onCancel();
            }
        };
    }

    /**
     * event on agree button.
     */
    protected abstract void onAgree();

    /**
     * event on cancel button.
     */
    protected abstract void onCancel();

    /**
     * dialog message.
     * @param id
     * @return
     */
    protected Label getLabel(String id) {
        return new Label(id, new ResourceModel("dialog.message", "not found label."));
    }
}
