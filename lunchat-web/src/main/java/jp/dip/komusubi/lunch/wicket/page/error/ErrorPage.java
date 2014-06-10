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
package jp.dip.komusubi.lunch.wicket.page.error;

import jp.dip.komusubi.lunch.wicket.component.ApplicationFrame;

import org.apache.wicket.markup.html.basic.Label;

/**
 * error page.
 * @author jun.ozeki
 */
public class ErrorPage extends ApplicationFrame {

    private static final long serialVersionUID = 1L;
    private String message;
    
    /**
     * create new instance.
     */
    public ErrorPage() {
        // 
        message = getString("unknown.error");
    }
    
    /**
     * create new instance.
     * @param message
     */
    public ErrorPage(String message) {
        this.message = message;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new Label("error.message", message));
    }
    
    /**
     * @see org.apache.wicket.Component#isVersioned()
     */
    @Override
    public boolean isVersioned() {
        return false;
    }

    /**
     * @see org.apache.wicket.Page#isErrorPage()
     */
    @Override
    public boolean isErrorPage() {
        return true;
    }

}
