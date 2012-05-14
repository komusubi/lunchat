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
package jp.dip.komusubi.lunch.wicket.page.account;

import jp.dip.komusubi.lunch.wicket.page.VariationBase;
import jp.dip.komusubi.lunch.wicket.page.error.ErrorPage;
import jp.dip.komusubi.lunch.wicket.panel.Footer;
import jp.dip.komusubi.lunch.wicket.panel.Header;
import jp.dip.komusubi.lunch.wicket.panel.Profile;

import org.apache.wicket.model.Model;
import org.apache.wicket.request.Url.StringMode;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Registry extends VariationBase {

	private static final long serialVersionUID = -7502193205773677682L;
	private static final Logger logger = LoggerFactory.getLogger(Registry.class);
	private PageParameters params;
	
	public Registry(PageParameters params) {
		this.params = params;
		// FIXME literal string set resource file. 
		add(new Header("header", Model.of(getDefaultHeaderBean("ユーザー登録"))));
		add(new Profile("profile", Model.of(params.get("fragment").toString())));
		add(new Footer("footer"));
	}

	@Override
	protected void onInitialize() {
		if (params.get("fragment").isEmpty()) {
			logger.info("malformed url {}", getRequest().getClientUrl().toString(StringMode.FULL));
			setResponsePage(new ErrorPage("不正なURLです。"));
		}
		super.onInitialize();
	}
}
