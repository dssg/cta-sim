package dssg.client;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.PlainTabPanel;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.info.Info;
 
public class BasicTabExample extends FlowLayoutContainer {
 
    public BasicTabExample() {
        VerticalPanel vp = new VerticalPanel();
        vp.setSpacing(10);
 
        String txt = "Test";
 
        SelectionHandler<Widget> handler = new SelectionHandler<Widget>() {
          public void onSelection(SelectionEvent<Widget> event) {
            TabPanel panel = (TabPanel) event.getSource();
            Widget w = event.getSelectedItem();
            TabItemConfig config = panel.getConfig(w);
            Info.display("Message", "'" + config.getText() + "' Selected");
          }
        };
 
        TabPanel folder = new TabPanel();
        folder.addSelectionHandler(handler);
        folder.setWidth(450);
 
        HTML shortText = new HTML(txt);
        shortText.addStyleName("pad-text");
        folder.add(shortText, "Short Text");
 
        HTML longText = new HTML(txt + "<br><br>" + txt);
        longText.addStyleName("pad-text");
        folder.add(longText, "Long Text");
 
        final PlainTabPanel panel = new PlainTabPanel();
        panel.setPixelSize(450, 250);
        panel.addSelectionHandler(handler);
 
        Label normal = new Label("Just a plain old tab");
        normal.addStyleName("pad-text");
        panel.add(normal, "Normal");
 
        Label iconTab = new Label("Just a plain old tab with an icon");
        iconTab.addStyleName("pad-text");
 
        TabItemConfig config = new TabItemConfig("Icon Tab");
        panel.add(iconTab, config);
 
        Label disabled = new Label("This tab should be disabled");
        disabled.addStyleName("pad-text");
 
        config = new TabItemConfig("Disabled");
        config.setEnabled(false);
        panel.add(disabled, config);
 
        vp.add(folder);
        vp.add(panel);
        add(vp);
        }
}