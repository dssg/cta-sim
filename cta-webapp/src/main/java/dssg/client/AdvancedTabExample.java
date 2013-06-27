package dssg.client;  
  
import com.extjs.gxt.ui.client.event.ButtonEvent;  
import com.extjs.gxt.ui.client.event.Events;  
import com.extjs.gxt.ui.client.event.Listener;  
import com.extjs.gxt.ui.client.event.SelectionListener;  
import com.extjs.gxt.ui.client.widget.HorizontalPanel;  
import com.extjs.gxt.ui.client.widget.LayoutContainer;  
import com.extjs.gxt.ui.client.widget.TabItem;  
import com.extjs.gxt.ui.client.widget.TabPanel;  
import com.extjs.gxt.ui.client.widget.VerticalPanel;  
import com.extjs.gxt.ui.client.widget.button.Button;  
import com.extjs.gxt.ui.client.widget.button.ToggleButton;  
import com.google.gwt.user.client.Element;  
  
public class AdvancedTabExample extends LayoutContainer {  
  
  private int index = 0;  
  private TabPanel advanced;  
  
  @Override  
  protected void onRender(Element parent, int pos) {  
    super.onRender(parent, pos);  
    VerticalPanel vp = new VerticalPanel();  
    vp.setSpacing(10);  
  
    HorizontalPanel hp = new HorizontalPanel();  
    hp.setSpacing(5);  
  
    Button add = new Button("Add Tab");  
    add.addSelectionListener(new SelectionListener<ButtonEvent>() {  
      @Override  
      public void componentSelected(ButtonEvent ce) {  
        addTab();  
        advanced.setSelection(advanced.getItem(index - 1));  
      }  
    });  
    hp.add(add);  
  
    ToggleButton toggle = new ToggleButton("Enable Tab Context Menu");  
    toggle.addListener(Events.Toggle, new Listener<ButtonEvent>() {  
      public void handleEvent(ButtonEvent be) {  
        advanced.setCloseContextMenu(((ToggleButton) be.getButton()).isPressed());  
      }  
    });  
    toggle.toggle(true);  
    hp.add(toggle);  
    vp.add(hp);  
  
    advanced = new TabPanel();  
    advanced.setSize(600, 250);  
    advanced.setMinTabWidth(115);  
    advanced.setResizeTabs(true);  
    advanced.setAnimScroll(true);  
    advanced.setTabScroll(true);  
    advanced.setCloseContextMenu(true);  
  
    while (index < 7) {  
      addTab();  
    }  
  
    advanced.setSelection(advanced.getItem(6));  
  
    vp.add(advanced);  
    add(vp);  
  }  
  
  private void addTab() {  
    TabItem item = new TabItem();  
    item.setText("New Tab " + ++index);  
    item.setClosable(index != 1);  
    item.addText("Tab Body " + index);  
    item.addStyleName("pad-text");  
    advanced.add(item);  
  }  
  
}  