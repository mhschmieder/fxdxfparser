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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mhschmieder.fxdxftoolkit.entity.DxfEntity;
import com.mhschmieder.fxdxftoolkit.entity.DxfVertex;
import com.mhschmieder.fxdxftoolkit.loader.DxfLineType;

import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.transform.Affine;

public final class PolylineUtilities {

    public static final int NUMBER_OF_GRADS = 5;

    public static int compareVertex2D( final PolyVertex pv1, final PolyVertex pv2 ) {
        return ( ( pv1._x == pv2._x ) && ( pv1._y == pv2._y ) ) ? 0 : 1;
    }

    public static void convertToFxShapes( final FxShapeContainer fxShapeContainer,
                                          final Affine transform,
                                          final double strokeScale,
                                          final Color color,
                                          final DxfLineType lineType,
                                          final double lineTypeScale,
                                          final Vertex[] vertices,
                                          final boolean closedPolyline ) {
        // NOTE: This is duplicated logic in some contexts, as it is too
        // complicated at the moment to refactor the structure of the calls and
        // also risky to other potential targets other than 2D JavaFX Geometry.
        if ( ( lineType == null ) || lineType.isContinuous() ) {
            if ( ( vertices == null ) || ( vertices.length < 1 ) ) {
                return;
            }
        }

        final int numberOfVertices = vertices.length;
        final int numberOfCoordinates = 2 * numberOfVertices;
        final List< Double > coordinates = new ArrayList<>( numberOfCoordinates );
        for ( int i = 0; i < numberOfVertices; i++ ) {
            final int j = 2 * i;
            final Vertex vertex = vertices[ i ];
            coordinates.add( j, Double.valueOf( vertex._x ) );
            coordinates.add( j + 1, Double.valueOf( vertex._y ) );
        }

        if ( closedPolyline ) {
            final Polygon polygon = new Polygon();
            final ObservableList< Double > polygonCoordinates = polygon.getPoints();
            polygonCoordinates.setAll( coordinates );

            // Polygons are supposed to be treated like a wireframe.
            polygon.setFill( null );

            polygon.getTransforms().add( transform );
            polygon.setStroke( color );

            if ( ( lineType != null ) && !lineType.isContinuous() ) {
                final Collection< Double > dashArrayCandidate = lineType
                        .makeDashArray( lineTypeScale );
                final ObservableList< Double > dashArray = polygon.getStrokeDashArray();
                dashArray.setAll( dashArrayCandidate );
            }

            fxShapeContainer.addShape( strokeScale, polygon );
        }
        else {
            final Polyline polyline = new Polyline();
            final ObservableList< Double > polylineCoordinates = polyline.getPoints();
            polylineCoordinates.setAll( coordinates );

            polyline.getTransforms().add( transform );
            polyline.setStroke( color );

            if ( ( lineType != null ) && !lineType.isContinuous() ) {
                final Collection< Double > dashArrayCandidate = lineType
                        .makeDashArray( lineTypeScale );
                final ObservableList< Double > dashArray = polyline.getStrokeDashArray();
                dashArray.setAll( dashArrayCandidate );
            }

            fxShapeContainer.addShape( strokeScale, polyline );
        }
    }

    public static boolean convertToFxShapes( final FxShapeContainer fxShapeContainer,
                                             final Affine transform,
                                             final double strokeScale,
                                             final DxfEntity entity,
                                             final boolean isVertex2D,
                                             final ArrayList< PolyVertex > polyVertices,
                                             final ArrayList< DxfVertex > dxfVertices,
                                             final boolean closedPolyline,
                                             final boolean hasWidth ) {
        final Color color = entity.getColor();
        final DxfLineType lineType = entity.getLineType();
        final double lineTypeScale =
                                   entity._dxfDoc.getGlobalLineTypeScale() * entity._lineTypeScale;

        if ( isVertex2D ) {
            final Vectorization vec = new Vectorization();
            vec.setGrads( NUMBER_OF_GRADS );
            final ArrayList< Vertex > vlist = makeVertexList( vec,
                                                              polyVertices,
                                                              closedPolyline,
                                                              hasWidth );
            if ( vlist.isEmpty() ) {
                return false;
            }

            final Vertex[] vertices = vlist.toArray( new Vertex[ vlist.size() ] );
            convertToFxShapes( fxShapeContainer,
                               transform,
                               strokeScale,
                               color,
                               lineType,
                               lineTypeScale,
                               vertices,
                               closedPolyline );

            return true;
        }

        // These were originally 3D points in AutoCAD, so LineType and other 2D
        // flags don't apply, meaning we can just go straight ahead and simply
        // these into regular Polylines and Polygons.
        final ArrayList< Vertex > vertarray = new ArrayList<>( dxfVertices.size() );
        for ( final DxfVertex dxfVertex : dxfVertices ) {
            if ( dxfVertex != null ) {
                final Vertex vertex = new Vertex( dxfVertex._x, dxfVertex._y );
                vertarray.add( vertex );
            }
        }

        final Vertex[] vertices = vertarray.toArray( new Vertex[ vertarray.size() ] );

        convertToFxShapes( fxShapeContainer,
                           transform,
                           strokeScale,
                           color,
                           lineType,
                           lineTypeScale,
                           vertices,
                           closedPolyline );

        return true;
    }

    public static double getBulge( final Object pv ) {
        if ( pv instanceof DxfVertex ) {
            final DxfVertex v = ( DxfVertex ) pv;
            final double bulge = v.getBulge();
            return bulge;
        }
        else if ( pv instanceof PolyVertex ) {
            final PolyVertex v = ( PolyVertex ) pv;
            return v._bulge;
        }
        else {
            return 0.0d;
        }
    }

    public static double getX( final Object pv ) {
        if ( pv instanceof DxfVertex ) {
            final DxfVertex v = ( DxfVertex ) pv;
            return v._x;
        }
        else if ( pv instanceof PolyVertex ) {
            final PolyVertex v = ( PolyVertex ) pv;
            return v._x;
        }
        else {
            return 0.0d;
        }
    }

    public static double getY( final Object pv ) {
        if ( pv instanceof DxfVertex ) {
            final DxfVertex v = ( DxfVertex ) pv;
            return v._y;
        }
        else if ( pv instanceof PolyVertex ) {
            final PolyVertex v = ( PolyVertex ) pv;
            return v._y;
        }
        else {
            return 0.0d;
        }
    }

    public static ArrayList< Vertex > makeVertexList( final Vectorization vectorization,
                                                      final ArrayList< PolyVertex > vertexlist,
                                                      final boolean closed,
                                                      final boolean hasWidth ) {
        // Lista de vértices
        final ArrayList< Vertex > vlist = new ArrayList<>( 4 );
        vlist.add( null ); // El elemento será sobrescrito con vlist.set

        final VectorizationMode mode = vectorization.getMode();
        int iStrip = 0; // índice del vértice actual de la Strip line

        int vi = 0;
        final int len = vertexlist.size();
        while ( vi < len ) {
            final PolyVertex vert1 = vertexlist.get( vi );
            if ( vert1 == null ) {
                continue;
            }

            final int vix = ( vi + 1 ) % len;

            final PolyVertex vert2 = vertexlist.get( vix );
            if ( vert2 == null ) {
                continue;
            }

            if ( vert1._bulge == 0 ) {
                final Vertex v1 = new Vertex( vert1._x,
                                              vert1._y,
                                              vert1._startWidth,
                                              vert1._endWidth );
                final Vertex v2 = new Vertex( vert2._x,
                                              vert2._y,
                                              vert2._startWidth,
                                              vert2._endWidth );

                // Actualizar lista de vértices
                vlist.set( iStrip, v1 );
                iStrip++;
                vlist.add( iStrip, v2 );
            }
            else {
                final EllipticalArc2D arc = vert1.getArc( vert2._x, vert2._y );

                double grads = 0.0d;
                switch ( mode ) {
                case GRADS:
                    grads = vectorization.getGrads();
                    break;
                case SCALE_GRADS:
                    grads = vectorization.getScaleGrads() / arc.getEndMajorAxis().getX();
                    break;
                case VERTEX:
                    grads = arc.getTotalAngle() / vectorization.getVertexCount();
                    break;
                default:
                    break;
                }

                final Vertex[] vectaux = arc.normalizeGradients( grads );

                final boolean reverse = vert1._bulge < 0;
                final Vertex[] vect = process( vectaux,
                                               vert1._startWidth,
                                               vert1._endWidth,
                                               vert2._startWidth,
                                               vert2._endWidth,
                                               reverse,
                                               hasWidth );

                // Actualizar lista de vértices
                final int vectlen = vect.length;
                vlist.set( iStrip, vect[ 0 ] );
                for ( int n = 1; n < vectlen; n++ ) {
                    vlist.add( iStrip + n, vect[ n ] );
                }
                iStrip += vectlen - 1;
            }

            if ( ( ++vi == ( len - 1 ) ) && !closed ) {
                break;
            }
        }

        return vlist;
    }

    private static Vertex[] process( final Vertex[] coords,
                                     final double start1,
                                     final double end1,
                                     final double start2,
                                     final double end2,
                                     final boolean reverse,
                                     final boolean hasWidth ) {
        final int numberOfCoordinates = coords.length;
        final int incr = reverse ? -1 : 1;
        final int init = reverse ? numberOfCoordinates - 1 : 0;
        final int fin = reverse ? -1 : numberOfCoordinates;

        final Vertex[] values = new Vertex[ numberOfCoordinates ];

        int n = 0;
        if ( hasWidth ) {
            // incremento del grosor por vértice
            final double widthIncrease = ( end1 - start1 ) / numberOfCoordinates;
            double tempstart = start1;
            double tempend = start1 + widthIncrease;
            for ( int i = init; i != fin; i += incr, n++ ) {
                final Vertex point = coords[ i ];
                values[ n ] = new Vertex( point._x, point._y, tempstart, tempend );
                tempstart = tempend;
                tempend = tempstart + widthIncrease;
            }
        }
        else {
            for ( int i = init; i != fin; i += incr, n++ ) {
                final Vertex point = coords[ i ];
                values[ n ] = new Vertex( point._x, point._y, 0.0d, 0.0d );
            }
        }
        return values;
    }

}// class PolylineUtilities
