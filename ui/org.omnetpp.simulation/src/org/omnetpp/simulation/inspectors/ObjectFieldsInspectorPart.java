package org.omnetpp.simulation.inspectors;

import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.omnetpp.common.color.ColorFactory;
import org.omnetpp.simulation.figures.FigureUtils;
import org.omnetpp.simulation.inspectors.ObjectFieldsViewer.Mode;
import org.omnetpp.simulation.inspectors.ObjectFieldsViewer.Ordering;
import org.omnetpp.simulation.inspectors.actions.InspectParentAction;
import org.omnetpp.simulation.inspectors.actions.SetModeAction;
import org.omnetpp.simulation.inspectors.actions.SortAction;
import org.omnetpp.simulation.model.cArray;
import org.omnetpp.simulation.model.cModule;
import org.omnetpp.simulation.model.cObject;
import org.omnetpp.simulation.model.cPacket;
import org.omnetpp.simulation.model.cQueue;

/**
 *
 * @author Andras
 */
//TODO adaptive label: display the most useful info that fits in the available space
public class ObjectFieldsInspectorPart extends AbstractSWTInspectorPart {
    private Composite frame;
    private Label label;
    private ObjectFieldsViewer viewer;
    
    public ObjectFieldsInspectorPart(IInspectorContainer parent, cObject object) {
        super(parent, object);
    }

    @Override
    protected Control createControl(Composite parent) {
        if (!object.isFilledIn())
            object.safeLoad(); // for getClassName() in next line

        boolean isSubclassedFromcPacket = (object instanceof cPacket) && !object.getClassName().equals("cPacket");
        boolean isContainer = (object instanceof cModule) || (object instanceof cQueue) || (object instanceof cArray);
        Mode initialMode = isSubclassedFromcPacket ? ObjectFieldsViewer.Mode.PACKET : 
            isContainer ? ObjectFieldsViewer.Mode.CHILDREN : ObjectFieldsViewer.Mode.GROUPED;

        frame = new Composite(parent, SWT.BORDER);
        frame.setSize(300, 200);
        frame.setLayout(new GridLayout(1, false));

        label = new Label(frame, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        viewer = new ObjectFieldsViewer(frame, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
        viewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        viewer.setMode(initialMode);
        viewer.setInput(object);

        viewer.getTree().addSelectionListener(new SelectionAdapter() {
            @Override
            @SuppressWarnings("unchecked")
            public void widgetDefaultSelected(SelectionEvent e) {
                // inspect the selected object(s)
                ISelection selection = viewer.getTreeViewer().getSelection();
                List<cObject> objects = SelectionUtils.getObjects(selection, cObject.class);
                for (cObject object : objects)
                    getContainer().inspect(object);
            }
        });

        viewer.getTreeViewer().addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent e) {
                // wrap to SelectionItems and export to the inspector canvas
                Object[] array = ((IStructuredSelection)e.getSelection()).toArray();
                for (int i = 0; i < array.length; i++)
                    array[i] = new SelectionItem(ObjectFieldsInspectorPart.this, array[i]);
                getContainer().select(array, true);
            }
        });

        frame.layout();

        getContainer().addMoveResizeSupport(frame);
        getContainer().addMoveResizeSupport(label);

        return frame;
    }

    @Override
    public boolean isMaximizable() {
        return false;
    }

    public Mode getMode() {
        return viewer.getMode();
    }

    public void setMode(Mode mode) {
        viewer.setMode(mode);
        getContainer().updateFloatingToolbarActions();
    }

    public Ordering getOrdering() {
        return viewer.getOrdering();
    }

    public void setOrdering(Ordering ordering) {
        viewer.setOrdering(ordering);
        getContainer().updateFloatingToolbarActions();
    }

    @Override
    public void populateContextMenu(MenuManager contextMenuManager, Point p) {
    }

    @Override
    public void populateFloatingToolbar(ToolBarManager manager) {
        manager.add(my(new InspectParentAction()));
        manager.add(new Separator());
        manager.add(my(new SortAction()));
        manager.add(new Separator());
        manager.add(my(new SetModeAction(Mode.PACKET, "Packet mode", ObjectFieldsViewer.IMG_MODE_PACKET)));
        manager.add(my(new SetModeAction(Mode.CHILDREN, "Children mode", ObjectFieldsViewer.IMG_MODE_CHILDREN)));
        manager.add(my(new SetModeAction(Mode.GROUPED, "Grouped mode", ObjectFieldsViewer.IMG_MODE_GROUPED)));
        manager.add(my(new SetModeAction(Mode.INHERITANCE, "Inheritance mode", ObjectFieldsViewer.IMG_MODE_INHERITANCE)));
        manager.add(my(new SetModeAction(Mode.FLAT, "Flat mode", ObjectFieldsViewer.IMG_MODE_FLAT)));
    }

    @Override
    public void refresh() {
        super.refresh();

        if (!isDisposed()) {
            String text = "(" + object.getShortTypeName() + ") " + object.getFullPath() + " - " + object.getInfo();
            label.setText(text);
            label.setToolTipText(text); // because label text is usually not fully visible

            viewer.refresh();
        }
    }
    
    @Override
    public int getDragOperation(Control control, int x, int y) {
        Point size = control.getSize();
        if (control == getSWTControl())
            return FigureUtils.getBorderResizeInsideMoveDragOperation(x, y, new Rectangle(0, 0, size.x, size.y));
        if (control == label)
            return FigureUtils.getMoveOnlyDragOperation(x, y, new Rectangle(0, 0, size.x, size.y));
        return 0;
    }

    protected void setSelectionMark(boolean isSelected) {
        super.setSelectionMark(isSelected);
        label.setBackground(isSelected ? ColorFactory.GREY50 : null);
    }
}