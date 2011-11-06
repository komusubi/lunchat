package jp.dip.komusubi.lunch.wicket.panel;

import jp.dip.komusubi.lunch.wicket.WicketApplication;
import jp.dip.komusubi.lunch.wicket.WicketSession;
import jp.dip.komusubi.lunch.wicket.page.Home;
import jp.dip.komusubi.lunch.wicket.page.Login;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

public class Header extends Panel {

	private static final long serialVersionUID = -467641882462547658L;

	public Header(String id, Model<String> model) {
		super(id, model);
		Label label;
		if (model != null)
			label = new Label("pageTitle", model);
		else
			label = new Label("pageTitle", "メニュー一覧");
		add(label);
		// FIXME bookmarkable page link not nice url
		add(new BookmarkablePageLink<WebPage>("link.home", WicketApplication.get().getHomePage(), null));
		add(getAuthLink("auth"));
		add(getWebMarkupContainer("nav"));
	}
	
	public Header(String id) {
		this(id, null);
	}

	private Link<Void> getAuthLink(String id) {
		Link<Void> link; 
		String label;
		
		if (WicketSession.get().isSignedIn()) {
			link = new Link<Void>(id) {

				private static final long serialVersionUID = 1L;

				@Override
				public void onClick() {
					WicketSession.get().invalidate();
				}
				
			};
			label = "ログアウト";
		} else {
//			link = new BookmarkablePageLink<Void>(id, 
//					((WicketApplication) WicketApplication.get()).getSignInPageClass(), null);
			link = new Link<Void>(id) {

				private static final long serialVersionUID = 1L;

				@Override
				public void onClick() {
					setResponsePage(new Login());
				}
			};
			label = "ログイン";
		}
				
		link.add(new Label("auth.label", label));
		return link;
	}
	
	private WebMarkupContainer getWebMarkupContainer(String id) {
		WebMarkupContainer container = new WebMarkupContainer(id) {
			
			private static final long serialVersionUID = 9112302809195248590L;

			@Override
			public boolean isVisible() {
				return WicketSession.get().isSignedIn();
			}
			
		};
		// FIXME reference from panel to page package. should change how to access. 
		container.add(new BookmarkablePageLink<WebPage>("nav.new", Home.class, null)); 
		container.add(new BookmarkablePageLink<WebPage>("nav.history", Home.class, null));
		container.add(new BookmarkablePageLink<WebPage>("nav.config", Home.class, null));
		
		return container;
	}
}
