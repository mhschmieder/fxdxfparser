/**
 * MIT License
 *
 * Copyright (c) 2020, 2023 Mark Schmieder
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * This file is part of the FxDxfParser Library
 *
 * You should have received a copy of the MIT License along with the
 * FxDxfParser Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxdxfparser
 */
package com.mhschmieder.fxdxfparser.entity;

import com.mhschmieder.commonstoolkit.lang.NumberUtilities;
import com.mhschmieder.fxdxfparser.geometry.ArcUtilities;
import com.mhschmieder.fxdxfparser.geometry.EllipticalArc2D;
import com.mhschmieder.fxdxfparser.reader.DxfGroupCodes;
import com.mhschmieder.fxdxfparser.reader.DxfPairContainer;
import com.mhschmieder.fxdxfparser.reader.DxfReaderException;
import com.mhschmieder.fxdxfparser.reader.EntityType;
import com.mhschmieder.fxdxfparser.structure.DxfDocument;

public class DxfVertex extends DxfPoint {

    public static final int FLAG_CONTROLPOINT = 16;
    public static final int FLAG_3DPOLYLINE   = 32;
    public static final int FLAG_3DPOLYGON    = 64;
    public static final int FLAG_3DPOLYFACE   = 128;

    protected int           _flags;

    private double          _startWidth;
    private double          _endWidth;
    private double          _bulge;

    public DxfVertex( final DxfDocument pdoc,
                      final DxfPairContainer pc,
                      final EntityType entityType,
                      final boolean ignorePaperSpace )
            throws DxfReaderException {
        super( pdoc, pc, entityType, ignorePaperSpace );
    }

    // TODO: Find out if this method should be called somewhere.
    public final EllipticalArc2D getArc( final double x2, final double y2 ) {
        final double bulge = getBulge();
        return ArcUtilities.getArc( bulge, _x, _y, x2, y2 );
    }

    public final double getBulge() {
        final double bulge = isVertex2D() ? _bulge : 0.0d;
        return bulge;
    }

    public final double getEndWidth() {
        final double endWidth = isVertex2D() ? _endWidth : 0.0d;
        return endWidth;
    }

    public final int getFlags() {
        return _flags;
    }

    public final double getStartWidth() {
        final double startWidth = isVertex2D() ? _startWidth : 0.0d;
        return startWidth;
    }

    public final boolean isControlPoint() {
        return ( ( _flags & FLAG_CONTROLPOINT ) != 0 );
    }

    public final boolean isVertex2D() {
        return ( _flags < FLAG_3DPOLYLINE );
    }

    @Override
    @SuppressWarnings("nls")
    protected final void parseEntityProperties( final DxfPairContainer pc ) {
        super.parseEntityProperties( pc );

        _startWidth = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE40, "0" ) );
        _endWidth = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE41, "0" ) );
        _bulge = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE42, "0" ) );

        _flags = NumberUtilities.parseInteger( pc.getValue( DxfGroupCodes.FLAGS, "0" ) );
    }

}// class DxfVertex
