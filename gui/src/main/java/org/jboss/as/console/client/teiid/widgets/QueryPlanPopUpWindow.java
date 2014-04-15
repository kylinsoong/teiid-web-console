/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.jboss.as.console.client.teiid.widgets;

import org.jboss.as.console.client.teiid.widgets.PlanNode.Property;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.ballroom.client.widgets.window.TrappedFocusPanel;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings("nls")
public class QueryPlanPopUpWindow extends DefaultWindow {
	
	public QueryPlanPopUpWindow(String title, String content) {
		super(title);
        setWidth(700);
        setHeight(700);
        addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
            }
        });
        center();
        
		// Add a close button at the bottom of the dialog
        HorizontalPanel closePanel = new HorizontalPanel();
        closePanel.getElement().setAttribute("style", "margin-top:10px;width:100%");
		Button closeButton = new Button("Close", new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();
			}
		});   
		closePanel.add(closeButton);
		closeButton.getElement().setAttribute("style", "min-width:60px;");
		closeButton.getElement().getParentElement().setAttribute("align", "right");
		closeButton.getElement().getParentElement().setAttribute("width", "100%");
		
		Widget widget = buildQueryPlan(content);
        
        widget.getElement().setAttribute("style", "margin:5px");

        Widget windowContent = new WindowContentBuilder(widget, closePanel).build();

        TrappedFocusPanel trap = new TrappedFocusPanel(windowContent)
        {
            @Override
            protected void onAttach() {
                super.onAttach();

                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        getFocus().onFirstButton();
                    }
                });
            }
        };

        setWidget(trap);
        center();
	}

	private Widget buildQueryPlan(String content) {
		
		PlanNode node = PlanNode.fromXml(content);
		
        VerticalPanel container = new VerticalPanel();
        container.setStyleName("fill-layout");

        Tree tree = new Tree();
        container.add(buildTree(tree, node));
        return container;
	}
	
	
	private Tree buildTree(Tree tree, PlanNode plan) {
        TreeItem outerRoot = new TreeItem(new SafeHtmlBuilder().appendHtmlConstant(plan.getName()).toSafeHtml());
        for (Property p:plan.getProperties()) {
        	outerRoot.addItem(new SafeHtmlBuilder().appendHtmlConstant(p.getName()+"="+p.getValuesAsCSV()).toSafeHtml());
        }
        for (PlanNode pn:plan.getChildNodes()) {
        	Tree child = new Tree();
        	buildTree(child, pn);
        	outerRoot.addItem(new TreeItem(child));
        }
        tree.addItem(outerRoot);
        outerRoot.setState(true);
        return tree;
        
	}
}
