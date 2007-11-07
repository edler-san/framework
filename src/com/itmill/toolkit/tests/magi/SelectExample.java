package com.itmill.toolkit.tests.magi;
import com.itmill.toolkit.ui.*;
import com.itmill.toolkit.data.*;
import com.itmill.toolkit.*;

/* Let us add an implementation of the ValueChangeListener interface. */
public class SelectExample extends CustomComponent implements Property.ValueChangeListener {
	
	class Planet extends Object {
		String planetName;
		Planet (String name) {
			planetName = name;
		}
		public String toString () {
			return "The Planet " + planetName;
		}
	}
	
	/* Create the Select object with a caption. */
	Select select = new Select(/*"This is a Select component that allows adding new items"*/);

	OrderedLayout layout = new OrderedLayout(OrderedLayout.ORIENTATION_VERTICAL);
	Label status = new Label("");
	
	SelectExample (Application application) {
		setCompositionRoot (layout);
		layout.addComponent(select);

		/* Fill the component with some items. */
		final String[] planets = new String[] {"Mercury", "Venus", "Earth", "Mars",
											   "Jupiter", "Saturn", "Uranus", "Neptune"};
		
		for (int i=0; i<planets.length; i++) {
			select.addItem(planets[i]);

			/* Create an item with an Integer as the Item ID. */
			//select.addItem(i);
			
			//select.addItem(new Planet(planets[i]));
			
			/* Set the visible caption of the item. */
			//select.setItemCaption(i, planets[i]);
			
			/* ClassResource icon = new ClassResource ("images/"+planets[i]+"_symbol.png", application);
			layout.addComponent(new Embedded ("Icon", icon));
			select.setItemIcon(i, icon); */
		}

		/* By default, the change event is not triggered immediately
		 * when the selection changes. This enables it. */
		select.setImmediate(true);
		
		/* Listen for changes in the selection. */
		select.addListener(this);

		select.setStyle("twincol");
		select.setMultiSelect(true);
		//select.setNewItemsAllowed(true);
		//int a=1;
		
		//select.setItemCaptionMode(Select.ITEM_CAPTION_MODE_ICON_ONLY);
		select.setNullSelectionItemId("-- select something --");
		
		layout.addComponent(status);

		/*
		status.setValue(String.format("Currently selected item ID: %s<br/>" +
				  "Class of the Item ID: %s<br/>" +
				  "Caption: %s",
				  select.getValue(),
				  select.getValue().getClass().getName(),
				  select.getItemCaption(select.getValue())));
		status.setContentMode(Label.CONTENT_XHTML);
				  */
		
	}

	/* Respond to change in the selection. */
	public void valueChange(Property.ValueChangeEvent event) {
		/* The event.getProperty() returns the component. The currently
		 * selected item is the property of the component, retrievable with getValue().*/
		if(true) {
			status.setValue(String.format("Currently selected item ID: %s<br/>" +
									  "Class of the Item ID: %s<br/>" +
									  "Caption: %s",
									  event.getProperty().getValue(),
									  event.getProperty().getValue().getClass().getName(),
									  select.getItemCaption(event.getProperty().getValue())));
			status.setContentMode(Label.CONTENT_XHTML);
		}
	}
}
