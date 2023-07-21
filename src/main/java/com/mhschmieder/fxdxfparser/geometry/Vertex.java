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
package com.mhschmieder.fxdxfparser.geometry;

public class Vertex extends Point {

    public double _startWidth;
    public double _endWidth;

    public Vertex() {
        this( 0.0d, 0.0d, 0.0d, 0.0d );
    }

    public Vertex( final double x, final double y ) {
        this( x, y, 0.0d, 0.0d );
    }

    public Vertex( final double x, final double y, final double z ) {
        this( x, y, z, 0.0d, 0.0d );
    }

    public Vertex( final double x,
                   final double y,
                   final double pStartWidth,
                   final double pEndWidth ) {
        this( x, y, 0.0d, pStartWidth, pEndWidth );
    }

    public Vertex( final double x,
                   final double y,
                   final double z,
                   final double startWidth,
                   final double endWidth ) {
        super( x, y, z );

        _startWidth = startWidth;
        _endWidth = endWidth;
    }

    public final double getEndWidth() {
        return _endWidth;
    }

    public final double getStartWidth() {
        return _startWidth;
    }

    public final void setEndWidth( final double endWidth ) {
        _endWidth = endWidth;
    }

    public final void setStartWidth( final double startWidth ) {
        _startWidth = startWidth;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString() {
        final String pointVerbose = super.toString();
        final String vertexVerbose = pointVerbose + " " + _startWidth + " " + _endWidth;
        return vertexVerbose;
    }

}// class Vertex
