package org.omnetpp.runtimeenv;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.StatusLineContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.omnetpp.experimental.simkernel.swig.cMessage;
import org.omnetpp.experimental.simkernel.swig.cModuleType;
import org.omnetpp.experimental.simkernel.swig.cSimulation;

/**
 * 
 * @author Andras
 */
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    public ActionBarAdvisor createActionBarAdvisor(
            IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }

    public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(800, 600));
        configurer.setShowCoolBar(true);
        configurer.setShowStatusLine(true);
        configurer.setTitle("OMNeT++ Simulation");
    }

    @Override
    public void createWindowContents(Shell shell) {
        super.createWindowContents(shell);

        class LabelContribution extends ControlContribution {
            private String tooltip;
            private Label label;
            public LabelContribution(String id, String tooltip) {
                super(id);
                this.tooltip = tooltip;
            }
            @Override
            protected Control createControl(Composite parent) {
                label = new Label(parent, SWT.BORDER | SWT.LEFT);
                if (tooltip != null) 
                    label.setToolTipText(tooltip);
                label.setText("                                      "); //XXX 
                return label;
            }
            public Label getLabel() { 
                return label; 
            }
        }

        // experimental toolbar contributions
        ICoolBarManager coolBarManager = getWindowConfigurer().getActionBarConfigurer().getCoolBarManager();
        ToolBarManager toolBarManager = new ToolBarManager();
        coolBarManager.add(toolBarManager);
        final LabelContribution simTime = new LabelContribution(null, "Simulation Time");
        final LabelContribution eventNumber = new LabelContribution(null, "Event Number");
        final LabelContribution networkName = new LabelContribution(null, "Network Name");
        final LabelContribution messageCounters = new LabelContribution(null, "Message counts: scheduled / existing / created");
        toolBarManager.add(simTime);
        toolBarManager.add(eventNumber);
        toolBarManager.add(networkName);
        toolBarManager.add(messageCounters);

        Activator.getSimulationManager().addChangeListener(new ISimulationListener() {
            @Override
            public void changed() {
                simTime.getLabel().setText(" T="+cSimulation.getActiveSimulation().getSimTime() + " ");
                eventNumber.getLabel().setText(" #"+cSimulation.getActiveSimulation().getEventNumber() + " ");
                cModuleType networkType = cSimulation.getActiveSimulation().getNetworkType();
                networkName.getLabel().setText(" "+ (networkType != null ? networkType.getFullName() : "(no network set up)") + " ");
                messageCounters.getLabel().setText(
                        " " + cSimulation.getActiveSimulation().getMessageQueue().getLength() + 
                        " / " + cMessage.getLiveMessageCount() + 
                        " / " + cMessage.getTotalMessageCount() + " ");
            }
        });

        // experimental statusbar contributions
        IStatusLineManager statusLineManager = getWindowConfigurer().getActionBarConfigurer().getStatusLineManager();
        final StatusLineContributionItem item = new StatusLineContributionItem(null);
        statusLineManager.add(item);

        Activator.getSimulationManager().addChangeListener(new ISimulationListener() {
            @Override
            public void changed() {
                String text = "T=" + cSimulation.getActiveSimulation().getSimTime();
                item.setText(text);
            }
        });
    }
}
