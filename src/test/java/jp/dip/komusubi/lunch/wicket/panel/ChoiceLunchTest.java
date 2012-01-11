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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Provider;

import jp.dip.komusubi.lunch.model.Product;
import jp.dip.komusubi.lunch.model.Shop;
import jp.dip.komusubi.lunch.module.dao.MockOrderDao;
import jp.dip.komusubi.lunch.module.dao.MockProductDao;
import jp.dip.komusubi.lunch.module.dao.MockShopDao;
import jp.dip.komusubi.lunch.module.dao.OrderDao;
import jp.dip.komusubi.lunch.module.dao.ProductDao;
import jp.dip.komusubi.lunch.module.dao.ShopDao;
import jp.dip.komusubi.lunch.module.resolver.DateResolver;
import jp.dip.komusubi.lunch.service.Shopping;
import jp.dip.komusubi.lunch.wicket.WicketTesterResource;
import jp.dip.komusubi.lunch.wicket.panel.ChoiceLunch.Choice;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.komusubi.common.util.Resolver;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

public class ChoiceLunchTest {

//	private static final Logger logger = LoggerFactory.getLogger(ChoiceLunchTest.class);
	private static final String[] parseFormats = {"HH:mm", "yyyy/MM/dd HH:mm"};
	private static List<Shop> shopsAll = new ArrayList<>();
	private static List<Product> productsAll = new ArrayList<>();
	private Calendar orderCalendar;
	
	private Resolver<Injector> injectBuilder = new Resolver<Injector>() {
		
		public Injector resolve() {
			return Guice.createInjector(new AbstractModule() {
				
				@Override
				protected void configure() {
					bind(new TypeLiteral<Resolver<Calendar>>(){ })
					.annotatedWith(Names.named("calendar")).toInstance(newCalendarResolver());
					bind(new TypeLiteral<Resolver<Date>>(){ })
					.annotatedWith(Names.named("date")).toInstance(new DateResolver());
					bind(Shopping.class);
//					bind(ProductDao.class).toInstance(newProductDao());
					bind(ProductDao.class).toProvider(ProductDaoProvider.class);
					bind(ShopDao.class).toProvider(ShopDaoProvider.class);
					bind(OrderDao.class).to(MockOrderDao.class);

				}
			});
		}
		
		public Injector resolve(Injector injector) {
			return injector;
		}
	};
	
	private static class ProductDaoProvider implements Provider<ProductDao> {

		@Override
		public ProductDao get() {
			return newProductDao();
		}
		
	}
	private static class ShopDaoProvider implements Provider<ShopDao> {
		@Override
		public ShopDao get() {
			return newShopDao();
		}
	}
	
	private static ProductDao newProductDao() {
		return new MockProductDao() {
			@Override
			protected List<Product> getProductsAll() {
				return productsAll;
			}
		};
	}
	
	private static ShopDao newShopDao() {
		return new MockShopDao() {
			@Override
			protected List<Shop> getShopsAll() {
				return shopsAll;
			}
		};
	}
	
	private Resolver<Calendar> newCalendarResolver() {
		return new Resolver<Calendar>() {

			@Override
			public Calendar resolve() {
				return (Calendar) orderCalendar.clone();
			}

			@Override
			public Calendar resolve(Calendar value) {
				return value;
			}
		};
	}
	
	@Rule public WicketTesterResource wicketResource = new WicketTesterResource(injectBuilder);

	public static class ChoiceLunchForTest extends ChoiceLunch {

		private static final long serialVersionUID = 5963622497446033857L;
		
		public ChoiceLunchForTest(String id) {
			super(id);
		}

		@Override
		protected void onChoiceProduct(Product product) {
			// nothing to do.
		}
		
		@Override
		public String getVariation() {
			return "jquery";
		}
	}
	
	@Before
	public void before() {
		shopsAll.clear();
		productsAll.clear();
		orderCalendar = Calendar.getInstance();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void 初期表示正常() throws Exception {
		// GuiceのInjectorがSingletonのため 返却値を想定したListにArrays.asList() を利用すると
		// 修正ができない(Unmodified)ので 新規でArrayListをそれぞれのテストケースで生成し、static の ArrayList
		// に addAllで設定する
		// 複数のテストケースが存在する場合考慮する必要有り。 
		ArrayList<Shop> shops = new ArrayList<>();
		shops.add(new Shop("bento")
						.setName("おべんと屋さん")
						.setPhoneNumber("00-0000-0000")
						.setUrl("http://yahoo.co.jp")
						.setLastOrder(DateUtils.parseDate("10:00", parseFormats)));
		shopsAll.addAll(shops);
		ArrayList<Product> products = new ArrayList<>();
		products.add(new Product("bento-1")
							.setName("美味しいごはん")
							.setAmount(500)
							.setFinish(DateUtils.parseDate("2012/01/10 10:00", parseFormats))
							.setShop(shopsAll.get(0)));
		productsAll.addAll(products);
		orderCalendar.setTime(DateUtils.parseDate("2012/01/10 9:40", parseFormats));
				
		WicketTester tester = wicketResource.getTester(true);
		tester.startPanel(ChoiceLunchForTest.class);
		tester.assertComponent("choice", Choice.class);
		ListView<Object> listView = (ListView<Object>) tester.getComponentFromLastRenderedPage("choice:shop.list");
		
		assertEquals(2, listView.size());
		
		assertTrue(listView.get(0).getDefaultModelObject() instanceof Shop);
		Shop firstShop = (Shop) listView.get(0).getDefaultModelObject();
		assertEquals(shopsAll.get(0), firstShop);
		
		assertTrue(listView.get(1).getDefaultModelObject() instanceof Product);
		assertEquals(productsAll.get(0), listView.get(1).getDefaultModelObject());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void 商品なし() throws Exception {
		ArrayList<Shop> shops = new ArrayList<>();
		shops.add(new Shop("bento")
						.setName("BENTO")
						.setPhoneNumber("00-0000-0000")
						.setUrl("http://lunchat.jp")
						.setLastOrder(DateUtils.parseDate("09:30", parseFormats)));
		shops.add(new Shop("washoku")
						.setName("和食弁当")
						.setPhoneNumber("11-1111-1111")
						.setUrl("http://lunchat.jp")
						.setLastOrder(DateUtils.parseDate("10:00", parseFormats)));
		shopsAll.addAll(shops);
		productsAll = Collections.emptyList();
		// 9:30は washoku のラストオーダー時刻前だが、商品が存在しないため、「受付を終了しました」と表示している。
		// 13:00 までは当日分の商品を検索する仕様としている。休日(SHOPの休日)などで13:00までの表示を検討の必要有り。
		orderCalendar.setTime(DateUtils.parseDate("2012/01/10 9:30", parseFormats));
		
		WicketTester tester = wicketResource.getTester(true);
		tester.startPanel(ChoiceLunchForTest.class);
		tester.assertComponent("choice", Choice.class);
		ListView<Object> listView = (ListView<Object>) tester.getComponentFromLastRenderedPage("choice:shop.list");
		
		assertEquals(4, listView.size());
		
		assertTrue(listView.get(0).getDefaultModelObject() instanceof Shop);
		Shop shop = (Shop) listView.get(0).getDefaultModelObject();
		assertEquals(shopsAll.get(0), shop);
		
		assertTrue(listView.get(1).getDefaultModelObject() instanceof Product);
		Product product = (Product) listView.get(1).getDefaultModelObject();
		assertEquals("BENTOは 1/10(火)の注文の受付を終了しました。", product.getName());
		
		assertTrue(listView.get(2).getDefaultModelObject() instanceof Shop);
		shop = (Shop) listView.get(2).getDefaultModelObject();
		assertEquals(shopsAll.get(1), shop);
		
		assertTrue(listView.get(3).getDefaultModelObject() instanceof Product);
		product = (Product) listView.get(3).getDefaultModelObject();
		assertEquals("和食弁当は 1/10(火)の注文の受付を終了しました。", product.getName());
	}

	@Ignore
	@Test
	public void 商品特定() throws Exception {
		
		shopsAll = Arrays.asList(new Shop()
										.setName("")
										.setPhoneNumber("00-0000-0000")
										.setUrl("http://lunchat.jp")
										.setLastOrder(DateUtils.parseDate("10:00", parseFormats)),
									new Shop()
										.setName("")
										.setPhoneNumber("11-1111-2222")
										.setUrl("http://lunchat.jp")
										.setLastOrder(DateUtils.parseDate("09:30", parseFormats))
										);
		productsAll = Arrays.asList(new Product()
													.setName("ランチ(大)")
													.setAmount(500)
													.setFinish(DateUtils.parseDate(""))
													.setShop(shopsAll.get(0)),
												new Product()
													.setName("ランチ(中)")
													.setAmount(430)
													.setShop(shopsAll.get(0)),
												new Product()
													.setName("ランチ(小)")
													.setAmount(400)
													.setShop(shopsAll.get(0))
													);
	}
}
