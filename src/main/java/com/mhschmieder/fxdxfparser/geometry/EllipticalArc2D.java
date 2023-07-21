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

import org.apache.commons.math3.util.FastMath;

import javafx.geometry.Point2D;

// Elliptical Arc in 2D space
public final class EllipticalArc2D {

    private final Point2D _center;
    private final Point2D _endMajorAxis;
    private final Point2D _endMinorAxis;

    private final double  _startAngle;
    private final double  _endAngle;

    // Supply a degenerate case, to avoid null pointers.
    public EllipticalArc2D() {
        _center = new Point2D( 0.0d, 0.0d );
        _endMajorAxis = new Point2D( 0.0d, 0.0d );
        _endMinorAxis = new Point2D( 0.0d, 0.0d );
        _startAngle = 0.0d;
        _endAngle = 0.0d;
    }

    public EllipticalArc2D( final double centerX,
                            final double centerY,
                            final double radius,
                            final double startAngle,
                            final double endAngle ) {
        _center = new Point2D( centerX, centerY );
        _endMajorAxis = new Point2D( radius, 0.0d );
        _endMinorAxis = new Point2D( 0.0d, radius );
        _startAngle = startAngle;
        _endAngle = endAngle;
    }

    public EllipticalArc2D( final double centerX,
                            final double centerY,
                            final Point2D endMajorAxis,
                            final Point2D endMinorAxis,
                            final double startAngle,
                            final double endAngle ) {
        _center = new Point2D( centerX, centerY );
        _endMajorAxis = endMajorAxis;
        _endMinorAxis = endMinorAxis;
        _startAngle = startAngle;
        _endAngle = endAngle;
    }

    public EllipticalArc2D( final Point2D center,
                            final Point2D endMajorAxis,
                            final Point2D endMinorAxis,
                            final double startAngle,
                            final double endAngle ) {
        _center = center;
        _endMajorAxis = endMajorAxis;
        _endMinorAxis = endMinorAxis;
        _startAngle = startAngle;
        _endAngle = endAngle;
    }

    public Point2D getCenter() {
        return _center;
    }

    public double getEndAngle() {
        return _endAngle;
    }

    public Point2D getEndMajorAxis() {
        return _endMajorAxis;
    }

    public Point2D getEndMinorAxis() {
        return _endMinorAxis;
    }

    public double getStartAngle() {
        return _startAngle;
    }

    public double getTotalAngle() {
        double totalAngle = _endAngle - _startAngle;
        if ( _endAngle < _startAngle ) {
            totalAngle += 360d;
        }
        return totalAngle;
    }

    public Vertex[] normalizeGradients( final double grads ) {
        final double normalizedGrads = ( ( float ) grads == 0f ) ? 1 : FastMath.min( 20.0d, grads );
        final double diff = getTotalAngle();
        final int numberOfVertices =
                FastMath.max( ( int ) FastMath.round( diff / normalizedGrads ) + 1, 2 );
        final double newgrads = diff / ( numberOfVertices - 1 );

        final Vertex[] vertices = new Vertex[ numberOfVertices ];
        final double cx = _center.getX();
        final double cy = _center.getY();
        final double miX = _endMinorAxis.getX();
        final double miY = _endMinorAxis.getY();
        final double maX = _endMajorAxis.getX();
        final double maY = _endMajorAxis.getY();
        double g = _startAngle;

        for ( int i = 0; i < numberOfVertices; i++, g += newgrads ) {
            g %= 360d;
            final double r = FastMath.toRadians( g );
            final double cosR = FastMath.cos( r );
            final double sinR = FastMath.sin( r );
            final double x = ( maX * cosR ) + ( miX * sinR ) + cx;
            final double y = ( maY * cosR ) + ( miY * sinR ) + cy;
            final Vertex vertex = new Vertex( x, y );
            vertices[ i ] = vertex;
        }

        return vertices;
    }

}// class EllipticalArc2D
