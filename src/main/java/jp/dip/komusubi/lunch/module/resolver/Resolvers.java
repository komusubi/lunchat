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
package jp.dip.komusubi.lunch.module.resolver;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import jp.dip.komusubi.common.util.Resolver;

import org.apache.commons.lang3.time.DateUtils;

/**
 * resolvers. 
 * @author jun.ozeki
 * @since 2011/12/03
 */
public class Resolvers {

	private Locale locale;

	public Resolvers() {
		
	}
	
	public Resolvers(Locale locale) {
		this.locale = locale;
	}
	
	public Resolver<Date> getResolver() {
		return dateResolver;
	}
	
	public static Resolver<Date> dateResolver = new Resolver<Date>() {
		@Override
		public Date resolve() {
			return new Date();
		}
		
		@Override
		public Date resolve(Date date) {
			return date;
		}
	};
	
	public static class CalendarResolver implements Resolver<Calendar> {

		private Locale locale;

		public CalendarResolver() {
			
		}
		
		public CalendarResolver(Locale locale) {
			this.locale = locale;
		}
		
		@Override
		public Calendar resolve() {
			return Calendar.getInstance();
//			Date date = null;
//			try {
//				date = DateUtils.parseDate("2011/12/11 14:00:00", new String[]{"yyyy/MM/dd HH:mm:ss"});
//			} catch (ParseException e) {
//				date = new Date();
//			}
//			Calendar cal = Calendar.getInstance();
//			cal.setTime(date);
//			return cal;
		}

		@Override
		public Calendar resolve(Calendar value) {
			return value;
		}
	
	}

}
