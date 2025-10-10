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
import com.mhschmieder.fxdxfparser.geometry.DxfShapeContainer;
import com.mhschmieder.fxdxfparser.geometry.EllipticalArc2D;
import com.mhschmieder.fxdxfparser.geometry.PolylineUtilities;
import com.mhschmieder.fxdxfparser.geometry.Vertex;
import com.mhschmieder.fxdxfparser.loader.DxfLineType;
import com.mhschmieder.fxdxfparser.reader.DxfGroupCodes;
import com.mhschmieder.fxdxfparser.reader.DxfPairContainer;
import com.mhschmieder.fxdxfparser.reader.DxfReaderException;
import com.mhschmieder.fxdxfparser.reader.EntityType;
import com.mhschmieder.fxdxfparser.structure.DxfDocument;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.apache.commons.math3.util.FastMath;

public class DxfEllipse extends DxfEntity {

    protected double _centerX;
    protected double _centerY;
    protected double _centerZ;

    protected double _endMajorAxisOffsetX;
    protected double _endMajorAxisOffsetY;
    protected double _endMajorAxisOffsetZ;

    protected double _normalX;
    protected double _normalY;
    protected double _normalZ;

    protected double _ratioMinorAxis;

    protected double _startAngle;
    protected double _endAngle;

    public DxfEllipse( final DxfDocument doc,
                       final DxfPairContainer pc,
                       final EntityType entityType,
                       final boolean ignorePaperSpace )
            throws DxfReaderException {
        super( doc, pc, entityType, ignorePaperSpace );
    }

    @Override
    public boolean convertToFxShapes( final DxfShapeContainer dxfShapeContainer,
                                      final Affine transform,
                                      final double strokeScale ) {
        if ( !_dxfDoc.getLayer( _layer ).isLayerOn() ) {
            return false;
        }

        final Color color = getColor();
        final DxfLineType lineType = getLineType();

        final boolean closed = ( ( _endAngle - _startAngle ) >= 360d );

        // TODO: Find out why Block References have a translational offset
        // issue along the y-axis, for open and closed ellipses, and are
        // affected dramatically by transform order, and then maybe simplify the
        // transform order to apply the passed in transform last, if we continue
        // to bypass direct shape translation when the passed-in transform is
        // not the identify matrix (as is the case for a Block Reference).
        if ( ( lineType == null ) || lineType.isContinuous() ) {
            if ( closed ) {
                if ( _ratioMinorAxis == 1 ) {
                    final double radius = FastMath.hypot( _endMajorAxisOffsetX, _endMajorAxisOffsetY );
                    final Circle circle = new Circle( _centerX, _centerY, radius );

                    circle.getTransforms().add( transform );
                    circle.setStroke( color );

                    // Ellipse is an outline-only entity.
                    circle.setFill( null );

                    dxfShapeContainer.addShape( strokeScale, circle );

                    return true;
                }
                else if ( transform.isIdentity() ) {
                    // NOTE: The ellipse must be made at the zero origin so
                    // that the rotation and translation factors can be applied
                    // in the correct order.
                    final double radiusMajor = FastMath.hypot( _endMajorAxisOffsetX,
                                                           _endMajorAxisOffsetY );
                    final double radiusMinor = radiusMajor * _ratioMinorAxis;
                    final Ellipse ellipse = new Ellipse( 0.0d, 0.0d, radiusMajor, radiusMinor );

                    // Find the rotation of the ellipse via the major axis, and
                    // translate to its center, in the correct order.
                    final double theta = FastMath.atan2( _endMajorAxisOffsetY, _endMajorAxisOffsetX );
                    final Rotate rotate = new Rotate( FastMath.toDegrees( theta ) );
                    final Translate translate = new Translate( _centerX, _centerY );

                    // NOTE: The transform that is passed in must be applied
                    // last (which means we must add it first), as it could be
                    // from a Block Reference, but this also means that we might
                    // see a translational offset that is incorrect?
                    final ObservableList< Transform > transforms = ellipse.getTransforms();
                    transforms.add( transform );
                    transforms.add( translate );
                    transforms.add( rotate );

                    ellipse.setStroke( color );

                    // Ellipse is an outline-only entity.
                    ellipse.setFill( null );

                    dxfShapeContainer.addShape( strokeScale, ellipse );

                    return true;
                }
            }
            else if ( transform.isIdentity() ) {
                // Both DXF and JavaFX set zero degrees to the positive x-axis
                // and measure angles counter-clockwise, but JavaFX confuses
                // matters by asking for an angle extent instead of an end
                // angle, so we essentially have to invert the angle
                // differential and then do a simple test to determine angle
                // stroke direction (which we compensate using a 360 degree
                // offset). Accurate documentation was hard to come by!
                // NOTE: The arc must be made at the zero origin so that the
                // rotation and translation factors can be applied in the
                // correct order.
                final double radiusMajor = FastMath.hypot( _endMajorAxisOffsetX, _endMajorAxisOffsetY );
                final double radiusMinor = radiusMajor * _ratioMinorAxis;
                double arcExtentDeg = _startAngle - _endAngle;
                if ( _endAngle < _startAngle ) {
                    arcExtentDeg -= 360d;
                }
                final Arc arc = new Arc( 0.0d, // _centerX,
                                         0.0d, // _centerY,
                                         radiusMajor,
                                         radiusMinor,
                                         -_startAngle,
                                         arcExtentDeg );

                // Find the rotation of the arc via the major axis, and
                // translate to its center, in the correct order.
                // TODO: verify the 90 degree offset of the rotation angle. We
                // have only one test file that hits this code so far and it
                // doesn't work correctly otherwise. But there doesn't seem to
                // be a strong rationale for why the Normal to the Major Axis
                // angle relative to the x-axis is needed.
                final double theta = FastMath.atan2( _endMajorAxisOffsetY, _endMajorAxisOffsetX );
                final Rotate rotate = new Rotate( FastMath.toDegrees( theta ) - 90d );
                final Translate translate = new Translate( _centerX, _centerY );

                // NOTE: The transform that is passed in must be applied last
                // (which means we must add it first), as it could be from a
                // Block Reference, but this also means that we might see a
                // translational offset that is incorrect?
                final ObservableList< Transform > transforms = arc.getTransforms();
                transforms.add( transform );
                transforms.add( translate );
                transforms.add( rotate );

                arc.setStroke( color );

                // Ellipse is an outline-only entity.
                arc.setFill( null );

                dxfShapeContainer.addShape( strokeScale, arc );

                return true;
            }
        }

        /*
         * Calculamos el vector del eje menor: Rotamos el eje mayor 90 grados
         * alrededor del centro.
         */
        final Affine at = new Affine();
        at.appendRotation( 90d, 0.0d, 0.0d );
        at.appendScale( _ratioMinorAxis, _ratioMinorAxis );

        final Point2D endMajorAxis = new Point2D( _endMajorAxisOffsetX, _endMajorAxisOffsetY );
        final Point2D endMinorAxis = at.transform( endMajorAxis );

        final EllipticalArc2D arc = new EllipticalArc2D( _centerX,
                                                         _centerY,
                                                         endMajorAxis,
                                                         endMinorAxis,
                                                         _startAngle,
                                                         _endAngle );
        final Vertex[] vertices = arc.normalizeGradients( PolylineUtilities.NUMBER_OF_GRADS );

        final double lineTypeScale = _dxfDoc.getGlobalLineTypeScale() * _lineTypeScale;
        PolylineUtilities.convertToFxShapes( dxfShapeContainer,
                                             transform,
                                             strokeScale,
                                             color,
                                             lineType,
                                             lineTypeScale,
                                             vertices,
                                             closed );

        return true;
    }

    @Override
    @SuppressWarnings("nls")
    protected void parseEntityProperties( final DxfPairContainer pc ) {
        _centerX = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE10 ) );
        _centerY = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE20 ) );
        _centerZ = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE30 ) );

        _endMajorAxisOffsetX = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE11 ) );
        _endMajorAxisOffsetY = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE21 ) );
        _endMajorAxisOffsetZ = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE31 ) );

        _normalX = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.NORMAL_X, "0" ) );
        _normalY = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.NORMAL_Y, "0" ) );
        _normalZ = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.NORMAL_Z, "0" ) );

        _ratioMinorAxis = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE40 ) );

        _startAngle = FastMath.toDegrees( NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE41 ) ) );
        _endAngle = FastMath.toDegrees( NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE42 ) ) );
    }

}// class DxfEllipse
