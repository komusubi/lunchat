/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package jp.dip.komusubi.lunch;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

public enum Configuration {
	INSTANCE;
	private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
	private static Injector injector;
	private static ServletContext servletContext;

	public static <T> T getInstance(Class<T> type) {
		if (injector == null)
			injector = (Injector) servletContext.getAttribute(Injector.class.getName());
		return injector.getInstance(type);
	}

	public static String getParameter(String key) {
		return getParameter(key, null);
	}

	public static String getParameter(String key, String def) {
		String value = servletContext.getInitParameter(key);
		if (value == null) {
			logger.error("\"{}\" value is null", key);
		}
		return value == null ? def : value;
	}

	public static RuntimeMode mode() {
	    if (Boolean.valueOf(getParameter("wicket.release.mode")))
	        return RuntimeMode.DEPLOYMENT;
	    else
	        return RuntimeMode.DEVELOPMENT;
	}

	static final void setServletContext(ServletContext servletContext) {
		Configuration.servletContext = servletContext;
	}

	public static enum RuntimeMode {
		DEVELOPMENT, DEPLOYMENT;
	}
	
}
