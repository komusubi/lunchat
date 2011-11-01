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
package jp.dip.komusubi.lunch.model;

import java.io.Serializable;
import java.util.Date;

public class Group implements Serializable {
	
	private static final long serialVersionUID = 9163879003361362197L;
	private String id;
	private String name;
	private Date lastOrder;

	public Group(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public Date getLastOrder() {
		return lastOrder;
	}

	public String getName() {
		return name;
	}

//	public void setId(String id) {
//		this.id = id;
//	}

	public Group setLastOrder(Date lastOrder) {
		this.lastOrder = lastOrder;
		return this;
	}

	public Group setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Group [id=").append(id).append(", name=").append(name)
				.append(", lastOrder=").append(lastOrder).append("]");
		return builder.toString();
	}
}
