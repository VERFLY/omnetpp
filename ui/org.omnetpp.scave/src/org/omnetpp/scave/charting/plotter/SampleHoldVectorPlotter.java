/*--------------------------------------------------------------*
  Copyright (C) 2006-2015 OpenSim Ltd.

  This file is distributed WITHOUT ANY WARRANTY. See the file
  'License' for details on this and other legal matters.
*--------------------------------------------------------------*/

package org.omnetpp.scave.charting.plotter;

import org.eclipse.draw2d.Graphics;
import org.eclipse.swt.SWT;
import org.omnetpp.common.canvas.ICoordsMapping;
import org.omnetpp.common.canvas.LargeGraphics;
import org.omnetpp.scave.charting.ILinePlot;
import org.omnetpp.scave.charting.dataset.IXYDataset;

/**
 * Sample-hold vector plotter
 *
 * @author Andras
 */
public class SampleHoldVectorPlotter extends VectorPlotter {

    boolean backward;

    public SampleHoldVectorPlotter(boolean backward) {
        this.backward = backward;
    }

    public boolean plot(ILinePlot plot, int series, Graphics graphics, ICoordsMapping mapping, IChartSymbol symbol, int timeLimitMillis) {
        IXYDataset dataset = plot.getDataset();
        int n = dataset.getItemCount(series);
        if (n==0)
            return true;

        // dataset index range to iterate over
        int[] range = indexRange(plot, series, graphics, mapping);
        int first = range[0], last = range[1];

        //
        // Performance optimization: avoid painting the same pixels over and over,
        // by maintaining prevX, minY and maxY.
        //
        // Note that we could spare a few extra cycles by merging in symbol drawing
        // (instead of calling drawSymbols()), but since "pin" mode doesn't make much
        // sense (doesn't show much) for huge amounts of data points, we don't bother.
        //
        long prevX = mapping.toCanvasX(plot.transformX(dataset.getX(series, first)));
        long prevY = mapping.toCanvasY(plot.transformY(dataset.getY(series, first)));
        boolean prevIsNaN = Double.isNaN(plot.transformY(dataset.getY(series, first)));
        long maxY = prevY;
        long minY = prevY;

        // We are drawing vertical/horizontal lines, so turn off antialias (it is slow).
        int origAntialias = graphics.getAntialias();
        graphics.setAntialias(SWT.OFF);

        int[] dots = new int[] {1,2};
        graphics.setLineDash(dots);

        if (!prevIsNaN && backward)
            LargeGraphics.drawPoint(graphics, prevX, prevY);

        long startTime = System.currentTimeMillis();

        for (int i = first+1; i <= last; i++) {
            if ((i & 255)==0 && System.currentTimeMillis() - startTime > timeLimitMillis)
                return false; // timed out

            double value = plot.transformY(dataset.getY(series, i));

            // for testing:
            //if (i%5==0) value = 0.0/0.0; //NaN

            boolean isNaN = Double.isNaN(value); // see isNaN handling later

            long x = mapping.toCanvasX(plot.transformX(dataset.getX(series, i)));
            long y = mapping.toCanvasY(value); // note: this maps +-INF to +-MAXPIX, which works out just fine here

            // for testing:
            //if (i%5==1) x = prevX;

            if (x != prevX) {
                if (!isNaN && backward) {
                    graphics.setLineStyle(SWT.LINE_SOLID);
                    LargeGraphics.drawLine(graphics, prevX, y, x, y); // horizontal

                    graphics.setLineDash(dots);
                    if (!prevIsNaN) {
                        LargeGraphics.drawLine(graphics, prevX, prevY, prevX, y); // vertical
                    }
                }
                else if (!prevIsNaN && !backward) { // forward
                    graphics.setLineStyle(SWT.LINE_SOLID);
                    LargeGraphics.drawLine(graphics, prevX, prevY, x, prevY); // horizontal

                    graphics.setLineDash(dots);
                    if (!isNaN) {
                        LargeGraphics.drawLine(graphics, x, prevY, x, y); // vertical
                    }
                }

                minY = maxY = y;
            }
            else if (!isNaN) {
                // same x coord, only vertical line needs to be drawn
                if (y < minY) {
                    LargeGraphics.drawLine(graphics, x, minY, x, y);  // in lineDash(dots) mode
                    minY = y;
                }
                else if (y > maxY) {
                    LargeGraphics.drawLine(graphics, x, maxY, x, y);  // in lineDash(dots) mode
                    maxY = y;
                }
            }

            if (!isNaN) {  // condition is to handle case when first value on this x is NaN
                prevX = x;
                prevY = y;
            }
            prevIsNaN = isNaN;
        }

        if (!prevIsNaN && !backward)
            LargeGraphics.drawPoint(graphics, prevX, prevY);

        // draw symbols
        graphics.setAntialias(origAntialias);
        graphics.setLineStyle(SWT.LINE_SOLID);

        int remainingTime = Math.max(0, timeLimitMillis - (int)(System.currentTimeMillis()-startTime));
        return plotSymbols(plot, series, graphics, mapping, symbol, remainingTime);
    }
}
