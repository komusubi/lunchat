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
package jp.dip.komusubi.lunch.wicket.component;

import jp.dip.komusubi.lunch.wicket.WicketApplication;
import jp.dip.komusubi.lunch.wicket.WicketSession;
import jp.dip.komusubi.lunch.wicket.page.account.Setting;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * framework page.
 * @author jun.ozeki
 */
public class ApplicationFrame extends ApplicationPage {

    private static final long serialVersionUID = 1L;
    private static Logger logger = LoggerFactory.getLogger(ApplicationFrame.class);

    /**
     * custom link component.
     * changeable <code>Link</code> component in ParentFrame class.
     * @author jun.ozeki
     * @param <K>
     */
    private static abstract class CustomLink<K> extends Link<K> {

        private static final long serialVersionUID = 1L;

        /**
         * create new insatnce. 
         * @param id
         */
        public CustomLink(String id) {
            super(id);
        }
        
        /**
         * create new instance.
         * @param id
         * @param model
         */
        public CustomLink(String id, IModel<K> model) {
            super(id, model);

        }
    }
    
    /**
     * create new instance.
     */
    public ApplicationFrame() {
   }
     
    /**
     * create new instance.
     * @param model
     */
    public ApplicationFrame(IModel<?> model) {
        super(model);
    }

    /**
     * create new instance.
     * @param parameters
     */
    public ApplicationFrame(PageParameters parameters) {
        super(parameters);
    }   
    
    /**
     * initialize componeents.
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(newNavigationBar("nav"));
        add(newHomeLink("link.home"));
        add(newSignInLink("signIn"));
        add(newSignOutLink("signOut"));
    }

    /**
     * create home link.
     * @param id
     * @return
     */
    protected Link<String> newHomeLink(String id) {
        return new CustomLink<String>(id) {

            private static final long serialVersionUID = 1L;

            /**
             * click home link.
             */
            @Override
            public void onClick() {
                setResponsePage(WicketApplication.get().getHomePage());
            }
            
        };
    }
    
    /**
     * craete sign out link.
     * @param id 
     * @return
     */
    protected Link<String> newSignOutLink(String id) {
        return new CustomLink<String>(id) {

            private static final long serialVersionUID = 1L;

            /**
             * initialize components.
             */
            @Override
            protected void onInitialize() {
                super.onInitialize();
                add(new Label("signOut.title", getString("signOut.title")));
            }
            
            /**
             * configure visible components.
             * this component is visible when signed in.
             */
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(WicketSession.get().isSignedIn());
            }
            
            /**
             * click sign out link.
             */
            @Override
            public void onClick() {
                logger.info("{} is logout", WicketSession.get().getSignedInUser().getId());
                WicketSession.get().invalidate();
                setResponsePage(WicketApplication.get().getHomePage());
            }
            
        };
    }
    
    /**
     * create sign in link.
     * @param id
     * @return
     */
    protected Link<String> newSignInLink(String id) {
        return new CustomLink<String>(id) {

            private static final long serialVersionUID = 1L;

            /**
             * initialize components.
             */
            @Override
            protected void onInitialize() {
                super.onInitialize(); 
                add(new Label("signIn.title", getString("signIn.title")));
            }

            /**
             * configure visible for components.
             * this component is visible when NOT signed in,
             */
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(!WicketSession.get().isSignedIn());
            }
            
            /**
             * click sign in link.
             */
            @Override
            public void onClick() {
                setResponsePage(WicketApplication.get().getSignInPageClass());
            }
        };
    }
    
    /**
     * create navigation bar.
     * @param id wicket id
     * @return navi bar component
     */
    protected WebMarkupContainer newNavigationBar(String id) {
        return new WebMarkupContainer(id) {
            
            private static final long serialVersionUID = 1L;

            /**
             * initialize components.
             */
            @Override
            protected void onInitialize() {
                super.onInitialize();
                add(newOrderLink("nav.new"));
                add(newHistoryLink("nav.history"));
                add(newConfigurationLink("nav.config"));
            }
            
            /**
             * configure compoennts.
             * this component is visible when signed in.
             */
            @Override
            protected void onConfigure() {
                setVisibilityAllowed(WicketSession.get().isSignedIn());
            }
            
            /**
             * create order link.
             * @param id
             * @return
             */
            protected Link<String> newOrderLink(String id) {
                return new CustomLink<String>(id) {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick() {
                        onOrderLinkClick();
                    }
                    
                };
            }
            
            /**
             * create history link.
             * @param id
             * @return
             */
            protected Link<?> newHistoryLink(String id) {
                return new CustomLink<String>(id) {
                    
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick() {
                        onHistoryLinkClick();
                    }
                };
            }
            
            /**
             * create configuration link.
             * @param id
             * @return
             */
            protected Link<?> newConfigurationLink(String id) {
                return new CustomLink<String>(id) {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick() {
                        onConfigurationLinkClick();
                    }
                    
                };
            }
        };
    }
    
    /**
     * order link click.
     * override subclass.
     */
    protected void onOrderLinkClick() {
        logger.info("order link clicked!");
    }
    
    /**
     * history link click.
     * override subclass.
     */
    protected void onHistoryLinkClick() {
        logger.info("history link clicked!");
    }
    
    /**
     * configuration link click.
     * override subclass.
     */
    protected void onConfigurationLinkClick() {
        setResponsePage(Setting.class);
        logger.info("configuration link clicked!");
    }
    
}