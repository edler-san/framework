/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ComponentState;
import com.vaadin.terminal.gwt.client.ConnectorHierarchyChangedEvent;
import com.vaadin.terminal.gwt.client.LayoutManager;
import com.vaadin.terminal.gwt.client.UIDL;

public class PanelConnector extends AbstractComponentContainerConnector
        implements SimpleManagedLayout, PostLayoutListener {

    public static class PanelState extends ComponentState {
        private int tabIndex;
        private int scrollLeft, scrollTop;

        public int getTabIndex() {
            return tabIndex;
        }

        public void setTabIndex(int tabIndex) {
            this.tabIndex = tabIndex;
        }

        public int getScrollLeft() {
            return scrollLeft;
        }

        public void setScrollLeft(int scrollLeft) {
            this.scrollLeft = scrollLeft;
        }

        public int getScrollTop() {
            return scrollTop;
        }

        public void setScrollTop(int scrollTop) {
            this.scrollTop = scrollTop;
        }

    }

    public static final String CLICK_EVENT_IDENTIFIER = "click";

    private Integer uidlScrollTop;

    private ClickEventHandler clickEventHandler = new ClickEventHandler(this,
            CLICK_EVENT_IDENTIFIER) {

        @Override
        protected <H extends EventHandler> HandlerRegistration registerHandler(
                H handler, Type<H> type) {
            return getWidget().addDomHandler(handler, type);
        }
    };

    private Integer uidlScrollLeft;

    @Override
    public void init() {
        VPanel panel = getWidget();
        LayoutManager layoutManager = getLayoutManager();

        layoutManager.registerDependency(this, panel.captionNode);
        layoutManager.registerDependency(this, panel.bottomDecoration);
        layoutManager.registerDependency(this, panel.contentNode);
    }

    @Override
    protected boolean delegateCaptionHandling() {
        return false;
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (isRealUpdate(uidl)) {

            // Handle caption displaying and style names, prior generics.
            // Affects size calculations

            // Restore default stylenames
            getWidget().contentNode.setClassName(VPanel.CLASSNAME + "-content");
            getWidget().bottomDecoration.setClassName(VPanel.CLASSNAME
                    + "-deco");
            getWidget().captionNode.setClassName(VPanel.CLASSNAME + "-caption");
            boolean hasCaption = false;
            if (getState().getCaption() != null
                    && !"".equals(getState().getCaption())) {
                getWidget().setCaption(getState().getCaption());
                hasCaption = true;
            } else {
                getWidget().setCaption("");
                getWidget().captionNode.setClassName(VPanel.CLASSNAME
                        + "-nocaption");
            }

            // Add proper stylenames for all elements. This way we can prevent
            // unwanted CSS selector inheritance.
            final String captionBaseClass = VPanel.CLASSNAME
                    + (hasCaption ? "-caption" : "-nocaption");
            final String contentBaseClass = VPanel.CLASSNAME + "-content";
            final String decoBaseClass = VPanel.CLASSNAME + "-deco";
            String captionClass = captionBaseClass;
            String contentClass = contentBaseClass;
            String decoClass = decoBaseClass;
            if (getState().hasStyles()) {
                for (String style : getState().getStyles()) {
                    captionClass += " " + captionBaseClass + "-" + style;
                    contentClass += " " + contentBaseClass + "-" + style;
                    decoClass += " " + decoBaseClass + "-" + style;
                }
            }
            getWidget().captionNode.setClassName(captionClass);
            getWidget().contentNode.setClassName(contentClass);
            getWidget().bottomDecoration.setClassName(decoClass);
        }
        // Ensure correct implementation
        super.updateFromUIDL(uidl, client);

        if (!isRealUpdate(uidl)) {
            return;
        }

        clickEventHandler.handleEventHandlerRegistration(client);

        getWidget().client = client;
        getWidget().id = uidl.getId();

        if (getState().getIcon() != null) {
            getWidget().setIconUri(getState().getIcon().getURL(), client);
        } else {
            getWidget().setIconUri(null, client);
        }

        getWidget().handleError(uidl);

        // Render content
        // final UIDL layoutUidl = uidl.getChildUIDL(0);
        // final ComponentConnector newLayout = client.getPaintable(layoutUidl);
        // if (newLayout != getWidget().layout) {
        // if (getWidget().layout != null) {
        // client.unregisterPaintable(getWidget().layout);
        // }
        // getWidget().setWidget(newLayout.getWidget());
        // getWidget().layout = newLayout;
        // }
        // getWidget().layout.updateFromUIDL(layoutUidl, client);

        // We may have actions attached to this panel
        if (uidl.getChildCount() > 0) {
            final int cnt = uidl.getChildCount();
            for (int i = 0; i < cnt; i++) {
                UIDL childUidl = uidl.getChildUIDL(i);
                if (childUidl.getTag().equals("actions")) {
                    if (getWidget().shortcutHandler == null) {
                        getWidget().shortcutHandler = new ShortcutActionHandler(
                                getConnectorId(), client);
                    }
                    getWidget().shortcutHandler.updateActionMap(childUidl);
                }
            }
        }

        if (getState().getScrollTop() != getWidget().scrollTop) {
            // Sizes are not yet up to date, so changing the scroll position
            // is deferred to after the layout phase
            uidlScrollTop = getState().getScrollTop();
        }

        if (getState().getScrollLeft() != getWidget().scrollLeft) {
            // Sizes are not yet up to date, so changing the scroll position
            // is deferred to after the layout phase
            uidlScrollLeft = getState().getScrollLeft();
        }

        // And apply tab index
        getWidget().contentNode.setTabIndex(getState().getTabIndex());
    }

    public void updateCaption(ComponentConnector component, UIDL uidl) {
        // NOP: layouts caption, errors etc not rendered in Panel
    }

    @Override
    public VPanel getWidget() {
        return (VPanel) super.getWidget();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VPanel.class);
    }

    public void layout() {
        updateSizes();
    }

    void updateSizes() {
        VPanel panel = getWidget();

        Style contentStyle = panel.contentNode.getStyle();
        if (isUndefinedHeight()) {
            contentStyle.clearHeight();
        } else {
            contentStyle.setHeight(100, Unit.PCT);
        }

        if (isUndefinedWidth()) {
            contentStyle.clearWidth();
        } else {
            contentStyle.setWidth(100, Unit.PCT);
        }

        LayoutManager layoutManager = getLayoutManager();
        int top = layoutManager.getInnerHeight(panel.captionNode);
        int bottom = layoutManager.getInnerHeight(panel.bottomDecoration);

        Style style = panel.getElement().getStyle();
        panel.captionNode.getStyle().setMarginTop(-top, Unit.PX);
        panel.bottomDecoration.getStyle().setMarginBottom(-bottom, Unit.PX);
        style.setPaddingTop(top, Unit.PX);
        style.setPaddingBottom(bottom, Unit.PX);

        // Update scroll positions
        panel.contentNode.setScrollTop(panel.scrollTop);
        panel.contentNode.setScrollLeft(panel.scrollLeft);
        // Read actual value back to ensure update logic is correct
        panel.scrollTop = panel.contentNode.getScrollTop();
        panel.scrollLeft = panel.contentNode.getScrollLeft();
    }

    public void postLayout() {
        VPanel panel = getWidget();
        if (uidlScrollTop != null) {
            panel.contentNode.setScrollTop(uidlScrollTop.intValue());
            // Read actual value back to ensure update logic is correct
            // TODO Does this trigger reflows?
            panel.scrollTop = panel.contentNode.getScrollTop();
            uidlScrollTop = null;
        }

        if (uidlScrollLeft != null) {
            panel.contentNode.setScrollLeft(uidlScrollLeft.intValue());
            // Read actual value back to ensure update logic is correct
            // TODO Does this trigger reflows?
            panel.scrollLeft = panel.contentNode.getScrollLeft();
            uidlScrollLeft = null;
        }
    }

    @Override
    public PanelState getState() {
        return (PanelState) super.getState();
    }

    @Override
    protected PanelState createState() {
        return GWT.create(PanelState.class);
    }

    @Override
    public void connectorHierarchyChanged(ConnectorHierarchyChangedEvent event) {
        super.connectorHierarchyChanged(event);
        // We always have 1 child, Panel takes care of ensuring content is never
        // null
        ComponentConnector newChild = getChildren().get(0);
        Widget newChildWidget = newChild.getWidget();

        getWidget().setWidget(newChildWidget);
    }

}
