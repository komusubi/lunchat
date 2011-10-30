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
package jp.dip.komusubi.lunch.wicket.panel;

import java.util.ArrayList;
import java.util.List;

import jp.dip.komusubi.lunch.Configuration;
import jp.dip.komusubi.lunch.model.Product;
import jp.dip.komusubi.lunch.model.Shop;
import jp.dip.komusubi.lunch.module.dao.ShopDao;
import jp.dip.komusubi.lunch.service.DuplicatedOrderException;
import jp.dip.komusubi.lunch.service.Shopping;
import jp.dip.komusubi.lunch.wicket.WicketSession;
import jp.dip.komusubi.lunch.wicket.page.CompleteOrder;
import jp.dip.komusubi.lunch.wicket.page.Login;
import jp.dip.komusubi.lunch.wicket.page.Notice;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class ChoiceShop extends Panel {

	private static final long serialVersionUID = -4318541322169808309L;
	private static final Logger logger = LoggerFactory.getLogger(ChoiceShop.class);
//	@Inject
//	private transient final Shopping shopping;
//	@Inject
//	private final ShopDao shopDao;
	
	public ChoiceShop(String id) {
		super(id);
		add(new Choice("choice"));
	}
	
	/**
	 * choice from.
	 * @author jun.ozeki
	 */
	public static class Choice extends Form<Void> {

		private static final long serialVersionUID = 4242361361989135612L;
		private transient Shopping shopping = Configuration.getInstance(Shopping.class);
		@Inject
		private transient ShopDao shopDao;

		
		public Choice(String id) {
			super(id);
			add(getProductList("shop.list"));
//					.setReuseItems(true));
		}
		
		public Choice(String id, ShopDao shopDao) {
			this(id);
			this.shopDao = shopDao;
		}
		
		@Override
		public void onBeforeRender() {
			if (WicketSession.VARIATION_JQUERY_MOBILE.equals(getVariation())) 
				add(new AttributeModifier("data-ajax", false));
			
			super.onBeforeRender();
		}
		
		private ListView<Object> getProductList(String id) {
			// TODO load method ? 確認
			List<Object> itemValues = new ArrayList<Object>();
			for (Shop shop: shopDao.findAll()) {
				itemValues.add(shop);
				for (Product product: shopping.getProducts(shop.getId())) {
					itemValues.add(product);
				}
			}
			
			return new ListView<Object>(id, itemValues) {

				private static final long serialVersionUID = -502489393499906175L;

				@Override
				protected void populateItem(ListItem<Object> item) {
					Object itemValue = item.getModelObject();
					if (itemValue instanceof Shop)
						item.add(new AttributeModifier("data-role", "list-divider"));
					else
						item.add(AttributeModifier.remove("data-role"));
					item.add(getLinkOfItem("item.link", itemValue));
					item.add(getLabelOfItem("item.name", itemValue));
					item.add(getLabelOfAmount("amount.label", itemValue));
				}
			};
		}
		/**
		 * リンク.
		 * @param id
		 * @param itemValue
		 * @return
		 */
		private Component getLinkOfItem(String id, final Object itemValue) {
			Link<String> link;
			if (itemValue instanceof Shop) {
				link= new Link<String>(id) {
					private static final long serialVersionUID = 1L;

					@Override 
					public void onClick() {
						return;
					}
					@Override
					public boolean isVisible() {
						return false; 
					}
				};
				link.add(new Label("link.name"));
				link.add(new Label("amount.value"));
			} else if (itemValue instanceof Product) {
				final Product product = (Product) itemValue;
				link = new Link<String>(id) {
	
					private static final long serialVersionUID = 3680360043205992841L;
	
					@Override
					public void onClick() {
						if (!WicketSession.get().isSignedIn()) {
							setResponsePage(Login.class);
						} else {
                        shopping.setUser(WicketSession.get().getLoggedInUser());
                        shopping.add(product);
                        try {
                            shopping.order(true);
                        } catch (DuplicatedOrderException e) {
                            // FIXME message
                            error("duplicate order " + e.getDuplictedOrders().get(0));
                            setResponsePage(new Notice(shopping, e.getDuplictedOrders()));
                            return;
                            }
                        setResponsePage(new CompleteOrder(shopping));
						}
					}
				};
				link.add(new Label("link.name", product.getName()));
				// TODO literal to property resource.
				link.add(new Label("amount.value", String.valueOf(product.getAmount()) + "円"));
			} else {
				throw new IllegalStateException("itemValue dosen't object Shop or Product");
			}
			
			return link;
		}
		/**
		 * 商品名表示.
		 * @param id
		 * @param itemValue
		 * @return
		 */
		private Label getLabelOfItem(String id, final Object itemValue) {
			Label label;
			if (itemValue instanceof Shop) {
				Shop shop = (Shop) itemValue;
				label = new Label(id, shop.getName());
			} else if (itemValue instanceof Product) {
				label = new Label(id);
				label.setVisible(false);
			} else {
				throw new IllegalStateException("itemValue dosen't object Shop or Product");
			}
			
			return label;
		}
		/**
		 * 金額表示.
		 * @param id
		 * @param itemValue
		 * @return
		 */
		private Label getLabelOfAmount(String id, final Object itemValue) {
			Label label;
			if (itemValue instanceof Shop) {
				// TODO property resource 
				label = new Label(id, "料金");
			} else if (itemValue instanceof Product) {
//				Product product = (Product) itemValue;
//				label = new Label(id, String.valueOf(product.getAmount()) + "円");
				label = new Label(id);
				label.setVisible(false);
			} else {
				throw new IllegalStateException("itemValue dosen't object Shop or Product");
			}
			if (WicketSession.VARIATION_JQUERY_MOBILE.equals(getVariation()))
				label.add(new AttributeModifier("class", "ui-li-aside"));
			return label;
		}
		
//		private ListView<Shop> getShopList(String id) {
//			List<Shop> list = shopDao.findAll();
//			return new ListView<Shop>(id, list) {
//
//				private static final long serialVersionUID = -319196005747155024L;
//
//				@Override
//				protected void populateItem(ListItem<Shop> item) {
//					Shop shop = item.getModelObject();
//					item.add(getButtonOfShop("shop.name", shop));
//				}
//			};
//		}
//		private Button getButtonOfShop(String id, final Shop shop) {
//			Button button = new Button(id) {
//				private static final long serialVersionUID = -9026265736216819271L;
//				
//				@Override
//				public void onSubmit() {
//					if (!WicketSession.get().isSignedIn())
//						setResponsePage(Login.class);
//					else {
//						shopping.add(shop.getName());
//						setResponsePage(new CompleteOrder(shop));
//					}
//				}
//			};
//			button.add(new AttributeModifier("value", shop.getName()));
//			return button;
//		}
	}
}
