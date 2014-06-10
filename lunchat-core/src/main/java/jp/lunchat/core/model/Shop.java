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
package jp.lunchat.core.model;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.dip.komusubi.lunch.Configuration;
import jp.dip.komusubi.lunch.module.dao.ContractDao;

/**
 * 
 * @author jun.ozeki
 * @since 2011/12/11
 */
public class Shop implements Serializable {

	public static final String DEFAULT_ID = "default";
    private static final long serialVersionUID = -1020122183352301083L;

    private String id;
	private String name;
	// private GeoLocation geoLocation;
	private String url;
	private String phoneNumber;
	private Date lastOrder;
	private List<Contract> contracts;
	
	public Shop() {
		this(DEFAULT_ID);
	}
	public Shop(String id) {
		this.id = id;
	}

	public Shop addContracts(Contract contract) {
		if (contracts == null)
//			contracts = new HashSet<>();
			contracts = new ArrayList<>();
		this.contracts.add(contract);
		return this;
	}
	
	@Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Shop other = (Shop) obj;
        if (contracts == null) {
            if (other.contracts != null)
                return false;
        } else if (!contracts.equals(other.contracts))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (lastOrder == null) {
            if (other.lastOrder != null)
                return false;
        } else if (!lastOrder.equals(other.lastOrder))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (phoneNumber == null) {
            if (other.phoneNumber != null)
                return false;
        } else if (!phoneNumber.equals(other.phoneNumber))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }

	public List<Contract> getContracts() {
		if (contracts == null) {
			// FIXME should be proxy pattern.
			ContractDao contractDao = Configuration.getInstance(ContractDao.class);
			contracts = contractDao.findByShopId(getId());
		}
		return contracts;
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

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getUrl() {
		return url;
	}

	public URL getURL() {
		URL url = null;
		try {
			url = new URL(this.url);
		} catch (MalformedURLException e) {
			// ignore
		}
		return url;
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((contracts == null) ? 0 : contracts.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((lastOrder == null) ? 0 : lastOrder.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

	public boolean isDayOff() {
	    return isDayOff(new Date());
	}

	public boolean isDayOff(Date date) {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    boolean dayOff = false;
	    if (Calendar.SATURDAY == cal.get(Calendar.DAY_OF_WEEK)
	            || Calendar.SUNDAY == cal.get(Calendar.DAY_OF_WEEK)) {
	        dayOff = true;
	    }
	    return dayOff;
	}

	public Shop setContracts(List<Contract> contracts) {
		this.contracts = contracts;
		return this;
	}

	public Shop setLastOrder(Date lastOrder) {
		this.lastOrder = lastOrder;
		return this;
	}

	public Shop setName(String name) {
		this.name = name;
		return this;
	}

	public Shop setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
		return this;
	}
	
	public Shop setUrl(String url) {
		this.url = url;
		return this;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Shop [id=").append(id).append(", name=").append(name).append(", url=")
				.append(url).append(", phoneNumber=").append(phoneNumber).append(", lastOrder=")
				.append(lastOrder).append(", contracts=").append(contracts).append("]");
		return builder.toString();
	}
}
