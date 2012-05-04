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

	private static final long serialVersionUID = -6439746231384675399L;
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

    public Order() {
		this(0);
	}

    public Order(int id) {
		this.id = id;
		lines = new ArrayList<OrderLine>();
	}
    
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
    
	public Order addLine(OrderLine orderLine) {
		addLine(orderLine, false);
		return this;
	}
	
	public Order addLines(Collection<OrderLine> orderLines, boolean summary) {
	    if (!summary) {
	        lines.addAll(orderLines);
	        return this;
	    }
	    for (OrderLine o: orderLines) 
	        addLine(o, summary);
	    return this;
	}
	
	public Order addLines(Collection<OrderLine> orderLines) {
	    addLines(orderLines, false);
		return this;
	}

	public void clear() {
		lines.clear();
	}

	public int getAmount() {
		// FIXME 注文確定後に商品が値段を変更した場合の対処が必要
		int amountAll = 0;
		for (OrderLine o: lines) {
			amountAll += o.getAmount();
		}
		return amountAll;
	}

	public Date getDatetime() {
		return datetime;
	}

	public Group getGroup() {
        return group;
    }

	public int getId() {
		return id;
	}
	
	public OrderLine getOrderLine(int index) {
		return lines.get(index);
	}

	public OrderLine getOrderLine(String productId) {
		OrderLine orderLine = null;
		if (productId == null || "".equals(productId))
			return orderLine;
		for (OrderLine o : lines) {
			if (o.getProduct() != null && productId.equals(o.getProduct().getId()))
				orderLine = o;
		}
		return orderLine;
	}

	public List<OrderLine> getOrderLines() {
	    return lines;
	}

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
	
	public Shop getShop() {
		return shop;
	}

	public User getUser() {
		return user;
	}

	public boolean isCancel() {
        return cancel;
    }

	public boolean isSummary() {
        return summary;
    }

	@Override
	public Iterator<OrderLine> iterator() {
		return lines.iterator();
	}

	public void modify(String productId, int quantity) {
		if (quantity < 0)
			throw new IllegalArgumentException("quantity MUST not minus: " + quantity);
		if (productId == null || "".equals(productId))
			throw new IllegalArgumentException("productId MUST required.");
		OrderLine orderLine = getOrderLine(productId);
		if (orderLine == null)
			throw new IllegalArgumentException("not found productId: " + productId);
		if ((orderLine.getQuantity() + quantity) < 1)
			throw new IllegalArgumentException("quantity can't under zero. current:"
					+ orderLine.getQuantity() + ", modify to: " + quantity);
		orderLine.increment(quantity);
	}

	public void remove(Product product) {
		remove(product.getId());
	}

	public boolean remove(String productId) {
		boolean result = false;
		if (productId == null || "".equals(productId))
			return result;
		for (OrderLine o : lines) {
			if (o.getProduct() != null && productId.equals(o.getProduct().getId())) {
				result = lines.remove(o);
			}
		}
		return result;
	}

	public Order setAmount(int amount) {
		this.amount = amount;
		return this;
	}

	public Order setCancel(boolean cancel) {
        this.cancel = cancel;
        return this;
    }

	public Order setDatetime(Date datetime) {
		this.datetime = datetime;
		return this;
	}

	public Order setGroup(Group group) {
        this.group = group;
        return this;
    }

	public Order setShop(Shop shop) {
		this.shop = shop;
		return this;
	}

	public Order setSummary(boolean summary) {
        this.summary = summary;
        return this;
    }

    public Order setUser(User user) {
		this.user = user;
		return this;
	}

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
