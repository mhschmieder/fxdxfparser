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
package com.mhschmieder.fxdxfparser.entity;

import java.util.ArrayList;
import java.util.List;

import com.mhschmieder.fxdxfparser.geometry.FxShapeContainer;
import com.mhschmieder.fxdxfparser.reader.DxfGroupCodes;
import com.mhschmieder.fxdxfparser.reader.DxfPairContainer;
import com.mhschmieder.fxdxfparser.reader.DxfReaderException;
import com.mhschmieder.fxdxfparser.reader.EntityType;
import com.mhschmieder.fxdxfparser.structure.DxfDocument;

import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.transform.Affine;

public class DxfPolygonMesh extends DxfPolyline {

    private static final int FLAG_MCLOSED = 1;
    private static final int FLAG_NCLOSED = 32;

    protected int            _mCount;
    protected int            _nCount;
    protected int            _mDensity;
    protected int            _nDensity;

    public DxfPolygonMesh( final DxfDocument doc,
                           final DxfPairContainer pc,
                           final EntityType entityType,
                           final boolean ignorePaperSpace )
            throws DxfReaderException {
        super( doc, pc, entityType, ignorePaperSpace );
    }

    @Override
    public boolean convertToFxShapes( final FxShapeContainer fxShapeContainer,
                                      final Affine transform,
                                      final double strokeScale ) {
        if ( !_dxfDoc.getLayer( _layer ).isLayerOn() ) {
            return false;
        }

        final Color color = getColor();
        for ( int n = 0; n < _nCount; n++ ) {
            final List< Double > coordinates = new ArrayList<>( 2 * _mCount );
            for ( int m = 0; m < _mCount; m++ ) {
                final DxfVertex vertex = _vertices.get( ( _nCount * m ) + n );

                final int j = 2 * m;
                coordinates.add( j, Double.valueOf( vertex._x ) );
                coordinates.add( j + 1, Double.valueOf( vertex._y ) );
            }

            if ( ( _polyFlags & FLAG_MCLOSED ) != 0 ) {
                final Polygon polygon = new Polygon();
                final ObservableList< Double > polygonCoordinates = polygon.getPoints();
                polygonCoordinates.addAll( coordinates );

                // Polygon3D is supposed to be treated like a wireframe.
                polygon.setFill( null );

                polygon.getTransforms().add( transform );
                polygon.setStroke( color );

                fxShapeContainer.addShape( strokeScale, polygon );
            }
            else {
                final Polyline polyline = new Polyline();
                final ObservableList< Double > polylineCoordinates = polyline.getPoints();
                polylineCoordinates.addAll( coordinates );

                polyline.getTransforms().add( transform );
                polyline.setStroke( color );

                fxShapeContainer.addShape( strokeScale, polyline );
            }
        }

        for ( int m = 0; m < _mCount; m++ ) {
            final List< Double > coordinates = new ArrayList<>( 2 * _nCount );
            for ( int n = 0; n < _nCount; n++ ) {
                final DxfVertex vertex = _vertices.get( ( _nCount * m ) + n );

                final int j = 2 * n;
                coordinates.add( j, Double.valueOf( vertex._x ) );
                coordinates.add( j + 1, Double.valueOf( vertex._y ) );
            }

            if ( ( _polyFlags & FLAG_NCLOSED ) != 0 ) {
                final Polygon polygon = new Polygon();
                final ObservableList< Double > polygonCoordinates = polygon.getPoints();
                polygonCoordinates.addAll( coordinates );

                // By default, Polygons are filled, but we only want a
                // wireframe (to be verified).
                polygon.setFill( null );

                polygon.getTransforms().add( transform );
                polygon.setStroke( color );

                fxShapeContainer.addShape( strokeScale, polygon );
            }
            else {
                final Polyline polyline = new Polyline();
                final ObservableList< Double > polylineCoordinates = polyline.getPoints();
                polylineCoordinates.addAll( coordinates );

                polyline.getTransforms().add( transform );
                polyline.setStroke( color );

                fxShapeContainer.addShape( strokeScale, polyline );
            }
        }

        return true;
    }

    @Override
    @SuppressWarnings("nls")
    protected void parseEntityProperties( final DxfPairContainer pc ) {
        super.parseEntityProperties( pc );

        _mCount = Integer.parseInt( pc.getValue( DxfGroupCodes.CODE71 ) );
        _nCount = Integer.parseInt( pc.getValue( DxfGroupCodes.CODE72 ) );
        _mDensity = Integer.parseInt( pc.getValue( DxfGroupCodes.CODE73, "0" ) );
        _nDensity = Integer.parseInt( pc.getValue( DxfGroupCodes.CODE74, "0" ) );
    }

}// class DxfPolygonMesh
