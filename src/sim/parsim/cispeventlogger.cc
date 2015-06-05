//=========================================================================
//  CISPEVENTLOGGER.CC - part of
//
//                  OMNeT++/OMNEST
//           Discrete System Simulation in C++
//
//  Author: Andras Varga, 2003
//          Dept. of Electrical and Computer Systems Engineering,
//          Monash University, Melbourne, Australia
//
//=========================================================================

/*--------------------------------------------------------------*
  Copyright (C) 2003-2015 Andras Varga
  Copyright (C) 2006-2015 OpenSim Ltd.

  This file is distributed WITHOUT ANY WARRANTY. See the file
  `license' for details on this and other legal matters.
*--------------------------------------------------------------*/

#include <cstdio>
#include "omnetpp/cparsimcomm.h"
#include "omnetpp/cmodule.h"
#include "omnetpp/globals.h"
#include "omnetpp/regmacros.h"
#include "cispeventlogger.h"
#include "cidealsimulationprot.h"
#include "cplaceholdermod.h"

NAMESPACE_BEGIN

Register_Class(cISPEventLogger);

cISPEventLogger::cISPEventLogger() : cNullMessageProtocol()
{
    fout = nullptr;
}

cISPEventLogger::~cISPEventLogger()
{
}

void cISPEventLogger::startRun()
{
    cNullMessageProtocol::startRun();

    char fname[200];
    sprintf(fname, "ispeventlog-%d.dat", comm->getProcId());
    fout = fopen(fname, "wb");
    if (!fout)
        throw cRuntimeError("cISPEventLogger error: cannot open file `%s' for write", fname);
}

void cISPEventLogger::endRun()
{
    cNullMessageProtocol::endRun();
    fclose(fout);
}

void cISPEventLogger::processReceivedMessage(cMessage *msg, int destModuleId, int destGateId, int sourceProcId)
{
    msg->setSchedulingPriority(sourceProcId);
    cParsimProtocolBase::processReceivedMessage(msg, destModuleId, destGateId, sourceProcId);
}

void cISPEventLogger::processOutgoingMessage(cMessage *msg, int procId, int moduleId, int gateId, void *data)
{
    if (msg->getSchedulingPriority() != 0)
        throw cRuntimeError("cISPEventLogger: outgoing message (%s)%s has nonzero priority set -- "
                            "this conflicts with ISP which uses priority for its own purposes",
                            msg->getClassName(), msg->getName());
    cParsimProtocolBase::processOutgoingMessage(msg, procId, moduleId, gateId, data);
}

cEvent *cISPEventLogger::takeNextEvent()
{
    cEvent *event = cNullMessageProtocol::takeNextEvent();

    if (event->getSrcProcId() != -1) {  // received from another partition
        // restore original priority
        event->setSchedulingPriority(0);

        // log event to file
        cIdealSimulationProtocol::ExternalEvent e;
        e.t = event->getArrivalTime();
        e.srcProcId = event->getSrcProcId();

        if (fwrite(&e, sizeof(cIdealSimulationProtocol::ExternalEvent), 1, fout) < 1)
            throw cRuntimeError("cISPEventLogger: file write failed (disk full?)");
    }

    return event;
}

void cISPEventLogger::putBackEvent(cEvent *event)
{
    throw cRuntimeError("cISPEventLogger: \"Run Until Event/Module\" functionality cannot be "
                        "used with this scheduler (putBackEvent() not implemented)");
}

NAMESPACE_END

