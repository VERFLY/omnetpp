//==========================================================================
//  INSPECTORLISTBOXVIEW.H - part of
//
//                     OMNeT++/OMNEST
//            Discrete System Simulation in C++
//
//==========================================================================

/*--------------------------------------------------------------*
  Copyright (C) 1992-2015 Andras Varga
  Copyright (C) 2006-2015 OpenSim Ltd.

  This file is distributed WITHOUT ANY WARRANTY. See the file
  `license' for details on this and other legal matters.
*--------------------------------------------------------------*/

#ifndef __OMNETPP_QTENV_INSPECTORLISTBOXVIEW_H
#define __OMNETPP_QTENV_INSPECTORLISTBOXVIEW_H

#include <QTableView>
#include "qtenvdefs.h"

namespace omnetpp {

class cObject;

namespace qtenv {

class InspectorListBox;

class QTENV_API InspectorListBoxView : public QTableView
{
    Q_OBJECT
private:
    bool isSetColumnWidth;
    cObject *object;
    int type;

protected:
    void contextMenuEvent(QContextMenuEvent *event);

public:
    InspectorListBoxView(QWidget *parent = nullptr);

    void setModel(QAbstractItemModel *model) override;
};

} // namespace qtenv
} // namespace omnetpp

#endif // __OMNETPP_QTENV_INSPECTORLISTBOXVIEW_H
