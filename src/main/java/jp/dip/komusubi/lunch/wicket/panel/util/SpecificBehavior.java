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
package jp.dip.komusubi.lunch.wicket.panel.util;

import java.util.regex.Pattern;

import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;

public class SpecificBehavior {

	/**
	 * ID field behavior.
	 * @param text
	 * @return
	 */
	public static TextField<String> behaveIdField(TextField<String> text) {
		return (TextField<String>) behaveIdField((FormComponent<String>) text);
	}
	
	public static FormComponent<String> behaveIdField(FormComponent<String> component) {
		component.add(StringValidator.lengthBetween(3, 64))
					.add(new PatternValidator(Pattern.compile("[a-zA-Z0-9\\.']+")))
					.setRequired(true);
		return component;
	}

	public static PasswordTextField behavePasswordField(PasswordTextField password) {
		return (PasswordTextField) behavePasswordField((FormComponent<String>) password);
	}
	
	public static FormComponent<String> behavePasswordField(FormComponent<String> component) {
		component.add(StringValidator.minimumLength(8))
					.setRequired(true);
		return component;
	}
}
