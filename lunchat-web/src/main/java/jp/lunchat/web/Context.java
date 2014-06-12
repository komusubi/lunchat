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
package jp.lunchat.web;

import javax.servlet.ServletContext;

import com.google.inject.Injector;

/**
 * @author jun.ozeki
 */
public enum Context {
    INSTANCE;
    private static ServletContext servletContext;
	private static Injector injector;

    public static <T> T getInstance(Class<T> type) {
		if (injector == null)
			injector = (Injector) servletContext.getAttribute(Injector.class.getName());
		return injector.getInstance(type);
    }

	/* package */ static final void setServletContext(ServletContext servletContext) {
		Context.servletContext = servletContext;
	}
}
