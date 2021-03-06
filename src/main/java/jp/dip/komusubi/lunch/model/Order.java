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
package jp.dip.komusubi.lunch.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * order.
 * 
 * @author jun.ozeki
 * @since 2011/11/24
 */
public class Order implements Serializable, Iterable<OrderLine> {

    private static final long serialVersionUID = 1L;
    private int id;
    private Group group;
    private User user;
    private Shop shop;
    private boolean summary;
    private boolean cancel;
    private int amount;
    private Date datetime;
    // private int geoId;
    private List<OrderLine> lines;

    /**
     * create new instance.
     */
    public Order() {
        this(0);
    }

    /**
     * craete new instance.
     * @param id order id.
     */
    public Order(int id) {
        this.id = id;
        lines = new ArrayList<OrderLine>();
    }
    
    /**
     * append a order line. 
     * @param orderLine
     * @param summary true:if order have same product already, to summarize quantity.
     * @return
     */
    public Order addLine(OrderLine orderLine, boolean summary) {
        if (!summary) {
            lines.add(orderLine);
            return this;
        }
        boolean found = false;
        for (OrderLine ol: lines) {
            if (ol.getProduct().equals(orderLine.getProduct())) {
                ol.increment(orderLine.getQuantity());
                found = true;
            } 
        }
        if (!found)
            lines.add(orderLine);
        return this;
    }
    
    /**
     * appnd a order line.
     * @param orderLine
     * @return
     */
    public Order addLine(OrderLine orderLine) {
        addLine(orderLine, false);
        return this;
    }

    /**
     * append order lines.
     * @param orderLines
     * @param summary true:if order have same product already, to summarize quantity.
     * @return
     */
    public Order addLines(Collection<OrderLine> orderLines, boolean summary) {
        if (!summary) {
            lines.addAll(orderLines);
            return this;
        }
        for (OrderLine o: orderLines)
            addLine(o, summary);
        return this;
    }
	
    /**
     * append order lines.
     * @param orderLines
     * @return
     */
    public Order addLines(Collection<OrderLine> orderLines) {
        addLines(orderLines, false);
        return this;
    }

    /**
     * clear order lines.
     */
    public void clear() {
        lines.clear();
    }

    /**
     * get amount all order lines.
     * @return
     */
    public int getAmount() {
        // FIXME 注文確定後に商品が値段を変更した場合の対処が必要
        int amountAll = 0;
        for (OrderLine o: lines) {
            amountAll += o.getAmount();
        }
        return amountAll;
    }

    /**
     * get order date.
     * @return
     */
    public Date getDatetime() {
        return datetime;
    }

    /**
     * get group.
     * @return
     */
    public Group getGroup() {
        return group;
    }

    /**
     * get order id.
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * get order line by index.
     * @param index
     * @return
     */
    public OrderLine getOrderLine(int index) {
        return lines.get(index);
    }

    /**
     * get a order line by product id
     * @param productId
     * @return
     */
    public OrderLine getOrderLine(String productId) {
        OrderLine orderLine = null;
        if (productId == null || "".equals(productId))
            return orderLine;
        for (OrderLine o: lines) {
            if (o.getProduct() != null && productId.equals(o.getProduct().getId()))
                orderLine = o;
        }
        return orderLine;
    }

    /**
     * get order lines.
     * @return
     */
    public List<OrderLine> getOrderLines() {
        return lines;
    }

    /**
     * get order lines.
     * @param canceled true: only canceled order lines. false: only available order lines.
     * @return
     */
    public List<OrderLine> getOrderLines(boolean canceled) {
        List<OrderLine> orderLines = new ArrayList<>();
        for (OrderLine o: lines) {
            if (canceled) {
                if (o.isCancel())
                    orderLines.add(o);
            } else {
                if (!o.isCancel())
                    orderLines.add(o);
            }
        }
        return orderLines;
    }

    /**
     * get shop.
     * @return
     */
    public Shop getShop() {
        return shop;
    }

    /**
     * get user.
     * @return
     */
    public User getUser() {
        return user;
    }

    /**
     * state of order.
     * @return
     */
    public boolean isCancel() {
        return cancel;
    }

    /**
     * ... unknown ...
     * @return
     */
    public boolean isSummary() {
        return summary;
    }

    /**
     * iterator of order lines.
     */
    @Override
    public Iterator<OrderLine> iterator() {
        return lines.iterator();
    }

    /**
     * 
     * @param productId
     * @param quantity
     */
    public void modify(String productId, int quantity) {
        if (quantity < 0)
            throw new IllegalArgumentException("quantity MUST not minus: " + quantity);
        if (productId == null || "".equals(productId))
            throw new IllegalArgumentException("productId MUST required.");
        OrderLine orderLine = getOrderLine(productId);
        if (orderLine == null)
            throw new IllegalArgumentException("not found productId: " + productId);
        if ((orderLine.getQuantity() + quantity) < 1)
            throw new IllegalArgumentException("quantity can't under zero. current:" + orderLine.getQuantity()
                    + ", modify to: " + quantity);
        orderLine.increment(quantity);
    }

    /**
     * remove order line by product id 
     * FIXME delete in near future.(when use this method with retrieve from storage, it will lose consistency.)
     * @param product
     * @deprecated
     */
    @Deprecated
    public void remove(Product product) {
        remove(product.getId());
    }

    /**
     * remove order line by product id
     * FIXME delete in near future.(when use this method with retrieve from storage, it will lose consistency.)
     * @param productId
     * @return
     * @deprecated
     */
    @Deprecated
    public boolean remove(String productId) {
        boolean result = false;
        if (productId == null || "".equals(productId))
            return result;
        for (OrderLine o: lines) {
            if (o.getProduct() != null && productId.equals(o.getProduct().getId())) {
                result = lines.remove(o);
            }
        }
        return result;
    }

    /**
     * set amount.
     * @param amount
     * @return
     */
    public Order setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    /**
     * set cancel.
     * @param cancel
     * @return
     */
    public Order setCancel(boolean cancel) {
//        for (OrderLine orderLine: lines) {
//            orderLine.setCancel(cancel);
//        }
        this.cancel = cancel;
        return this;
    }

    /**
     * set date.
     * @param datetime
     * @return
     */
    public Order setDatetime(Date datetime) {
        this.datetime = datetime;
        return this;
    }

    /**
     * set group.
     * @param group
     * @return
     */
    public Order setGroup(Group group) {
        this.group = group;
        return this;
    }

    /**
     * set shop.
     * @param shop
     * @return
     */
    public Order setShop(Shop shop) {
        this.shop = shop;
        return this;
    }

    /**
     * ... unknown ...
     * @param summary
     * @return
     */
    public Order setSummary(boolean summary) {
        this.summary = summary;
        return this;
    }
    
    /**
     * set user.
     * @param user
     * @return
     */
    public Order setUser(User user) {
        this.user = user;
        return this;
    }

    /**
     * convert to receipt.
     * @return
     */
    public Receipt toReceipt() {
        Receipt receipt = new Receipt()
                            .setAmount(getAmount())
                            .setGroup(getGroup())
                            .setOrderId(getId())
                            .setShop(getShop())
                            // FIXME change date resolver
                            .setDatetime(new Date())
                            .setUser(getUser());
        for (OrderLine o: lines) 
            receipt.addLine(o.toReceiptLine());
        return receipt;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + amount;
        result = prime * result + (cancel ? 1231 : 1237);
        result = prime * result + ((datetime == null) ? 0 : datetime.hashCode());
        result = prime * result + ((group == null) ? 0 : group.hashCode());
        result = prime * result + id;
        result = prime * result + ((lines == null) ? 0 : lines.hashCode());
        result = prime * result + ((shop == null) ? 0 : shop.hashCode());
        result = prime * result + (summary ? 1231 : 1237);
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Order other = (Order) obj;
        if (amount != other.amount)
            return false;
        if (cancel != other.cancel)
            return false;
        if (datetime == null) {
            if (other.datetime != null)
                return false;
        } else if (!datetime.equals(other.datetime))
            return false;
        if (group == null) {
            if (other.group != null)
                return false;
        } else if (!group.equals(other.group))
            return false;
        if (id != other.id)
            return false;
        if (lines == null) {
            if (other.lines != null)
                return false;
        } else if (!lines.equals(other.lines))
            return false;
        if (shop == null) {
            if (other.shop != null)
                return false;
        } else if (!shop.equals(other.shop))
            return false;
        if (summary != other.summary)
            return false;
        if (user == null) {
            if (other.user != null)
                return false;
        } else if (!user.equals(other.user))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Order [id=").append(id).append(", group=").append(group).append(", user=")
                .append(user).append(", shop=").append(shop).append(", summary=").append(summary)
                .append(", cancel=").append(cancel).append(", amount=").append(amount)
                .append(", datetime=").append(datetime).append(", lines=").append(lines)
                .append("]");
        return builder.toString();
    }

}
