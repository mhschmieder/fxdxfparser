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
 * This file is part of the FxDxfToolkit Library
 *
 * You should have received a copy of the MIT License along with the
 * FxDxfToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxdxftoolkit
 */
package com.mhschmieder.fxdxfparser.geometry;

public final class PolyVertex extends Vertex {

    public double _bulge;

    public PolyVertex() {
        this( 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d );
    }

    public PolyVertex( final double x,
                       final double y,
                       final double startWidth,
                       final double endWidth,
                       final double bulge ) {
        this( x, y, 0.0d, startWidth, endWidth, bulge );
    }

    public PolyVertex( final double x,
                       final double y,
                       final double z,
                       final double startWidth,
                       final double endWidth,
                       final double bulge ) {
        // Always call the superclass constructor first!
        super( x, y, z, startWidth, endWidth );

        _bulge = bulge;
    }

    public EllipticalArc2D getArc( final double x2, final double y2 ) {
        return ArcUtilities.getArc( _bulge, _x, _y, x2, y2 );
    }

    public double getBulge() {
        return _bulge;
    }

    public void setBulge( final double bulge ) {
        _bulge = bulge;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString() {
        final String vertexVerbose = super.toString();
        final String polyVertexVerbose = vertexVerbose + " " + _bulge;
        return polyVertexVerbose;
    }

}// class PolyVertex
