package org.omnetpp.test.gui.access;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.omnetpp.common.util.ReflectionUtils;
import org.omnetpp.test.gui.InUIThread;

public class MenuItemAccess extends ClickableWidgetAccess<MenuItem>
{
	public MenuItemAccess(MenuItem menuItem) {
		super(menuItem);
	}

	public MenuItem getMenuItem() {
		return widget;
	}

	@InUIThread
	public MenuAccess activateWithMouseClick() {
		click();
		return widget.getMenu() == null ? null : new MenuAccess(widget.getMenu());
	}

	@Override
	protected Point getPointToClick() {
		Rectangle parentRectangle = (Rectangle)ReflectionUtils.invokeMethod(widget.getParent(), "getBounds");
		Rectangle rectangle = (Rectangle)ReflectionUtils.invokeMethod(widget, "getBounds");
		rectangle.x = rectangle.x + parentRectangle.x;
		rectangle.y = rectangle.y + parentRectangle.y;
		return getCenter(rectangle);
	}

	@Override
	protected Menu getContextMenu() {
		return null;  // a menu has no context menu
	}
}
