package org.omnetpp.scave.editors.ui;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class CheckboxSelectionDialog extends Dialog {
	
	private String title;
	private String[] labels;
	private boolean[] selection;

	protected CheckboxSelectionDialog(Shell parentShell, String title, String[] labels, boolean[] initialSelection) {
		super(parentShell);
		this.title = title;
		Assert.isTrue(labels != null && initialSelection != null && labels.length == initialSelection.length);
		this.labels = labels;
		this.selection = initialSelection;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		if (title != null)
			newShell.setText(title);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		composite.setLayout(layout);
		for (int i = 0; i < labels.length; ++i) {
			final int index = i;
			final Button checkbox = createCheckbox(composite, labels[i], selection[i]);
			checkbox.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					selection[index] = checkbox.getSelection();
				}
			});
		}
		return composite;
	}
	
	protected Button createCheckbox(Composite parent, String label, boolean selected) {
		Button checkbox = new Button(parent, SWT.CHECK);
		checkbox.setText(label);
		checkbox.setSelection(selected);
		return checkbox;
	}
	
	public boolean[] getSelection() {
		return selection;
	}
}
