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
import java.util.Date;

/**
 * @author jun.ozeki
 * @since 2012/04/09
 */
public class ReceiptLine implements Serializable {

    public static class ReceiptLineKey implements Serializable {

        private static final long serialVersionUID = -2180378021412599367L;
        private Integer no;
        private Integer receiptId;

        public ReceiptLineKey() {
            this(new Integer(0), new Integer(0));
        }

        public ReceiptLineKey(Integer receiptId, Integer no) {
            this.receiptId = receiptId;
            this.no = no;
        }
    
        public Integer getNo() {
            return no;
        }
        
        public Integer getReceiptId() {
            return receiptId;
        }

        public ReceiptLineKey setNo(Integer no) {
            this.no = no;
            return this;
        }

        public ReceiptLineKey setReceiptId(Integer receiptId) {
            this.receiptId = receiptId;
            return this;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("ReceiptLineKey [no=").append(no).append(", receiptId=")
                    .append(receiptId).append("]");
            return builder.toString();
        }
    }

    private static final long serialVersionUID = -6164693604031798496L;
    
    private ReceiptLineKey primaryKey;
    private Product product;
    private int quantity;
    private int amount;
    private String memo;
    private Date datetime;

    public ReceiptLine() {
        this(new ReceiptLineKey());
    }
    
    public ReceiptLine(ReceiptLineKey primaryKey) {
        this.primaryKey = primaryKey;
    }
    
    public int getAmount() {
        return amount;
    }

    public Date getDatetime() {
        return datetime;
    }

    public String getMemo() {
        return memo;
    }

    public ReceiptLineKey getPrimaryKey() {
        return primaryKey;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public ReceiptLine setAmount(int amount) {
        this.amount = amount;
        return this;
    }
    
    public ReceiptLine setDatetime(Date datetime) {
        this.datetime = datetime;
        return this;
    }
    
    public ReceiptLine setMemo(String memo) {
        this.memo = memo;
        return this;
    }
    
    public ReceiptLine setPrimaryKey(ReceiptLineKey primaryKey) {
        this.primaryKey = primaryKey;
        return this;
    }
    
    public ReceiptLine setProduct(Product product) {
        this.product = product;
        return this;
    }

    public ReceiptLine setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ReceiptLine [primaryKey=").append(primaryKey).append(", product=")
                .append(product).append(", quantity=").append(quantity).append(", amount=")
                .append(amount).append(", memo=").append(memo).append(", datetime=")
                .append(datetime).append("]");
        return builder.toString();
    }
}
