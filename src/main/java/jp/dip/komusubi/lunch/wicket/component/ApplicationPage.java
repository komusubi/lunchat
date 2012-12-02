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
package jp.dip.komusubi.lunch.wicket.component;

import jp.dip.komusubi.lunch.Configuration;
import jp.dip.komusubi.lunch.Configuration.RuntimeMode;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;

/**
 * @author jun.ozeki
 */
public class ApplicationPage extends WebPage {

    private static final long serialVersionUID = 1L;
    /** google analysis javascript snippet */
    private static final String analytics = "var _gaq = _gaq || [];"
            + "_gaq.push(['_setAccount', 'UA-26541332-2']);"
            + "_gaq.push(['_setDomainName', 'lunchat.jp']);"
            + "_gaq.push(['_trackPageview']);"
            + "(function() {"
            + "var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;"
            + "ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';"
            + "var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);"
            + "})();";
    /** disalbe jquery ajax */
    private static final String disabledAjax = "$(document).bind(\"mobileinit\", function(){"
            + "$.mobile.ajaxEnabled = false;});";

    /**
     * create new instance.
     */
    public ApplicationPage() {
        
    }

    /**
     * 
     * @return
     */
    public boolean isJquery() {
        // TODO return true static value.
        return true;
    }
    
    /**
     * 
     * @param response
     */
    @Override
    public void renderHead(IHeaderResponse response) {
        if (isJquery()) {
            response.renderCSSReference("http://code.jquery.com/mobile/1.2.0/jquery.mobile-1.2.0.min.css");
            response.renderJavaScriptReference("http://code.jquery.com/jquery-1.8.2.min.js");
            response.renderJavaScript(disabledAjax, null);
            response.renderJavaScriptReference("http://code.jquery.com/mobile/1.2.0/jquery.mobile-1.2.0.min.js");
//            response.render(CssHeaderItem.forUrl("http://code.jquery.com/mobile/1.2.0/jquery.mobile-1.2.0.min.css"));
//            response.render(JavaScriptHeaderItem.forUrl("http://code.jquery.com/jquery-1.8.2.min.js"));
//            response.render(JavaScriptHeaderItem.forScript(disabledAjax, null));
//            response.render(JavaScriptHeaderItem.forUrl("http://code.jquery.com/mobile/1.2.0/jquery.mobile-1.2.0.min.js"));
        }
        if (RuntimeMode.DEPLOYMENT.equals(Configuration.mode()))
            response.renderJavaScript(analytics, null);
    }
}
