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
package jp.dip.komusubi.lunch.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author jun.ozeki
 * @since 2012/04/09
 */
public class Receipt implements Serializable, Iterable<ReceiptLine> {

    private static final long serialVersionUID = -5076725047617672217L;

    private Integer id;
    private Integer orderId;
    private User user;
    private Group group;
    private Shop shop;
    private int amount;
    private Date datetime;
    private List<ReceiptLine> lines;
    
    public Receipt() {
        this(0);
    }
    public Receipt(Integer id) {
        this.id = id;
        lines = new ArrayList<>();
    }
    
    public void addLine(ReceiptLine receiptLine) {
        lines.add(receiptLine);
    }
    
    public void addLines(Collection<ReceiptLine> receiptLines) {
        lines.addAll(receiptLines);
    }
    public int getAmount() {
        return amount;
    }
    public Date getDatetime() {
        return datetime;
    }
    public Group getGroup() {
        return group;
    }
    public Integer getId() {
        return id;
    }
    public Integer getOrderId() {
        return orderId;
    }
    public Shop getShop() {
        return shop;
    }
    public User getUser() {
        return user;
    }
    @Override
    public Iterator<ReceiptLine> iterator() {
        return lines.iterator(); 
    }
    public Receipt setAmount(int amount) {
        this.amount = amount;
        return this;
    }
    public Receipt setDatetime(Date datetime) {
        this.datetime = datetime;
        return this;
    }
    public Receipt setGroup(Group group) {
        this.group = group;
        return this;
    }
    public Receipt setId(Integer id) {
        this.id = id;
        return this;
    }
    public Receipt setOrderId(Integer orderId) {
        this.orderId = orderId;
        return this;
    }
    public Receipt setShop(Shop shop) {
        this.shop = shop;
        return this;
    }
    public Receipt setUser(User user) {
        this.user = user;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Receipt [id=").append(id).append(", orderId=").append(orderId)
                .append(", user=").append(user).append(", group=").append(group).append(", shop=")
                .append(shop).append(", amount=").append(amount).append(", datetime=")
                .append(datetime).append(", lines=").append(lines).append("]");
        return builder.toString();
    }
}
