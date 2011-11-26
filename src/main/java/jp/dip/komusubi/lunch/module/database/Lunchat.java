/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
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
import java.util.List;

import jp.dip.komusubi.lunch.LunchException;
import jp.dip.komusubi.lunch.model.Shop;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.empire.data.DataMode;
import org.apache.empire.data.DataType;
import org.apache.empire.db.DBCmdType;
import org.apache.empire.db.DBColumn;
import org.apache.empire.db.DBCommand;
import org.apache.empire.db.DBDatabase;
import org.apache.empire.db.DBRecord;
import org.apache.empire.db.DBSQLScript;
import org.apache.empire.db.DBTable;
import org.apache.empire.db.DBTableColumn;
import org.apache.empire.db.exceptions.QueryFailedException;
import org.apache.empire.db.mysql.DBDatabaseDriverMySQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lunchat extends DBDatabase {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(Lunchat.class);
	private static final int sizeOfUserId = 32;
	private static final int sizeOfShopId = 16;
	private static final int sizeOfProductId = 32;
	private static final int sizeOfGroupId = 32;


	// users
	public static class Users extends DBTable {

		private static final long serialVersionUID = 1L;

		public final DBTableColumn id;
		public final DBTableColumn groupId;
		public final DBTableColumn password;
		public final DBTableColumn name;
		public final DBTableColumn email;

		public Users(DBDatabase db) {
			super("Users", db);
			id 			= addColumn("id", 		DataType.TEXT,  sizeOfUserId, DataMode.NotNull);
			groupId 	= addColumn("groupId", 	DataType.TEXT, sizeOfGroupId, DataMode.Nullable);
			password 	= addColumn("password", DataType.TEXT,  		  64, DataMode.NotNull);
			name		= addColumn("name", 	DataType.TEXT, 			 128, DataMode.NotNull);
			email 		= addColumn("email", 	DataType.TEXT, 			 255, DataMode.NotNull);

			addIndex("email_idx", true, new DBTableColumn[]{email});

			setPrimaryKey(id);
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
		public final DBTableColumn active;

		public Health(DBDatabase db) {
			super("Health", db);
			userId 		= addColumn("userId",	 DataType.TEXT, 	sizeOfUserId, 	DataMode.NotNull);
			login 		= addColumn("login", 	 DataType.INTEGER, 			   0, 	DataMode.NotNull);
			lastLogin 	= addColumn("lastLogin", DataType.DATETIME, 		   0, 	DataMode.Nullable);
			loginFail 	= addColumn("loginFail", DataType.INTEGER, 			   0, 	DataMode.NotNull);
			active 		= addColumn("active", 	 DataType.BOOL, 			   0, 	DataMode.NotNull);

			setPrimaryKey(userId);
		}
	}

	// groups
	public static class Groups extends DBTable {

		private static final long serialVersionUID = 1L;

		public final DBTableColumn id;
		public final DBTableColumn geoId;
		public final DBTableColumn name;
		public final DBTableColumn lastOrder;
		public final DBTableColumn place;
		public final DBTableColumn tel;

		public Groups(DBDatabase db) {
			super("Groups", db);
			id 			= addColumn("id", 		 DataType.TEXT, 	sizeOfGroupId, 	DataMode.NotNull);
			geoId		= addColumn("geoId", 	 DataType.INTEGER, 	   			0, 	DataMode.Nullable);
			name 		= addColumn("name", 	 DataType.TEXT, 			  128, 	DataMode.NotNull);
			lastOrder 	= addColumn("lastOrder", DataType.DATETIME,	  			0, 	DataMode.NotNull);
			place       = addColumn("place", 	 DataType.TEXT,	    		  255, 	DataMode.NotNull);
			tel         = addColumn("tel", 	 	 DataType.TEXT,	 	   		   64, 	DataMode.NotNull);

			setPrimaryKey(id);
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
			super("Shops", db);
			id 		  =	addColumn("id", 		DataType.TEXT, sizeOfShopId, DataMode.NotNull);
			name 	  =	addColumn("name", 		DataType.TEXT, 		    255, DataMode.NotNull);
			geoId 	  = addColumn("geoId", 		DataType.INTEGER,		  0, DataMode.Nullable);
			url	 	  =	addColumn("url", 		DataType.TEXT, 	   		255, DataMode.Nullable);
			phoneNumber=addColumn("phoneNumber",DataType.TEXT, 			 64, DataMode.Nullable);
			// 店舗単位でもラストオーダー(受注の締切り時刻があっても良いはず)追加
			lastOrder = addColumn("lastOrder",  DataType.DATETIME,  	  0, DataMode.NotNull);

			setPrimaryKey(id);
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
			super("Products", db);
			id = 	addColumn("id", 	DataType.TEXT, 		sizeOfProductId,	DataMode.NotNull);
			refId = addColumn("refId", 	DataType.TEXT,		sizeOfProductId,	DataMode.Nullable);
			shopId =addColumn("shopId", DataType.TEXT, 		   sizeOfShopId, 	DataMode.NotNull);
			name = 	addColumn("name", 	DataType.TEXT, 	   				255, 	DataMode.NotNull);
			amount =addColumn("amount", DataType.INTEGER, 	 			  0, 	DataMode.NotNull);
			start = addColumn("start", 	DataType.DATETIME,	 			  0, 	DataMode.Nullable);
			finish =addColumn("finish", DataType.DATETIME,   			  0, 	DataMode.Nullable);

			setPrimaryKey(id);
			setCascadeDelete(true);
		}

	}

	// receiving
	public static class Receiving extends DBTable {

		private static final long serialVersionUID = 1L;

		public final DBTableColumn id;
		public final DBTableColumn orderId;
		public final DBTableColumn groupId;
		public final DBTableColumn shopId;
		public final DBTableColumn productId;
		public final DBTableColumn quantity;
		public final DBTableColumn amount;
		public final DBTableColumn datetime;

		public Receiving(DBDatabase db) {
			super("Receiving", db);
			id       =	addColumn("id", 		DataType.AUTOINC, 				  0, DataMode.AutoGenerated);
			orderId  =	addColumn("orderId", 	DataType.INTEGER, 			      0, DataMode.NotNull);
			groupId  =  addColumn("groupId", 	DataType.TEXT, 	      sizeOfGroupId, DataMode.NotNull);
			shopId   = 	addColumn("shopId", 	DataType.TEXT, 		   sizeOfShopId, DataMode.NotNull);
			productId=  addColumn("productId", 	DataType.TEXT, 		sizeOfProductId, DataMode.NotNull);
			quantity = 	addColumn("quantity", 	DataType.INTEGER, 				  0, DataMode.NotNull);
			amount   = 	addColumn("amount", 	DataType.INTEGER,   			  0, DataMode.NotNull);
			datetime = 	addColumn("datetime", 	DataType.DATETIME, 				  0, DataMode.NotNull);
			
			setPrimaryKey(id);
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
			super("Shipping", db);
			id       =	addColumn("id", 		DataType.AUTOINC, 				  0, DataMode.AutoGenerated);
			orderId =   addColumn("orderId", 	DataType.INTEGER,   			  0, DataMode.NotNull);
			departure=  addColumn("departure", 	DataType.INTEGER,     			  0, DataMode.Nullable);
			arrival  =  addColumn("arrival", 	DataType.INTEGER,     			  0, DataMode.Nullable);
			datetime = 	addColumn("datetime", 	DataType.DATETIME, 				  0, DataMode.NotNull);

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
			super("ShippingLines", db);

			shippingId  = addColumn("shippingId",	DataType.INTEGER, 				  0, DataMode.NotNull);
			productId	= addColumn("productId", 	DataType.TEXT, 		sizeOfProductId, DataMode.NotNull);
			quantity 	= addColumn("quantity", 	DataType.INTEGER, 				  0, DataMode.NotNull);
			amount   	= addColumn("amount", 		DataType.INTEGER,   			  0, DataMode.NotNull);
//			datetime 	= addColumn("datetime", 	DataType.DATETIME, 				  0, DataMode.NotNull);

			addIndex("shippingLines_idx", true, new DBColumn[]{shippingId, productId});
		}

	}

	// orders
	public static class Orders extends DBTable {

		private static final long serialVersionUID = 1L;

		public final DBTableColumn id;		// 注文ID
		public final DBTableColumn userId;	// ユーザー
		public final DBTableColumn shopId;	// 店舗ID
		public final DBTableColumn amount;	// 合計金額
		public final DBTableColumn geoId;   // 届先ID
		public final DBTableColumn datetime;// 注文日時
//		public final DBTableColumn payment; // 支払方法
		
		public Orders(DBDatabase db) {
			super("Orders", db);
			id       = 	addColumn("id", 		DataType.AUTOINC,  			   0, 	DataMode.AutoGenerated);
			userId   =  addColumn("userId", 	DataType.TEXT, 	   sizeOfUserId,  	DataMode.NotNull);
			shopId   =  addColumn("shopId", 	DataType.TEXT,      sizeOfShopId, 	DataMode.NotNull);
			amount   = 	addColumn("amount",  	DataType.INTEGER,			   0,	DataMode.NotNull);
			geoId	 =  addColumn("geoId",		DataType.INTEGER,			   0,  	DataMode.Nullable);
			datetime =  addColumn("datetime", 	DataType.DATETIME, 		   	   0, 	DataMode.Nullable);

			setPrimaryKey(id);

			setCascadeDelete(true);
		}

	}

	// order lines
	public static class OrderLines extends DBTable {

		private static final long serialVersionUID = 1L;

		public final DBTableColumn id;			// 注文明細ID
		public final DBTableColumn orderId; 	// 注文ID
		public final DBTableColumn productId;	// 商品ID
		public final DBTableColumn quantity;	// 数量
		public final DBTableColumn amount;		// 金額
		public final DBTableColumn datetime;	// 注文明細作成時刻
		
		public OrderLines(DBDatabase db) {
			super("OrderLines", db);
			id		=   addColumn("id", 		DataType.AUTOINC,  				 0, DataMode.AutoGenerated);
			orderId =   addColumn("orderId", 	DataType.INTEGER,  				 0, DataMode.NotNull);
			productId = addColumn("productId", 	DataType.TEXT,     sizeOfProductId, DataMode.NotNull);
			quantity =  addColumn("quantity", 	DataType.INTEGER,  				 0, DataMode.NotNull);
			amount = 	addColumn("amount", 	DataType.INTEGER,  			     0, DataMode.NotNull);
			datetime = 	addColumn("datetime", 	DataType.DATETIME,				 0, DataMode.NotNull);
			
			setPrimaryKey(id, orderId);
			setCascadeDelete(true);
		}
	}

	// payment
	public static class Payment extends DBTable {

		private static final long serialVersionUID = 1L;
		public final DBTableColumn id;
		public final DBTableColumn name;
		
		public Payment(DBDatabase db) {
			super("Payment", db);
			id = addColumn("id", DataType.AUTOINC, 0, DataMode.AutoGenerated);
			name = addColumn("name", DataType.TEXT, 64, DataMode.Nullable);
			
			setPrimaryKey(id);
		}

	}

	// geolocation
	public static class GeoLocation extends DBTable {

		private static final long serialVersionUID = 1L;

		public final DBTableColumn id;
		public final DBTableColumn latitude;  // 緯度
		public final DBTableColumn longitude; // 経度
		public final DBTableColumn altitude;  // 高度
		public final DBTableColumn accuracy;  //
		public final DBTableColumn altitudeAccuracy;

		public GeoLocation(DBDatabase db) {
			super("GeoLocation", db);
			id  			 = addColumn("id", 				 DataType.AUTOINC,  0, DataMode.AutoGenerated);
			latitude 	 	 = addColumn("latitude", 		 DataType.FLOAT, 	0, DataMode.NotNull);
			longitude 		 = addColumn("longitude", 		 DataType.FLOAT, 	0, DataMode.NotNull);
			altitude 		 = addColumn("altitude", 		 DataType.FLOAT, 	0, DataMode.NotNull);
			accuracy 		 = addColumn("accuracy", 		 DataType.FLOAT, 	0, DataMode.NotNull);
			altitudeAccuracy = addColumn("altitudeAccuracy", DataType.FLOAT, 	0, DataMode.NotNull);

			setPrimaryKey(id);
		}

	}

	public final Users USERS = new Users(this);
	public final Health HEALTH = new Health(this);
	public final Groups GROUPS = new Groups(this);
	public final Shops SHOPS = new Shops(this);
	public final Products PRODUCTS = new Products(this);
	public final Receiving RECEIVING = new Receiving(this);
	public final Shipping SHIPPING = new Shipping(this);
	public final ShippingLines SHIPPING_LINES = new ShippingLines(this);
	public final Orders ORDERS = new Orders(this);
	public final OrderLines ORDER_LINES = new OrderLines(this);
	public final Payment PAYMENT = new Payment(this);
	public final GeoLocation GEOLOCATION = new GeoLocation(this);
	
	public Lunchat() {
		// foreign key
		addRelation(HEALTH.userId.referenceOn(USERS.id));
		addRelation(USERS.groupId.referenceOn(GROUPS.id));
		addRelation(PRODUCTS.refId.referenceOn(PRODUCTS.id));
		addRelation(PRODUCTS.shopId.referenceOn(SHOPS.id));
//		addRelation(ORDERS.groupId.referenceOn(GROUPS.id));
		addRelation(ORDER_LINES.orderId.referenceOn(ORDERS.id));
//		addRelation(ORDER_LINES.userId.referenceOn(USERS.id));
		addRelation(ORDER_LINES.productId.referenceOn(PRODUCTS.id));
		addRelation(SHIPPING_LINES.shippingId.referenceOn(SHIPPING.id));
	}
	
	public static void main(String[] args) {
		Lunchat lunchat = new Lunchat();
		lunchat.configure();
	}
	
	private void configure() {
		boolean drop = Boolean.getBoolean("DropTable");
		drop = true;
		// table list must below order because foreign key relations. 
		List<DBTable> tableList = Arrays.asList(
									HEALTH,
									SHIPPING_LINES,
									SHIPPING,
									ORDER_LINES,
									ORDERS,
									RECEIVING,
									PAYMENT,
									PRODUCTS,
									USERS,
									GROUPS,
									SHOPS,
									GEOLOCATION
									);
		String message = "craete tables...";
		if (drop)
			message = "drop and create tables...";

		logger.info(message);
		
		DBDatabaseDriverMySQL driver = new DBDatabaseDriverMySQL();
		DBSQLScript script = new DBSQLScript();
		try (Connection con 
				= DriverManager.getConnection("jdbc:log4jdbc:mysql://localhost:3306/lunchat", "root", "5150oozeki")) {
			con.setAutoCommit(false);

			open(driver, con);
			if (drop) {
				for (DBTable table: tableList) {
					try {
						DBCommand command = createCommand();
						command.select(table.count());
						if (querySingleInt(command.getSelect(), -1, con) != -1)
							driver.getDDLScript(DBCmdType.DROP, table, script);
					} catch (QueryFailedException e) {
						logger.error("query error: ", e);
					} catch (Exception e) {
						logger.error("exception: ", e);
					}
				}
				script.run(driver, con);
			}
			
			// create tables
			script.clear();
			getCreateDDLScript(driver, script);
			script.run(driver, con);
			
			// init records
			initShopsRecord(getShops(), con);
			
			// commit
			commit(con);
		} catch (SQLException e) {
			throw new LunchException("script: " + script, e);
		}
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
	private List<Shop> getShops() {
		List<Shop> shops = new ArrayList<Shop>();
		String format = "yyyy/MM/dd HH:mm:ss.SSS";
		String lastOrder = "2011/12/01 10:00:00.000";
		try {
			shops.add(new Shop("tamagoya")
						.setName("玉子屋")
						.setPhoneNumber("03-3754-6167")
						.setUrl("http://www.tamagoya.co.jp/menu/menu.html")
						.setLastOrder(DateUtils.parseDate(lastOrder, format)));
			shops.add(new Shop("fresh")
						.setName("フレッシュランチ")
						.setPhoneNumber("03-5769-0339")
						.setUrl("http://www.fl39.com/month_menu")
						.setLastOrder(DateUtils.parseDate(lastOrder, format)));
		} catch (ParseException e) {
			throw new LunchException(e);
		}
		return shops;
	}
}
