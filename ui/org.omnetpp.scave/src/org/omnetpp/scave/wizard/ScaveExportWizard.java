/*--------------------------------------------------------------*
  Copyright (C) 2006-2015 OpenSim Ltd.

  This file is distributed WITHOUT ANY WARRANTY. See the file
  'License' for details on this and other legal matters.
*--------------------------------------------------------------*/

package org.omnetpp.scave.wizard;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.omnetpp.common.ui.SWTFactory;
import org.omnetpp.common.util.StringUtils;
import org.omnetpp.common.util.UIUtils;
import org.omnetpp.common.wizard.XSWTDataBinding;
import org.omnetpp.scave.ScavePlugin;
import org.omnetpp.scave.editors.IDListSelection;
import org.omnetpp.scave.editors.ScaveEditor;
import org.omnetpp.scave.engine.Exporter;
import org.omnetpp.scave.engine.ExporterFactory;
import org.omnetpp.scave.engine.ExporterType;
import org.omnetpp.scave.engine.IDList;
import org.omnetpp.scave.engine.ResultFileManager;
import org.omnetpp.scave.model.Dataset;
import org.omnetpp.scave.model.DatasetItem;
import org.omnetpp.scave.model2.DatasetManager;
import org.omnetpp.scave.model2.ScaveModelUtil;

import com.swtworkbench.community.xswt.XSWT;

/**
 * A generic export wizard.
 *
 * @author andras
 */
public class ScaveExportWizard extends Wizard implements IExportWizard {
    protected IDList selectedIDs = IDList.EMPTY;
    protected Dataset selectedDataset;
    protected DatasetItem selectedDatasetItem;
    protected int selectionItemTypes;
    protected ResultFileManager resultFileManager;
    protected Page page;
    protected String format;
    protected ExporterType exporterType;
    protected Exporter exporter;


    public ScaveExportWizard(String format) {
        this.format = format;
        setWindowTitle(format + " Export");
        setDialogSettings(UIUtils.getDialogSettings(ScavePlugin.getDefault(), getClass().getName()));
    }

    /**
     * Sets the IDs of data to be exported and the ResultFileManager that owns them.
     */
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        // TODO use the selection of the active Scave editor instead passed parameter?

        if (selection instanceof IDListSelection) {
            IDListSelection idlistSelection = (IDListSelection)selection;
            selectedIDs = IDList.fromArray(idlistSelection.getIDs());
            resultFileManager = idlistSelection.getResultFileManager();
        }
        else if (selection.size() == 1 && (selection.getFirstElement() instanceof Dataset || selection.getFirstElement() instanceof DatasetItem)) {
            // get IDList from selected Dataset or DatasetItem
            Object selected = selection.getFirstElement();
            ScaveEditor editor = ScaveEditor.getActiveScaveEditor(workbench);
            if (editor != null && selected instanceof Dataset || selected instanceof DatasetItem) {
                selectedDataset = ScaveModelUtil.findEnclosingOrSelf((EObject)selected, Dataset.class);
                selectedDatasetItem = (selected instanceof DatasetItem) ? (DatasetItem)selected : null;
                resultFileManager = editor.getResultFileManager();
                selectedIDs = DatasetManager.getIDListFromDataset(resultFileManager, selectedDataset, selectedDatasetItem, null);
            }
        }
        else {
            // Invoking class (ExportDataAction) should ensure this does not happen
            ScavePlugin.logError("Invalid selection, cannot export", null);
        }

        // create exporter
        exporterType = ExporterFactory.getByFormat(format);
        exporter = ExporterFactory.createExporter(format);
        if (exporter == null)
            throw new RuntimeException("Unsupported export format: " + format);

        // check whether exporter supports the result types in the IDList
        selectionItemTypes = selectedIDs.getItemTypes();
    }

    @Override
    public void addPages() {
        page = new Page();
        addPage(page);
    }

    @Override
    public boolean canFinish() {
        return page != null && resultFileManager != null && super.canFinish();
    }

    @Override
    public boolean performFinish() {
        if (page != null && resultFileManager != null) {
            try {
                // set exporter options from the page
                for (String widgetName : page.widgetMap.keySet()) {
                    Control control = page.widgetMap.get(widgetName);
                    Object value = XSWTDataBinding.getValueFromControl(control, null);
                    try {
                        exporter.setOption(widgetName, value.toString());
                    }
                    catch (Exception e) {
                        // log but otherwise ignore the error
                        ScavePlugin.logError("Cannot set option " + widgetName + "=" + value + " on " + format + " exporter", e);
                    }
                }
                
                final String filename = page.getFilePath().toOSString();

                // perform the export
                Job exportJob = new WorkspaceJob(format + " Export") {
                    @Override
                    public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
                        exporter.saveResults(filename, resultFileManager, selectedIDs, monitor);
                        return Status.OK_STATUS;
                    }
                };
                exportJob.schedule();

                // save the control values before the dialog gets closed
                saveDialogSettings();
                return true;
            }
            catch (Exception e) {
                MessageDialog.openError(getShell(), "Error", "Error occured during export: " + e.toString());
            }
        }
        return false;
    }

    protected void saveDialogSettings() {
        if (page != null)
            page.saveDialogSettings();
    }

    /**
     * Wizard page
     */
    class Page extends WizardPage
    {
        private Text filenameText;
        private Map<String,Control> widgetMap;

        public Page() {
            super(format + " Export");
            setTitle(StringUtils.breakLines(exporterType.getDescription().trim(),80) + "\n" + getSelectionInfoString());
        }

        @Override
        public void createControl(Composite parent) {
            Composite panel = SWTFactory.createComposite(parent, 1, 1, SWTFactory.GRAB_AND_FILL_BOTH);
            addCommonFields(panel);
            addXswtForm(panel);
            restoreDialogSettings();
            setControl(panel);
        }

        protected void addCommonFields(Composite parent) {
            Composite fileSel = SWTFactory.createComposite(parent, 3, 1, SWTFactory.GRAB_AND_FILL_HORIZONTAL);
            SWTFactory.createLabel(fileSel, "Target file:", 1);
            filenameText = SWTFactory.createText(fileSel, SWT.BORDER, 1, SWTFactory.GRAB_AND_FILL_HORIZONTAL);
            Button browseButton = SWTFactory.createPushButton(fileSel, "Browse..." , null);
            browseButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    handleBrowseButtonPressed();
                }
            });
            filenameText.addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(ModifyEvent e) {
                    updatePageCompletion();
                }
            });
        }

        @SuppressWarnings("unchecked")
        protected void addXswtForm(Composite parent) {
            try {
                // instantiate XSWT form
                Composite xswtHolder = SWTFactory.createComposite(parent, 1, 1, SWTFactory.GRAB_AND_FILL_BOTH);
                String xswtDoc = exporterType.getXswtForm();
                widgetMap = new HashMap<>(); // prevent NPE later
                if (!xswtDoc.isEmpty())
                    widgetMap = XSWT.create(xswtHolder, new ByteArrayInputStream(xswtDoc.getBytes()));
                parent.getShell().layout(true); // needed?
            }
            catch (Exception e) {
                IStatus status = new Status(IStatus.ERROR, ScavePlugin.PLUGIN_ID, "Error showing the XSWT form of exporter '" + format + "'", e);
                ScavePlugin.log(status);
                ErrorDialog.openError(getShell(), "Error", "Cannot add exporter options to Export dialog.", status);
            }
        }

        protected String getSelectionInfoString() {
            ArrayList<String> items = new ArrayList<>();
            int numScalars = selectedIDs.countByTypes(ResultFileManager.SCALAR);
            int numVectors = selectedIDs.countByTypes(ResultFileManager.VECTOR);
            int numHistograms = selectedIDs.countByTypes(ResultFileManager.HISTOGRAM);
            if (numScalars > 0)
                items.add(StringUtils.formatCounted(numScalars, "scalar"));
            if (numVectors > 0)
                items.add(StringUtils.formatCounted(numVectors, "vector"));
            if (numHistograms > 0)
                items.add(StringUtils.formatCounted(numHistograms, "histogram"));
            if (selectedIDs.isEmpty())
                items.add("Empty set");
            return StringUtils.join(items, ", ", " and ") + " selected.";
        }

        private String itemTypesAsString(int types) {
            String result = "";
            if ((types & ResultFileManager.SCALAR) != 0)
                result += "scalar ";
            if ((types & ResultFileManager.VECTOR) != 0)
                result += "vector ";
            if ((types & ResultFileManager.HISTOGRAM) != 0)
                result += "histogram ";
            return StringUtils.join(result.split(" "), " and ");
        }

        protected void updatePageCompletion() {
            int supportedTypes = exporterType.getSupportedResultTypes();
            int unsupportedTypes = selectionItemTypes & ~supportedTypes;
            if (unsupportedTypes != 0) {
                setPageComplete(false);
                setErrorMessage("This exporter does not support exporting " + itemTypesAsString(unsupportedTypes) + " items");
                return;
            }

            IPath path = getFilePath();
            if (path.isEmpty()) {
                setPageComplete(false);
                setErrorMessage("Enter file name to export to");
            }
            else if (path.segmentCount() > 1) {
                boolean ok = path.removeLastSegments(1).toFile().isDirectory();
                setErrorMessage(ok ? null : "No such directory");
                setPageComplete(ok);
            }
        }

        protected void handleBrowseButtonPressed() {
            IPath path = getFilePath();
            String directory = path.removeLastSegments(1).toOSString();
            String fileName = path.lastSegment();

            FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
            dialog.setText("Save to File");
            dialog.setFilterPath(directory);
            dialog.setFileName(fileName);
            dialog.setFilterExtensions(new String[] {"*." + exporterType.getFileExtension(), "*.*"});
            String selectedFileName = dialog.open();
            if (selectedFileName != null)
                filenameText.setText(selectedFileName);
        }

        /**
         * Returns *absolute* file system (not workspace) path.
         */
        public IPath getFilePath() {
            String currentFilename = filenameText.getText();
            if (currentFilename.isEmpty())
                currentFilename = "exported." + exporterType.getFileExtension();
            IPath path = new Path(currentFilename);
            if (!path.isAbsolute()) {
                String directory = getDefaultExportDirectory();
                path = new Path(directory).append(path);
            }
            return path;
        }
        
        protected String getDefaultExportDirectory() {
            if (selectedIDs.isEmpty())
                return "";
            String firstResultFile = ResultFileManager.callWithReadLock(resultFileManager, new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return resultFileManager.getItem(selectedIDs.get(0)).getFileRun().getFile().getFileSystemFilePath();
                }
            });
            return new Path(firstResultFile).removeLastSegments(1).toOSString(); // remove file name
        }
        
        //TODO use
        public Map<String,String> getOptions() {
            Map<String,String> options = new HashMap<String, String>();
            for (String widgetName : widgetMap.keySet()) {
                Control control = widgetMap.get(widgetName);
                Object value = XSWTDataBinding.getValueFromControl(control, null);
                options.put(widgetName, value.toString());
            }
            return options;
        }
        
        protected void saveDialogSettings() {
            IDialogSettings settings = getDialogSettings();
            if (settings != null) {
                for (String widgetName : page.widgetMap.keySet()) {
                    Control control = page.widgetMap.get(widgetName);
                    Object value = XSWTDataBinding.getValueFromControl(control, null);
                    settings.put(format + ":" + widgetName, value.toString());
                }
            }
        }

        protected void restoreDialogSettings() {
            IDialogSettings settings = getDialogSettings();
            if (settings != null) {
                for (String widgetName : page.widgetMap.keySet()) {
                    Control control = page.widgetMap.get(widgetName);
                    String value = settings.get(format + ":" + widgetName);
                    if (value != null) {
                        try {
                            XSWTDataBinding.putValueIntoControl(control, value, null);
                        }
                        catch (Exception e) {
                            ScavePlugin.logError("Cannot restore exporter preference " + format + ":" + widgetName, e);
                        }
                    }
                }
            }
        }
    }
}