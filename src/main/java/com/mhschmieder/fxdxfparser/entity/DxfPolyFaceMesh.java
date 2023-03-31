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
import javafx.scene.transform.Affine;

public class DxfPolyFaceMesh extends DxfPolyline {

    protected ArrayList< DxfFaceDef > _faces;

    protected int                     _numberOfVertices;
    protected int                     _numberOfFaces;

    public DxfPolyFaceMesh( final DxfDocument pdoc,
                            final DxfPairContainer pc,
                            final EntityType entityType,
                            final boolean ignorePaperSpace )
            throws DxfReaderException {
        super( pdoc, pc, entityType, ignorePaperSpace );
    }

    @Override
    public void addEntity( final DxfEntity pEntity ) {
        if ( pEntity instanceof DxfFaceDef ) {
            final DxfFaceDef face = ( DxfFaceDef ) pEntity;
            if ( _faces == null ) {
                _faces = new ArrayList<>( _numberOfFaces );
            }
            _faces.add( face );
        }
        else {
            super.addEntity( pEntity );
        }
    }

    @Override
    public boolean convertToFxShapes( final FxShapeContainer fxShapeContainer,
                                      final Affine transform,
                                      final double strokeScale ) {
        if ( !_dxfDoc.getLayer( _layer ).isLayerOn() ) {
            return false;
        }

        if ( _faces.isEmpty() ) {
            return true;
        }

        // Make a closed polygon wireframe for each face.
        final Color color = getColor();
        for ( final DxfFaceDef face : _faces ) {
            int numberOfVertices = 0;
            switch ( face.getFaceType() ) {
            case UNDEFINED:
                break;
            case POINT:
                numberOfVertices = 1;
                break;
            case LINE:
                numberOfVertices = 2;
                break;
            case TRIANGLE:
                numberOfVertices = 3;
                break;
            case QUAD:
                numberOfVertices = 4;
                break;
            default:
                break;
            }

            final List< Double > coordinates = new ArrayList<>( 2 * numberOfVertices );
            for ( int i = 0; i < numberOfVertices; i++ ) {
                // NOTE: All of these are base 1 to 0.
                final DxfVertex vertex = _vertices.get( face._iv[ i ] - 1 );

                final int j = 2 * i;
                coordinates.add( j, Double.valueOf( vertex._x ) );
                coordinates.add( j + 1, Double.valueOf( vertex._y ) );
            }

            final Polygon polygon = new Polygon();
            final ObservableList< Double > polygonCoordinates = polygon.getPoints();
            polygonCoordinates.addAll( coordinates );

            // Polyface3D is supposed to be filled with its stroke color.
            // NOTE: We are better off sticking with wireframe-only for now.
            polygon.setFill( null ); // color );

            polygon.getTransforms().add( transform );
            polygon.setStroke( color );

            fxShapeContainer.addShape( strokeScale, polygon );
        }

        return true;
    }

    @Override
    protected void parseEntityProperties( final DxfPairContainer pc ) {
        super.parseEntityProperties( pc );

        _numberOfVertices = Integer.parseInt( pc.getValue( DxfGroupCodes.CODE71 ) );
        _numberOfFaces = Integer.parseInt( pc.getValue( DxfGroupCodes.CODE72 ) );
    }

}// class DxfPolyFaceMesh
