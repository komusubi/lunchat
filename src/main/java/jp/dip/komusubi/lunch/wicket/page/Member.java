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
package jp.dip.komusubi.lunch.wicket.page;

import jp.dip.komusubi.lunch.wicket.panel.Footer;
import jp.dip.komusubi.lunch.wicket.panel.Header;
import jp.dip.komusubi.lunch.wicket.panel.MemberList;

import org.apache.wicket.model.Model;

public class Member extends VariationBase {

	private static final long serialVersionUID = 4784071743684739829L;
	private static final String pageTitle = "メンバー選択";
	
	public Member() {
		add(new Header("header", Model.of(pageTitle)));
		add(new MemberList("member.list"));
		add(new Footer("footer"));
	}

}
