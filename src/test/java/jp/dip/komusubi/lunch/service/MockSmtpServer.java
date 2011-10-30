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
package jp.dip.komusubi.lunch.service;

import jp.dip.komusubi.common.protocol.smtp.MailMessage;
import jp.dip.komusubi.common.protocol.smtp.SmtpServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockSmtpServer extends SmtpServer {
	private static final Logger logger = LoggerFactory.getLogger(MockSmtpServer.class);
	private static final long serialVersionUID = -8430998293174833975L;
	private MailMessage message;

	@Override
	public void send(MailMessage message) {
		logger.info("mock smtp server!! send message done. {}", message);
		this.message = message;
	}
	
	public MailMessage getMailMessage() {
		return message;
	}
}
