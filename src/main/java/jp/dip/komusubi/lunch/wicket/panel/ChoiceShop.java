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
package jp.dip.komusubi.lunch.wicket.panel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jp.dip.komusubi.lunch.Configuration;
import jp.dip.komusubi.lunch.model.Product;
import jp.dip.komusubi.lunch.model.Shop;
import jp.dip.komusubi.lunch.model.User;
import jp.dip.komusubi.lunch.module.Basket;
import jp.dip.komusubi.lunch.service.AccountService;
import jp.dip.komusubi.lunch.service.Shopping;
import jp.dip.komusubi.lunch.wicket.WicketSession;
import jp.dip.komusubi.lunch.wicket.page.Confirm;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.komusubi.common.util.Resolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * choice shop panel.
 * 
 * @author jun.ozeki
 * @since 2011/11/16
 */
public class ChoiceShop extends Panel {

	private static final long serialVersionUID = -4318541322169808309L;
	private static final Logger logger = LoggerFactory.getLogger(ChoiceShop.class);

	// @Inject
	// private transient final Shopping shopping;
	// @Inject
	// private final ShopDao shopDao;

	public ChoiceShop(String id, boolean forward) {
		super(id);
		add(new Choice("choice", forward));
	}

	public ChoiceShop(String id) {
		this(id, false);
	}
	
	/**
	 * choice from.
	 * 
	 * @author jun.ozeki
	 */
	public static class Choice extends Form<Void> {

		private static final long serialVersionUID = 4242361361989135612L;
//		private transient Shopping shopping = Configuration.getInstance(Shopping.class);
//		@Inject
//		private transient ShopDao shopDao;
		@Inject @Named("calendar") Resolver<Calendar> calendarResolver;
		private LoadableDetachableModel<List<Object>> ldmodel = new LoadableDetachableModel<List<Object>>() {
			private static final long serialVersionUID = 3385439610274972123L;
			private static final int SEEK_PERIOD = 14;
			private static final long MILLISECONDS_DAY = 60 * 60 * 24 * 1000;
			
			@Override
			public List<Object> load() {
				Shopping shopping = Configuration.getInstance(Shopping.class);
				AccountService accountService = Configuration.getInstance(AccountService.class);
				List<Shop> shops = null;
				Calendar current = calendarResolver.resolve();
				if (WicketSession.get().isSignedIn()) {
					User user = WicketSession.get().getLoggedInUser();
					shops = accountService.getContractedShops(user);
    			} else {
    				shops = shopping.getAvailableShops();
    			}
				
				List<Object> itemValues = new ArrayList<>();
				Calendar todayLimit = calendarResolver.resolve();
				todayLimit.set(Calendar.HOUR_OF_DAY, 13);
				todayLimit.set(Calendar.MINUTE, 0);
				todayLimit.set(Calendar.SECOND, 0);
				todayLimit.set(Calendar.MILLISECOND, 0);
				for (Shop shop: shops) {
					boolean found = false;
					itemValues.add(shop);
					if (current.before(todayLimit)) {
						for (Product product: shopping.getDeadlineProducts(shop.getId())) {
							if (product.getFinish().before(current.getTime())) {
								itemValues.add(product);
								found = true;
							}
						}
						if (!found) {
							// FIXME string go to resource file.
							String lastOrderDate = DateFormatUtils.format(current, "MM/dd(EEE)");
							itemValues.add(new Product("dummy")
											.setName(shop.getName() + " は " + lastOrderDate + "の注文の受付を終了しました。")
											.setAmount(0)
											.setShop(shop));
						}
					} else {
						for (Calendar orderCalendar = (Calendar) current.clone(); 
								(orderCalendar.getTimeInMillis() - current.getTimeInMillis()) / MILLISECONDS_DAY <= SEEK_PERIOD;
								orderCalendar.add(Calendar.DATE, 1)) {
							for (Product product: shopping.getDeadlineProducts(shop.getId(), orderCalendar.getTime())) {
								itemValues.add(product);
								found = true;
							}
							if (found)
								break;
						}
						if (!found) { 
//							throw new IllegalStateException("not found product over the period days:"  + SEEK_PERIOD);
//							String lastOrderDate = DateFormatUtils.format(current, "MM/dd(EEE)");
							itemValues.add(new Product("dummy")
											.setName(shop.getName() + " の商品は見つかりませんでした。")
											.setAmount(0)
											.setShop(shop));
						}
					}
				}
				
				return itemValues;
			}
		};

		public Choice(String id, boolean forward) {
			super(id);
			add(getProductList("shop.list"));
			// .setReuseItems(true));
		}

//		public Choice(String id, ShopDao shopDao) {
//			this(id, null);
//			this.shopDao = shopDao;
//			throw new UnsupportedOperationException("choice constructor.");
//		}

		private ListView<Object> getProductList(String id) {

			return new ListView<Object>(id, ldmodel) {

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
		 * 
		 * @param id
		 * @param itemValue
		 * @return
		 */
		private Component getLinkOfItem(String id, final Object itemValue) {
			Link<String> link;
			if (itemValue instanceof Shop) {
				link = new Link<String>(id) {
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
						Shopping shopping = Configuration.getInstance(Shopping.class);
						Basket basket = shopping.getBasket(WicketSession.get().getLoggedInUser());
						basket.add(product);
//						try {
//							shopping.order(true);
//						} catch (DuplicatedOrderException e) {
//							// FIXME message
//							error("duplicate order " + e.getDuplictedOrders().get(0));
//							setResponsePage(new Notice(shopping, e.getDuplictedOrders()));
//							return;
//						}
						setResponsePage(new Confirm(basket));
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
		 * 
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
		 * 
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
				label = new Label(id);
				label.setVisible(false);
			} else {
				throw new IllegalStateException("itemValue dosen't object Shop or Product");
			}
			if (WicketSession.VARIATION_JQUERY_MOBILE.equals(getVariation()))
				label.add(new AttributeModifier("class", "ui-li-aside"));
			return label;
		}
	}
}
