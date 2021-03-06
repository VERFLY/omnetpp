/*--------------------------------------------------------------*
  Copyright (C) 2006-2015 OpenSim Ltd.

  This file is distributed WITHOUT ANY WARRANTY. See the file
  'License' for details on this and other legal matters.
*--------------------------------------------------------------*/

package org.omnetpp.scave.engineext;

import java.util.EventListener;

public interface IResultFilesChangeListener extends EventListener {
    public void resultFileManagerChanged(ResultFileManagerChangeEvent event);
}
