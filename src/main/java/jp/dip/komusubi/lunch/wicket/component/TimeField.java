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

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

public class TimeField extends TextField<Date> {

	private static final long serialVersionUID = -2538831506163010339L;
	private static final String[] formats = {"HH:mm"}; 
	
	public TimeField(String id) {
		this(id, null, null, Calendar.getInstance());
	}

	public TimeField(String id, Class<Date> type) {
		this(id, null, type, Calendar.getInstance());
	}

	public TimeField(String id, IModel<Date> model, Class<Date> type, Calendar cal) {
		super(id, model, type);
	}

	public TimeField(String id, IModel<Date> model, Class<Date> type, Date date) {
		this(id, model, type, getCalendar(date));
	}
	
	public TimeField(String id, IModel<Date> model) {
		this(id, model, null, Calendar.getInstance());
	}
	
	private static Calendar getCalendar(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}
	
	@Override
	protected String getInputType() {
		return "time";
	}
	
	@SuppressWarnings("hiding")
	@Override
	public <Date> IConverter<Date> getConverter(Class<Date> type) {
		return new IConverter<Date>() {

			private static final long serialVersionUID = -1613523542730161173L;

			@SuppressWarnings("unchecked")
			@Override
			public Date convertToObject(String value, Locale locale) {
				Date date = null;
				try {
					date = (Date) DateUtils.parseDate(value, formats);
				} catch (ParseException e) {
					throw new ConversionException(e);
				}
				return date;
			}

			@Override
			public String convertToString(Date value, Locale locale) {
				return DateFormatUtils.format(((java.util.Date) value), "HH:mm", locale);
			}
			
		};
	}

}
