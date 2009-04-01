package org.omnetpp.runtimeenv.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.omnetpp.runtimeenv.Activator;


/**
 * Handler for the "Run" action
 * @author Andras
 */
public class RunHandler extends AbstractHandler {

    public RunHandler() {
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
	    Activator.getSimulationManager().run();
	    return null;
	}
}
