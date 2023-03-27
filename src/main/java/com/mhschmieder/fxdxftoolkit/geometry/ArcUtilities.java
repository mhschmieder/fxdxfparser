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
package com.mhschmieder.fxdxftoolkit.geometry;

import org.apache.commons.math3.util.FastMath;

public final class ArcUtilities {

    public static EllipticalArc2D getArc( final double bulge,
                                          final double x,
                                          final double y,
                                          final double x2,
                                          final double y2 ) {
        if ( ( float ) bulge == 0f ) {
            return null;
        }

        final double cotan = 0.5d * ( ( 1.0d / bulge ) - bulge );
        final double centerX = 0.5d * ( ( x + x2 ) - ( ( y2 - y ) * cotan ) );
        final double centerY = 0.5d * ( ( y2 + y ) + ( ( x2 - x ) * cotan ) );
        final double radius = FastMath.hypot( centerX - x, centerY - y );

        double startAngle = FastMath.toDegrees( FastMath.atan( ( y - centerY ) / ( x - centerX ) ) );
        double endAngle = FastMath.toDegrees( FastMath.atan( ( y2 - centerY ) / ( x2 - centerX ) ) );

        // atan devuelve un ángulo correcto entre -pi/2 y pi/2 por tanto hay
        // que corregir el ángulo si x < 0
        if ( ( x - centerX ) < 0.0d ) {
            startAngle = 180d + startAngle;
        }
        else if ( startAngle < 0.0d ) {
            startAngle = 360d + startAngle;
        }

        if ( ( x2 - centerX ) < 0.0d ) {
            endAngle = 180d + endAngle;
        }
        else if ( endAngle < 0.0d ) {
            endAngle = 360d + endAngle;
        }

        if ( bulge < 0.0d ) {
            final double temp = endAngle;
            endAngle = startAngle;
            startAngle = temp;
        }

        return new EllipticalArc2D( centerX, centerY, radius, startAngle, endAngle );
    }

}// class ArcUtilities
