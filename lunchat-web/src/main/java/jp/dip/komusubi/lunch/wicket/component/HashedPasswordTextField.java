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
package jp.dip.komusubi.lunch.wicket.component;

import java.util.Locale;

import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;
import org.komusubi.common.util.Resolver;

public class HashedPasswordTextField extends PasswordTextField {

	private static final long serialVersionUID = 4572617554012113411L;
	private Resolver<String> resolver;
	private IConverter<String> hashConverter = new IConverter<String>() {
		private static final long serialVersionUID = 3644773075381778441L;
		
		public String convertToObject(String value, Locale locale) {
			return resolver.resolve(value);
//			return Digest.hash(value);
		}
		
		public String convertToString(String value, Locale locale) {
			return value;
		}
	};
	
	public HashedPasswordTextField(String id, Resolver<String> resolver) {
		this(id, null, resolver);
	}

	public HashedPasswordTextField(String id, IModel<String> model, Resolver<String> resolver) {
		super(id, model);
		this.resolver = resolver;
	}


	@SuppressWarnings({ "hiding", "unchecked" })
	@Override
	public <String> IConverter<String> getConverter(Class<String> type) {
		return (IConverter<String>) hashConverter;
	}

}
