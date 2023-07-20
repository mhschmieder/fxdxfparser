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

import com.mhschmieder.commonstoolkit.lang.NumberUtilities;
import com.mhschmieder.fxdxfparser.geometry.DxfShapeContainer;
import com.mhschmieder.fxdxfparser.reader.DxfGroupCodes;
import com.mhschmieder.fxdxfparser.reader.DxfPairContainer;
import com.mhschmieder.fxdxfparser.reader.DxfReaderException;
import com.mhschmieder.fxdxfparser.reader.EntityType;
import com.mhschmieder.fxdxfparser.structure.DxfDocument;

import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Affine;

public class DxfFace3D extends DxfEntity {

    protected static final int MAXIMUM_NUMBER_OF_VERTICES = 4;

    public static final int    TRIANGLE                   = 1;
    public static final int    QUAD                       = 2;

    protected int              _polygonType;

    protected double[]         _xCoordinates;
    protected double[]         _yCoordinates;
    protected double[]         _zCoordinates;

    protected int              _invisibleFlags;

    public DxfFace3D( final DxfDocument pdoc,
                      final DxfPairContainer pc,
                      final EntityType entityType,
                      final boolean ignorePaperSpace )
            throws DxfReaderException {
        super( pdoc, pc, entityType, ignorePaperSpace );
    }

    @Override
    public boolean convertToFxShapes( final DxfShapeContainer dxfShapeContainer,
                                      final Affine transform,
                                      final double strokeScale ) {
        if ( !_dxfDoc.getLayer( _layer ).isLayerOn() ) {
            return false;
        }

        final Color color = getColor();

        final int polygonType = getPolygonType();
        int numberOfVertices = 1;
        switch ( polygonType ) {
        case TRIANGLE:
            numberOfVertices = 3;
            break;
        case QUAD:
            numberOfVertices = 4;
            break;
        default:
            break;
        }

        // NOTE: AutoCAD does not necessarily use the four points in the
        // order in which they are given; it treats them as a pair of pairs,
        // and may change the order, resulting in illegal self-intersection.
        final List< Double > coordinates = new ArrayList<>( 2 * numberOfVertices );
        for ( int i = 0; i < numberOfVertices; i++ ) {
            final int j = 2 * i;
            coordinates.add( j, Double.valueOf( _xCoordinates[ i ] ) );
            coordinates.add( j + 1, Double.valueOf( _yCoordinates[ i ] ) );
        }

        // TODO: Eliminate common edges if the DXF flag for "isEdgeVisible" is
        // off for that edge (is it being checked via the invisible flags?).
        final Polygon polygon = new Polygon();
        final ObservableList< Double > polygonCoordinates = polygon.getPoints();
        polygonCoordinates.addAll( coordinates );

        // Face3D is supposed to be filled with its stroke color.
        // NOTE: We are better off sticking with wireframe-only for now.
        // TODO: Only fill if shading is turned on (via invisible flags?).
        polygon.setFill( null ); // color );

        polygon.getTransforms().add( transform );
        polygon.setStroke( color );

        dxfShapeContainer.addShape( strokeScale, polygon );

        return true;
    }

    public int getPolygonType() {
        return _polygonType;
    }

    @Override
    @SuppressWarnings("nls")
    protected void parseEntityProperties( final DxfPairContainer pc ) {
        _xCoordinates = new double[ MAXIMUM_NUMBER_OF_VERTICES ];
        _yCoordinates = new double[ MAXIMUM_NUMBER_OF_VERTICES ];
        _zCoordinates = new double[ MAXIMUM_NUMBER_OF_VERTICES ];

        _xCoordinates[ 0 ] = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE10 ) );
        _yCoordinates[ 0 ] = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE20 ) );
        _zCoordinates[ 0 ] = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE30 ) );

        _xCoordinates[ 1 ] = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE11 ) );
        _yCoordinates[ 1 ] = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE21 ) );
        _zCoordinates[ 1 ] = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE31 ) );

        _xCoordinates[ 2 ] = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE12 ) );
        _yCoordinates[ 2 ] = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE22 ) );
        _zCoordinates[ 2 ] = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE32 ) );

        _xCoordinates[ 3 ] = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE13 ) );
        _yCoordinates[ 3 ] = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE23 ) );
        _zCoordinates[ 3 ] = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE33 ) );

        _invisibleFlags = NumberUtilities.parseInteger( pc.getValue( DxfGroupCodes.FLAGS, "0" ) );

        if ( ( _xCoordinates[ 3 ] == _xCoordinates[ 2 ] )
                && ( _yCoordinates[ 3 ] == _yCoordinates[ 2 ] )
                && ( _zCoordinates[ 3 ] == _zCoordinates[ 2 ] ) ) {
            _polygonType = TRIANGLE;
        }
        else {
            _polygonType = QUAD;
        }
    }

}// class DxfFace3D
