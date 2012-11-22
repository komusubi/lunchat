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
package jp.dip.komusubi.lunch.wicket;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.apache.wicket.application.ReloadingClassLoader;
import org.apache.wicket.protocol.http.ReloadingWicketFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jun.ozeki
 */
public class DevelopmentFilter extends ReloadingWicketFilter {
    private static Logger logger = LoggerFactory.getLogger(DevelopmentFilter.class);

    static {
        ReloadingClassLoader.includePattern("jp.dip.komusubi.lunch.wicket.panel.*");
//        ReloadingClassLoader.includePattern("jp.dip.komusubi.lunch.wicket.page.*");
        ReloadingClassLoader.includePattern("jp.dip.komusubi.lunch.wicket.page.settings.*");
        logger.info("wicket reloading mode on ! :{}", DevelopmentFilter.class);
    }

    @Override
    public void init(boolean bool, FilterConfig config) throws ServletException {
        logger.info("filter init start {}", this.getClass().getSimpleName());
        super.destroy();
        super.init(bool, config);
    }

    @Override
    public void destroy() {
        logger.info("destory start {}", this.getClass().getSimpleName());
        super.destroy();
    }
}
