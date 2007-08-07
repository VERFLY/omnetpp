package org.omnetpp.test.gui.access;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.omnetpp.test.gui.InUIThread;

public class MenuAccess extends WidgetAccess<Menu> {

	public MenuAccess(Menu menu) {
		super(menu);
	}

	public Menu getMenu() {
		return widget;
	}

	/**
	 * Activates the menu item with the given label. If it opens a submenu,
	 * return it, otherwise return null.
	 */
	@InUIThread
	public MenuAccess activateMenuItemWithMouse(String label) {
		return findMenuItemByLabel(label).activateWithMouseClick();
	}

	@InUIThread
	public MenuItemAccess findMenuItemByLabel(final String label) {
		return new MenuItemAccess((MenuItem)theOnlyWidget(collectMenuItems(widget, new IPredicate() {
			public boolean matches(Object object) {
				String menuItemLabel = ((MenuItem)object).getText().replace("&", "");
				return menuItemLabel.matches(label);
			}
		})));
	}

	@InUIThread
	public List<MenuItem> collectMenuItems(Menu menu, IPredicate predicate) {
		ArrayList<MenuItem> resultMenuItems = new ArrayList<MenuItem>();
		collectMenuItems(menu, predicate, resultMenuItems);
		return resultMenuItems;
	}

	protected void collectMenuItems(Menu menu, IPredicate predicate, List<MenuItem> resultMenuItems) {
		for (MenuItem menuItem : menu.getItems()) {
			if (debug)
				System.out.println("Trying to collect menu item: " + menuItem.getText());

			if (menuItem.getMenu() != null)
				collectMenuItems(menuItem.getMenu(), predicate, resultMenuItems);

			if (predicate.matches(menuItem))
				resultMenuItems.add(menuItem);
		}
	}
}
