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
package jp.lunchat.storage;

import jp.dip.komusubi.lunch.util.Nonce;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DefaultNonce implements Nonce {

	private static final long serialVersionUID = -5470620148919390867L;
	private static final Logger logger = LoggerFactory.getLogger(DefaultNonce.class);
	private String salt;

	public DefaultNonce() {
		
	}
	
	public String get(String value, String salt) {
		Validate.notNull(value, "value is MUST not null.");
		StringBuilder builder = new StringBuilder(value);
		if (logger.isDebugEnabled())
			logger.debug("salt is {}", salt);
		builder.append(salt);

		return DigestUtils.shaHex(builder.toString());
	}
	
	public String get(String value) {
		return get(value, salt());
	}

	public String salt() {
		if (salt == null)
			salt = String.valueOf(System.currentTimeMillis() + Math.random());
		return salt;
	}
	
}
