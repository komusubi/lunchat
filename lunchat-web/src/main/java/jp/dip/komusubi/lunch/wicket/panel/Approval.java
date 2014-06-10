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

import java.util.List;

import jp.dip.komusubi.lunch.Configuration;
import jp.dip.komusubi.lunch.model.User;
import jp.dip.komusubi.lunch.module.dao.UserDao;
import jp.dip.komusubi.lunch.service.AccountService;
import jp.dip.komusubi.lunch.wicket.WicketSession;
import jp.dip.komusubi.lunch.wicket.page.error.ErrorPage;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.komusubi.common.util.Resolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * approval.
 * @author jun.ozeki
 * @since 2012/01/26
 */
public class Approval extends Panel {
    
    private static final long serialVersionUID = -4176824939454988839L;
    private static final Logger logger = LoggerFactory.getLogger(Approval.class);
	private User applicant;
	private User admitter = WicketSession.get().getSignedInUser();;
	private PageParameters params;
	private Button approval;
	@Inject @Named("digest") Resolver<String> digester;
	private ApprovalForm form;
	private String message;
	
	public Approval(String id, PageParameters params) {
		super(id);
		this.params = params;
		form = new ApprovalForm("approval.form");
		form.add(approval = getApprovalButton("approval"));
		form.add(getCancelButton("cancel"));
		add(form);
	}
	
	@Override
	public void onInitialize() {
	    if (admitter == null) {
	        logger.error("admitter is null");
	        throw new IllegalStateException("admitter is null"); 
	    }
	    if (params == null || params.get("fragment").isEmpty()) {
	        setResponsePage(new ErrorPage(getString("url.invalid")));
	    } else {
            UserDao userDao = Configuration.getInstance(UserDao.class);
            List<User> users = userDao.findByAdmitter(params.get("fragment").toString());
            if (users.size() != 1) {
                logger.warn("wrong digest value:{}, size:{}, users:{}", 
                        new Object[]{ params.get("fragment").toString(), users.size(), users });
                error(getString("not.found.applicant"));
            } else {
                applicant = users.get(0);
            }
	    }
	        
	    boolean greetingVisible = true;
	    String groupName = "";
	    if (admitter.getGroup() == null) {
	        greetingVisible = false;
	        logger.debug("admitter's group is null, greeting label visible set false.");
	    } else {
	        groupName = admitter.getGroup().getName();
	    }
	    
        if (applicant != null) {
            // compare digest value.
            AccountService account = Configuration.getInstance(AccountService.class);
            String digested = account.digest(admitter, applicant, applicant.getHealth().getGroupJoined());
            logger.info("fragment:{}, digest:{}", params.get("fragment").toString(), digested);
            if (!digested.equals(params.get("fragment").toString())) {
                error(getString("unmatch.admitter.and.applicant", Model.of(admitter)));
            }
        } else {
            greetingVisible = false;
            logger.debug("applicant is null, greeting label visible set false");
        }
        
        Label label = new Label("greeting", new StringResourceModel("apply.message", this, Model.of(applicant), 
                new Object[]{ groupName }));
        label.setVisibilityAllowed(greetingVisible);
        add(label);
        approval.setEnabled(greetingVisible);
        if (greetingVisible)
            message = getString("welcome.message", Model.of(admitter.getGroup()));
        else if (admitter.getGroup() != null)
            message = getString("decline.message", Model.of(admitter.getGroup()));
        else 
            message = getString("only.decline.message");
        form.add(new TextField<String>("message", Model.of(message)));

        super.onInitialize();
    }
	    
//	private TextField<String> getApprovalMessage(String id) {
//	    TextField<String> text = new TextField<String>(id, new StringResourceModel("")); 
//	    return text;
//	}
	
	private Button getApprovalButton(String id) {
	    return new Button(id) {
	        
	        private static final long serialVersionUID = 7138884209309510709L;
	        @Override
	        public void onSubmit() {
	            try {
	                onApproval();
	            } catch (Exception e) {
	                error(getString(""));
	            }
	        }
	    };
	}
	
	private Button getCancelButton(String id) {
	    return new Button(id) {
	        
	        private static final long serialVersionUID = 3491917688221911493L;
	        @Override
	        public void onSubmit() {
	            try {
	                onCancel();
	            } catch (Exception e) {
	                error("error occurred");
	            }
	        }
	    };
	}
	
	private class ApprovalForm extends Form<Approval> {

		private static final long serialVersionUID = 4010463474401119716L;
		
		public ApprovalForm(String id) {
			super(id);
		}
		
	}
	
	protected void onApproval() {
	    logger.info("onApproval event!");
	}
	
	protected void onCancel() {
	    logger.info("onCancel event!");
	}

	protected User getAdmitter() {
	    return admitter;
	}
	
	protected User getApplicant() {
	    return applicant;
	}
	
	protected String getMessageFromAdmitter() {
	    return message;
	}
}
