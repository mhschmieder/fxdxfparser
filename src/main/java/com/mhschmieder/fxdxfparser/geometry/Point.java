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

/**
 * This is a stripped down representation of a 3D Point, when we need to avoid
 * unnecessary generation of Scene Graph Nodes or heavy objects.
 */
public class Point {

    public static final double DEFAULT_TOLERANCE = 0.0001d;

    public double              _x;
    public double              _y;
    public double              _z;

    public Point() {
        this( 0.0d, 0.0d, 0.0d );
    }

    public Point( final double x, final double y, final double z ) {
        _x = x;
        _y = y;
        _z = z;
    }

    @Override
    public boolean equals( final Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final Point other = ( Point ) obj;

        return ( ( FastMath.abs( _x - other.getX() ) <= DEFAULT_TOLERANCE )
                && ( FastMath.abs( _y - other.getY() ) <= DEFAULT_TOLERANCE )
                && ( FastMath.abs( _z - other.getZ() ) <= DEFAULT_TOLERANCE ) );
    }

    /**
     * @return Returns the x-coordinate.
     */
    public final double getX() {
        return _x;
    }

    /**
     * @return Returns the y-coordinate.
     */
    public final double getY() {
        return _y;
    }

    /**
     * @return Returns the z-coordinate.
     */
    public final double getZ() {
        return _z;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits( _x );
        result = ( prime * result ) + ( int ) ( temp ^ ( temp >>> 32 ) );
        temp = Double.doubleToLongBits( _y );
        result = ( prime * result ) + ( int ) ( temp ^ ( temp >>> 32 ) );
        temp = Double.doubleToLongBits( _z );
        result = ( prime * result ) + ( int ) ( temp ^ ( temp >>> 32 ) );
        return result;
    }

    /**
     * @param x
     *            The x-coordinate to set.
     */
    public final void setX( final double x ) {
        _x = x;
    }

    /**
     * @param y
     *            The y-coordinate to set.
     */
    public final void setY( final double y ) {
        _y = y;
    }

    /**
     * @param z
     *            The z-coordinate to set.
     */
    public final void setZ( final double z ) {
        _z = z;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return super.toString() + "[" + _x + ", " + _y + ", " + _z + "]";
    }

}// class Point
