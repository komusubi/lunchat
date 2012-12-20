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
package jp.dip.komusubi.lunch.module.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import jp.dip.komusubi.lunch.LunchException;
import jp.dip.komusubi.lunch.model.Product;
import jp.dip.komusubi.lunch.model.Shop;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.empire.data.DataMode;
import org.apache.empire.data.DataType;
import org.apache.empire.db.DBCmdType;
import org.apache.empire.db.DBColumn;
import org.apache.empire.db.DBCommand;
import org.apache.empire.db.DBDatabase;
import org.apache.empire.db.DBDatabaseDriver;
import org.apache.empire.db.DBRecord;
import org.apache.empire.db.DBSQLScript;
import org.apache.empire.db.DBTable;
import org.apache.empire.db.DBTableColumn;
import org.apache.empire.db.exceptions.QueryFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lunchat extends DBDatabase {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(Lunchat.class);
    private static final int sizeOfShopId = 64;
    private static final int sizeOfProductId = 64;

    // users
    public static class Users extends DBTable {
        private static final long serialVersionUID = 1L;

        public final DBTableColumn id;
        public final DBTableColumn email;
        public final DBTableColumn password;
        public final DBTableColumn nickname;
        public final DBTableColumn name;
        public final DBTableColumn joined; // 加入日

        public Users(DBDatabase db) {
            super("users", db);
            id          = addColumn("id",       DataType.AUTOINC,     0, DataMode.AutoGenerated);
            email       = addColumn("email",    DataType.TEXT,      255, DataMode.NotNull);
            password    = addColumn("password", DataType.TEXT,       64, DataMode.NotNull);
            nickname    = addColumn("nickname", DataType.TEXT,      128, DataMode.NotNull);
            name        = addColumn("name",     DataType.TEXT,      128, DataMode.NotNull);
            joined      = addColumn("joined",   DataType.DATETIME,    0, DataMode.NotNull);

            setPrimaryKey(id);
            addIndex("users_email_idx", true, new DBTableColumn[] { email });
            addIndex("users_nickname_idx", true, new DBTableColumn[]{ nickname });
            setCascadeDelete(true);
        }
    }

    // health
    public static class Health extends DBTable {

        private static final long serialVersionUID = 1L;

        public final DBTableColumn userId;
        public final DBTableColumn login;
        public final DBTableColumn lastLogin;
        public final DBTableColumn loginFail;
        public final DBTableColumn admitted; // 加入承認者
        public final DBTableColumn groupId;
        public final DBTableColumn groupJoined;
        public final DBTableColumn active;

        public Health(DBDatabase db) {
            super("health", db);
            userId      = addColumn("userId",       DataType.INTEGER,          0, DataMode.NotNull);
            login       = addColumn("login",        DataType.INTEGER,          0, DataMode.NotNull);
            lastLogin   = addColumn("lastLogin",    DataType.DATETIME,         0, DataMode.Nullable);
            loginFail   = addColumn("loginFail",    DataType.INTEGER,          0, DataMode.NotNull);
            admitted    = addColumn("admitted",     DataType.TEXT,            64, DataMode.Nullable);
            groupId     = addColumn("groupId",      DataType.INTEGER,          0, DataMode.Nullable);
            groupJoined = addColumn("groupJoined",  DataType.DATETIME,         0, DataMode.Nullable);
            active      = addColumn("active",       DataType.BOOL,             0, DataMode.NotNull);

            setPrimaryKey(userId);
        }
    }

    // notice
    public static class Notices extends DBTable {

        private static final long serialVersionUID = 1L;

        public final DBTableColumn id; // identify a user, so that email address, twitter id, facebook id etc...
        public final DBTableColumn userId;
        public final DBTableColumn type; // email, twitter, facebook and so on.
        public final DBTableColumn priority;

        public Notices(DBDatabase db) {
            super("notices", db);
            id       = addColumn("id",      DataType.TEXT,  255, DataMode.NotNull);
            userId   = addColumn("userId",  DataType.INTEGER, 0, DataMode.NotNull);
            type     = addColumn("type",    DataType.INTEGER, 0, DataMode.NotNull);
            priority = addColumn("priority",DataType.INTEGER, 0, DataMode.NotNull);

            addIndex("notice_user_priority_idx", true, new DBTableColumn[] { userId, priority });

            setPrimaryKey(id);
        }
    }

    // groups
    public static class Groups extends DBTable {

        private static final long serialVersionUID = 1L;

        public final DBTableColumn id;
        public final DBTableColumn code;
        public final DBTableColumn geoId;
        public final DBTableColumn name;
        public final DBTableColumn place;
        public final DBTableColumn phoneNumber;

        public Groups(DBDatabase db) {
            super("groups", db);

            id          = addColumn("id",           DataType.AUTOINC,     0, DataMode.NotNull);
            code        = addColumn("code",         DataType.TEXT,      128, DataMode.NotNull);
            geoId       = addColumn("geoId",        DataType.INTEGER,     0, DataMode.Nullable);
            name        = addColumn("name",         DataType.TEXT,      128, DataMode.NotNull);
            place       = addColumn("place",        DataType.TEXT,      255, DataMode.Nullable);
            phoneNumber = addColumn("phoneNumber",  DataType.TEXT,       64, DataMode.Nullable);

            setPrimaryKey(id);
            addIndex("groups_code_idx", true, new DBTableColumn[]{ code });
        }

    }

    // shop
    public static class Shops extends DBTable {

        private static final long serialVersionUID = 1L;

        public final DBTableColumn id;
        public final DBTableColumn geoId;
        public final DBTableColumn name;
        public final DBTableColumn url;
        public final DBTableColumn phoneNumber;
        public final DBTableColumn lastOrder;

        public Shops(DBDatabase db) {
            super("shops", db);
            id          = addColumn("id",           DataType.TEXT, sizeOfShopId, DataMode.NotNull);
            name        = addColumn("name",         DataType.TEXT,      255,     DataMode.NotNull);
            geoId       = addColumn("geoId",        DataType.INTEGER,     0,     DataMode.Nullable);
            url         = addColumn("url",          DataType.TEXT,      255,     DataMode.Nullable);
            phoneNumber = addColumn("phoneNumber",  DataType.TEXT,       64,     DataMode.Nullable);
            // 店舗単位でもラストオーダー(受注の締切り時刻があっても良いはず)追加
            lastOrder   = addColumn("lastOrder",    DataType.DATETIME,    0,     DataMode.NotNull);

            setPrimaryKey(id);
        }

    }

    // contract
    public static class Contracts extends DBTable {

        private static final long serialVersionUID = 1L;

        public final DBTableColumn id;
        public final DBTableColumn groupId;
        public final DBTableColumn shopId;
        public final DBTableColumn contracted;

        public Contracts(DBDatabase db) {
            super("contracts", db);
            id          = addColumn("id",         DataType.AUTOINC, 0, DataMode.AutoGenerated);
            groupId     = addColumn("groupId",    DataType.INTEGER, 0, DataMode.NotNull);
            shopId      = addColumn("shopId",     DataType.TEXT, sizeOfShopId, DataMode.NotNull);
            contracted  = addColumn("contracted", DataType.DATETIME, 0, DataMode.Nullable);

            setPrimaryKey(id);
            addIndex("contract_group_shop_idx", true, new DBColumn[] { groupId, shopId });
        }
    }

    // product
    public static class Products extends DBTable {

        private static final long serialVersionUID = 1L;

        public final DBTableColumn id;
        public final DBTableColumn refId; 
        public final DBTableColumn shopId;
        public final DBTableColumn name;
        public final DBTableColumn amount;
        public final DBTableColumn start;
        public final DBTableColumn finish;

        public Products(DBDatabase db) {
            super("products", db);
            id      = addColumn("id",       DataType.TEXT,  sizeOfProductId, DataMode.NotNull);
            refId   = addColumn("refId",    DataType.TEXT,  sizeOfProductId, DataMode.Nullable);
            shopId  = addColumn("shopId",   DataType.TEXT,  sizeOfShopId, DataMode.NotNull);
            name    = addColumn("name",     DataType.TEXT,              255, DataMode.NotNull);
            amount  = addColumn("amount",   DataType.INTEGER,             0, DataMode.NotNull);
            start   = addColumn("start",    DataType.DATETIME,            0, DataMode.Nullable);
            finish  = addColumn("finish",   DataType.DATETIME,            0, DataMode.Nullable);

            setPrimaryKey(id);
            setCascadeDelete(true);
        }

    }

    // receiving
    public static class Receipts extends DBTable {

        private static final long serialVersionUID = 1L;

        public final DBTableColumn id;
        public final DBTableColumn orderId;
        public final DBTableColumn userId;
        public final DBTableColumn groupId;
        public final DBTableColumn shopId;
        public final DBTableColumn amount;
        public final DBTableColumn datetime;

        public Receipts(DBDatabase db) {
            super("receipts", db);
            id        = addColumn("id",        DataType.AUTOINC,  0,        DataMode.AutoGenerated);
            orderId   = addColumn("orderId",   DataType.INTEGER,  0,        DataMode.NotNull);
            userId    = addColumn("userId",    DataType.INTEGER,  0,        DataMode.NotNull);
            groupId   = addColumn("groupId",   DataType.INTEGER,  0,        DataMode.NotNull);
            shopId    = addColumn("shopId",    DataType.TEXT, sizeOfShopId, DataMode.NotNull);
            amount    = addColumn("amount",    DataType.INTEGER,  0,        DataMode.NotNull);
            datetime  = addColumn("datetime",  DataType.DATETIME, 0,        DataMode.NotNull);

            setPrimaryKey(id);
        }

    }

    public static class ReceiptLines extends DBTable {

        private static final long serialVersionUID = 1L;
        
        public final DBTableColumn receiptId;
        public final DBTableColumn no;
        public final DBTableColumn productId;
        public final DBTableColumn quantity;
        public final DBTableColumn amount;
        public final DBTableColumn memo;
        public final DBTableColumn datetime; 
        
        public ReceiptLines(DBDatabase db) {
            super("receiptLines", db);
            receiptId = addColumn("receiptId", DataType.INTEGER,    0,          DataMode.NotNull);
            no        = addColumn("no",        DataType.INTEGER,    0,          DataMode.NotNull);
            productId = addColumn("productId", DataType.TEXT, sizeOfProductId,  DataMode.NotNull);
            quantity  = addColumn("quantity",  DataType.INTEGER,    0,          DataMode.NotNull);
            amount    = addColumn("amount",    DataType.INTEGER,    0,          DataMode.Nullable);
            memo      = addColumn("memo",      DataType.TEXT,     256,          DataMode.Nullable);       
            datetime  = addColumn("datetime",  DataType.DATETIME,   0,          DataMode.NotNull);
            
            setPrimaryKey(receiptId, no);
        }
    }
    
    // shipping
    public static class Shipping extends DBTable {

        private static final long serialVersionUID = 1L;

        public final DBTableColumn id;
        public final DBTableColumn departure;
        public final DBTableColumn arrival;
        public final DBTableColumn orderId;
        public final DBTableColumn datetime;

        public Shipping(DBDatabase db) {
            super("shipping", db);
            id        = addColumn("id",         DataType.AUTOINC,  0, DataMode.AutoGenerated);
            orderId   = addColumn("orderId",    DataType.INTEGER,  0, DataMode.NotNull);
            departure = addColumn("departure",  DataType.INTEGER,  0, DataMode.Nullable);
            arrival   = addColumn("arrival",    DataType.INTEGER,  0, DataMode.Nullable);
            datetime  = addColumn("datetime",   DataType.DATETIME, 0, DataMode.NotNull);

            // unique
            // groupId, shopId, productId, datetime
//			addIndex("shipping_idx", true, new DBColumn[]{groupId, shopId, datetime});
            setPrimaryKey(id);
        }

    }

    // shipping lines
    public static class ShippingLines extends DBTable {

        private static final long serialVersionUID = 1L;

        public final DBTableColumn shippingId;
        public final DBTableColumn productId;
        public final DBTableColumn quantity;
        public final DBTableColumn amount;

//		public final DBTableColumn datetime;

        public ShippingLines(DBDatabase db) {
            super("shippingLines", db);

            shippingId = addColumn("shippingId", DataType.INTEGER,          0, DataMode.NotNull);
            productId  = addColumn("productId",  DataType.TEXT, sizeOfProductId, DataMode.NotNull);
            quantity   = addColumn("quantity",   DataType.INTEGER,          0, DataMode.NotNull);
            amount     = addColumn("amount",     DataType.INTEGER,          0, DataMode.NotNull);
//			datetime 	= addColumn("datetime", 	DataType.DATETIME, 				  0, DataMode.NotNull);

            addIndex("shippingLines_idx", true, new DBColumn[] { shippingId, productId });
        }

    }

    // orders
    public static class Orders extends DBTable {

        private static final long serialVersionUID = 1L;

        // MEMO 
        // ユーザーはグループを変更可能とするので Orderテーブルにはスナップショットしてその時点のグループIDを保持させる
        // グループ別の店舗宛の注文はサマリーをTrueにする
        
        public final DBTableColumn id;      // 注文ID
        public final DBTableColumn userId;  // ユーザーID
        public final DBTableColumn groupId; // グループID 
        public final DBTableColumn shopId;  // 店舗ID
        public final DBTableColumn amount;  // 合計金額
        public final DBTableColumn geoId;   // 届先ID
        public final DBTableColumn summary; // サマリー  
        public final DBTableColumn cancel;  // キャンセル
        public final DBTableColumn datetime;// 注文日時
//		public final DBTableColumn payment; // 支払方法
        
        public Orders(DBDatabase db) {
            super("orders", db);
            id          = addColumn("id",       DataType.AUTOINC,         0, DataMode.AutoGenerated);
            userId      = addColumn("userId",   DataType.INTEGER,         0, DataMode.NotNull);
            groupId     = addColumn("groupId",  DataType.INTEGER,         0, DataMode.NotNull);
            shopId      = addColumn("shopId",   DataType.TEXT, sizeOfShopId, DataMode.NotNull);
            amount      = addColumn("amount",   DataType.INTEGER,         0, DataMode.NotNull);
            geoId       = addColumn("geoId",    DataType.INTEGER,         0, DataMode.Nullable);
            summary     = addColumn("summary",  DataType.BOOL,            0, DataMode.NotNull,     Boolean.FALSE);
            cancel      = addColumn("cancel",   DataType.BOOL,            0, DataMode.NotNull,     Boolean.FALSE);
            datetime    = addColumn("datetime", DataType.DATETIME,        0, DataMode.Nullable);

            setPrimaryKey(id);

            setCascadeDelete(true);
        }

    }

    // order lines
    public static class OrderLines extends DBTable {

        private static final long serialVersionUID = 1L;

        public final DBTableColumn orderId;   // 注文ID
        public final DBTableColumn no;        // 注文明細ID
        public final DBTableColumn productId; // 商品ID
        public final DBTableColumn quantity;  // 数量
        public final DBTableColumn amount;    // 金額
        public final DBTableColumn datetime;  // 注文明細作成時刻
        public final DBTableColumn cancel;    // キャンセルフラグ
        
        public OrderLines(DBDatabase db) {
            super("orderLines", db);
            orderId   = addColumn("orderId",   DataType.INTEGER,            0, DataMode.NotNull);
            no        = addColumn("no",        DataType.INTEGER,            0, DataMode.NotNull);
            productId = addColumn("productId", DataType.TEXT, sizeOfProductId, DataMode.NotNull);
            quantity  = addColumn("quantity",  DataType.INTEGER,            0, DataMode.NotNull);
            amount    = addColumn("amount",    DataType.INTEGER,            0, DataMode.NotNull);
            datetime  = addColumn("datetime",  DataType.DATETIME,           0, DataMode.NotNull);
            cancel    = addColumn("cancel",    DataType.BOOL,               0, DataMode.NotNull,  Boolean.FALSE);

            setPrimaryKey(orderId, no);
            setCascadeDelete(true);
        }
    }

    // payment
    public static class Payment extends DBTable {

        private static final long serialVersionUID = 1L;
        public final DBTableColumn id;
        public final DBTableColumn name;

        public Payment(DBDatabase db) {
            super("payment", db);
            id = addColumn("id", DataType.AUTOINC, 0, DataMode.AutoGenerated);
            name = addColumn("name", DataType.TEXT, 64, DataMode.Nullable);

            setPrimaryKey(id);
        }

    }

    // geolocation
    public static class GeoLocation extends DBTable {

        private static final long serialVersionUID = 1L;

        public final DBTableColumn id;
        public final DBTableColumn latitude; // 緯度
        public final DBTableColumn longitude; // 経度
        public final DBTableColumn altitude; // 高度
        public final DBTableColumn accuracy; //
        public final DBTableColumn altitudeAccuracy;

        public GeoLocation(DBDatabase db) {
            super("geoLocation", db);
            id        = addColumn("id",       DataType.AUTOINC, 0, DataMode.AutoGenerated);
            latitude  = addColumn("latitude", DataType.FLOAT,   0, DataMode.NotNull);
            longitude = addColumn("longitude",DataType.FLOAT,   0, DataMode.NotNull);
            altitude  = addColumn("altitude", DataType.FLOAT,   0, DataMode.NotNull);
            accuracy  = addColumn("accuracy", DataType.FLOAT,   0, DataMode.NotNull);
            altitudeAccuracy = addColumn("altitudeAccuracy", DataType.FLOAT, 0, DataMode.NotNull);

            setPrimaryKey(id);
        }

    }

    public final Users USERS = new Users(this);
    public final Health HEALTH = new Health(this);
    public final Groups GROUPS = new Groups(this);
    public final Shops SHOPS = new Shops(this);
    public final Contracts CONTRACTS = new Contracts(this);
    public final Products PRODUCTS = new Products(this);
    public final Shipping SHIPPING = new Shipping(this);
    public final ShippingLines SHIPPING_LINES = new ShippingLines(this);
    public final Orders ORDERS = new Orders(this);
    public final OrderLines ORDER_LINES = new OrderLines(this);
    public final Receipts RECEIPTS = new Receipts(this);
    public final ReceiptLines RECEIPT_LINES = new ReceiptLines(this);
    public final Payment PAYMENT = new Payment(this);
    public final GeoLocation GEOLOCATION = new GeoLocation(this);
    public final Notices NOTICES = new Notices(this);
    private String driverClassName;
    private DBDatabaseDriver driver;
    
    /**
     * create new instance.
     */
    public Lunchat() {
        this("org.apache.empire.db.mysql.DBDatabaseDriverMySQL");
        // foreign key
        addRelation(HEALTH.userId.referenceOn(USERS.id));
        addRelation(HEALTH.groupId.referenceOn(GROUPS.id));
        addRelation(NOTICES.userId.referenceOn(USERS.id));
        addRelation(CONTRACTS.groupId.referenceOn(GROUPS.id));
        addRelation(CONTRACTS.shopId.referenceOn(SHOPS.id));
        addRelation(PRODUCTS.refId.referenceOn(PRODUCTS.id));
        addRelation(PRODUCTS.shopId.referenceOn(SHOPS.id));
        addRelation(ORDERS.userId.referenceOn(USERS.id));
        addRelation(ORDERS.shopId.referenceOn(SHOPS.id));
        addRelation(ORDER_LINES.orderId.referenceOn(ORDERS.id));
        addRelation(ORDER_LINES.productId.referenceOn(PRODUCTS.id));
        addRelation(RECEIPTS.shopId.referenceOn(SHOPS.id));
        addRelation(RECEIPTS.groupId.referenceOn(GROUPS.id));
        addRelation(RECEIPTS.orderId.referenceOn(ORDERS.id));
        addRelation(RECEIPTS.userId.referenceOn(USERS.id));
        addRelation(RECEIPT_LINES.productId.referenceOn(PRODUCTS.id));
        addRelation(RECEIPT_LINES.receiptId.referenceOn(RECEIPTS.id));
//        addRelation(SHIPPING_LINES.shippingId.referenceOn(SHIPPING.id));
    }

    /**
     * create new instance.
     * @param driverClassName
     */
    public Lunchat(String driverClassName) {
        this.driverClassName = driverClassName;
    }
    
    private static final String FRESH_LUNCH_SHIBAURA = "fresh-shibaura";
    private static final String FRESH_LUNCH_KAWASAKI = "fresh-kawasaki";
    private static final String TAMAGOYA_OOTAKU = "tamagoya";

    /**
     * main
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 3)
            throw new IllegalArgumentException("less argument! missing databse name and  user and passowrd");

        Lunchat lunchat = new Lunchat();
        lunchat.configureMysql(args[0], args[1], args[2]);
        System.out.printf("finish initialize database");
    }

    /**
     * 
     * @param databaseName
     * @param user
     * @param password
     */
    public void configureMysql(String databaseName, String user, String password) {
        configure("jdbc:log4jdbc:mysql://localhost:3306/" + databaseName, user, password);
    }
    
    /**
     * configure database(drop and create tables). 
     * @param url
     * @param user
     * @param password
     */
    public void configure(String url, String user, String password) {
        boolean drop = Boolean.getBoolean("DropTable");
        drop = true;
        // table list must below order because foreign key relations.
        List<DBTable> tableList = Arrays.asList(HEALTH, SHIPPING_LINES, SHIPPING, ORDER_LINES,
                RECEIPT_LINES, RECEIPTS, ORDERS, PAYMENT, PRODUCTS, CONTRACTS, GROUPS, NOTICES, SHOPS,
                GEOLOCATION, USERS);
        String message = "craete tables...";
        if (drop)
            message = "drop and create tables...";

        logger.info(message);

        DBSQLScript script = new DBSQLScript();
        try (Connection con = DriverManager.getConnection(url, user, password)) {
            con.setAutoCommit(false);

            if (!isOpen())
                open(loadDriver(), con);
            if (drop) {
                for (DBTable table: tableList) {
                    logger.info("table name: {}", table.getName());
                    try {
                        DBCommand command = createCommand();
                        command.select(table.count());
                        if (querySingleInt(command, -1, con) != -1)
                            loadDriver().getDDLScript(DBCmdType.DROP, table, script);
                    } catch (QueryFailedException e) {
                        // logging in empire-db already.
//                        logger.error("query error: {}", e.getLocalizedMessage());
                    } catch (Exception e) {
                        logger.error("exception: ", e);
                    }
                }
                logger.info("script is {}", script.toString());
                script.run(loadDriver(), con);
            }

            // create tables
            script.clear();
            getCreateDDLScript(loadDriver(), script);
            script.run(loadDriver(), con);

            // init records
            initShopsRecord(getShops(), con);
            initProductRecord(getProducts(), con);

            // commit
            commit(con);
        } catch (SQLException e) {
            throw new LunchException("script: " + script, e);
        }
    }
    
    
    /**
     * exists table
     * @param tableName
     * @param con
     * @return
     */
    public boolean existsTable(String tableName, Connection con) {
        if (!isOpen()) {
            open(loadDriver(), con);
        }
        DBTable table = getTable(tableName);
        if (table == null)
            return false;
        DBCommand command = createCommand();
        command.select(table.count()); // select count(*) from tableName
        try {
            return querySingleInt(command, -1, con) >= 0;
        } catch (QueryFailedException e) {
            throw new LunchException(e);
        }
    }
       
    /**
     * exists tables in the database.
     * @param con
     * @return
     */
    public boolean existsTables(Connection con) {
        for (DBTable table: getTables()) {
            if (!existsTable(table.getName(), con)) {
                logger.error("table not found: {}", table.getFullName());
                return false;
            }
        }
        // exists all tables.
        return true;
    }
    
    /**
     * load database driver for empire-db.
     * @return
     */
    protected DBDatabaseDriver loadDriver() {
        if (this.driver != null)
            return driver;
        try {
            driver = (DBDatabaseDriver) Class.forName(getDriverClassName()).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new LunchException(e);
        }
        return driver;
    }
    
    /**
     * get driver class name.
     * @return
     */
    private String getDriverClassName() {
        return driverClassName;
    }
    
    private void initShopsRecord(List<Shop> shops, Connection con) {
        for (Shop shop: shops) {
            DBRecord record = new DBRecord();
            record.create(SHOPS);
            record.setValue(SHOPS.id, shop.getId());
            record.setValue(SHOPS.name, shop.getName());
            record.setValue(SHOPS.phoneNumber, shop.getPhoneNumber());
            record.setValue(SHOPS.url, shop.getUrl());
            record.setValue(SHOPS.lastOrder, shop.getLastOrder());
            record.update(con);
        }
    }

    private void initProductRecord(List<Product> products, Connection con) {
        for (Product p: products) {
            DBRecord record = new DBRecord();
            record.create(PRODUCTS);
            record.setValue(PRODUCTS.id, p.getId());
            record.setValue(PRODUCTS.refId, p.getRefId());
            record.setValue(PRODUCTS.name, p.getName());
            record.setValue(PRODUCTS.amount, p.getAmount());
            record.setValue(PRODUCTS.shopId, p.getShopId());
            record.setValue(PRODUCTS.start, p.getStart());
            record.setValue(PRODUCTS.finish, p.getFinish());
            record.update(con);
        }
    }

    private List<Shop> getShops() {
        List<Shop> shops = new ArrayList<Shop>();
        String format = "yyyy/MM/dd HH:mm:ss.SSS";
        String lastOrderTamagoya = "2011/12/01 10:00:00.000";
        String lastOrderFresh = "2011/12/01 09:30:00.000";
        try {
            shops.add(new Shop(TAMAGOYA_OOTAKU).setName("玉子屋").setPhoneNumber("03-3754-6167")
                    .setUrl("http://www.tamagoya.co.jp/menu/menu.html")
                    .setLastOrder(DateUtils.parseDate(lastOrderTamagoya, format)));
            shops.add(new Shop(FRESH_LUNCH_SHIBAURA).setName("フレッシュランチ 芝浦店")
                    .setPhoneNumber("03-5769-0339").setUrl("http://www.fl39.com/month_menu")
                    .setLastOrder(DateUtils.parseDate(lastOrderFresh, format)));
            shops.add(new Shop(FRESH_LUNCH_KAWASAKI).setName("フレッシュランチ 川崎店")
                    .setPhoneNumber("044-281-2011").setUrl("http://www.fl39.com/month_menu")
                    .setLastOrder(DateUtils.parseDate(lastOrderFresh, format)));
        } catch (ParseException e) {
            throw new LunchException(e);
        }
        return shops;
    }

    private List<Product> getProducts() {
        List<Product> list = new ArrayList<>();
        Calendar start = Calendar.getInstance();
        Calendar finish = (Calendar) start.clone();
        finish.set(Calendar.SECOND, 0);
        finish.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 7; i++) {
            finish.add(Calendar.DATE, 1);
            if (finish.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
                    && finish.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                list.addAll(getTamagoyaProducts(start, finish));
                list.addAll(getFreshLunchProducts(start, finish));
            }
        }
        return list;
    }

    public static List<Product> getTamagoyaProducts(Calendar start, Calendar finish) {
        List<Product> products = new ArrayList<>();
        String idFormat = "tamagoya%tm%td";
        // set deadline time
        finish.set(Calendar.HOUR_OF_DAY, 10);
        finish.set(Calendar.MINUTE, 0);
        finish.set(Calendar.SECOND, 0);
        finish.set(Calendar.MILLISECOND, 0);
        String finishDate = DateFormatUtils.format(finish, "M/d(EEE)");
        products.add(new Product(String.format(idFormat + "-001", finish.getTime(),
                finish.getTime())).setShop(new Shop(TAMAGOYA_OOTAKU)).setRefId(null)
                .setName(finishDate + " のお弁当").setAmount(430)
                .setStart(start.getTime()).setFinish(finish.getTime()));
        products.add(new Product(String.format(idFormat + "-002", finish, finish))
                .setShop(new Shop(TAMAGOYA_OOTAKU)).setRefId(null)
                .setName(finishDate + " のお弁当（小）").setAmount(400)
                .setStart(start.getTime()).setFinish(finish.getTime()));
        products.add(new Product(String.format(idFormat + "-003", finish, finish))
                .setShop(new Shop(TAMAGOYA_OOTAKU)).setRefId(null)
                .setName(finishDate + " のおかず").setAmount(380)
                .setStart(start.getTime()).setFinish(finish.getTime()));
        return products;
    }

    public static List<Product> getFreshLunchProducts(Calendar start, Calendar finish) {
        List<Product> products = new ArrayList<>();
        String idFormat = "fresh%tm%td";
        // set deadline time
        finish.set(Calendar.HOUR_OF_DAY, 9);
        finish.set(Calendar.MINUTE, 30);
        finish.set(Calendar.SECOND, 0);
        finish.set(Calendar.MILLISECOND, 0);
        String finishDate = DateFormatUtils.format(finish, "M/d(EEE)");
        products.add(new Product(String.format(idFormat + "-001", finish, finish)).setRefId(null)
                .setName(finishDate + " のお弁当").setAmount(430)
                .setShop(new Shop(FRESH_LUNCH_SHIBAURA)).setStart(start.getTime())
                .setFinish(finish.getTime()));
//		products.add(new Product("")
//						.setRefId(null)
//						.setName(String.format("%tm/%td のお弁当", finishDate, finishDate))
//						.setAmount(430)
//						.setShopId("freshLunch")
//						.setStart(startDate)
//						.setFinish(finishDate));
        return products;
    }
}
