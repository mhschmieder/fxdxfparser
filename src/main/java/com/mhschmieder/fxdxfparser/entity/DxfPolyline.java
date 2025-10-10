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
import com.mhschmieder.fxdxfparser.geometry.PolyVertex;
import com.mhschmieder.fxdxfparser.geometry.PolylineUtilities;
import com.mhschmieder.fxdxfparser.loader.DxfEntityContainer;
import com.mhschmieder.fxdxfparser.reader.DxfGroupCodes;
import com.mhschmieder.fxdxfparser.reader.DxfPairContainer;
import com.mhschmieder.fxdxfparser.reader.DxfReaderException;
import com.mhschmieder.fxdxfparser.reader.EntityType;
import com.mhschmieder.fxdxfparser.structure.DxfDocument;
import javafx.scene.transform.Affine;

import java.util.ArrayList;

public class DxfPolyline extends DxfEntity implements DxfEntityContainer {

    protected static final int        MAXIMUM_NUMBER_OF_VERTICES = 10;

    public static final int           QUAD                       = 5;
    public static final int           CUBIC                      = 6;
    public static final int           BEZIER                     = 8;

    public static final int           FLAG_CLOSED                = 1;
    public static final int           FLAG_3DPOLYLINE            = 8;
    public static final int           FLAG_POLYGON_MESH          = 16;
    public static final int           FLAG_POLY_FACE_MESH        = 64;

    protected double                  _elevationX;
    protected double                  _elevationY;
    protected double                  _elevationZ;

    protected double                  _thickness;

    protected int                     _polyFlags;
    protected double                  _startWidth;
    protected double                  _endWidth;
    protected int                     _surfaceType;

    protected double                  _extrusionX;
    protected double                  _extrusionY;
    protected double                  _extrusionZ;

    protected int                     _vertexCount;
    protected boolean                 _hasWidth;

    protected ArrayList< DxfVertex >  _controlPoints;
    protected ArrayList< DxfVertex >  _vertices;
    protected ArrayList< PolyVertex > _polyVertices;

    public DxfPolyline( final DxfDocument pdoc,
                        final DxfPairContainer pc,
                        final EntityType entityType,
                        final boolean ignorePaperSpace )
            throws DxfReaderException {
        super( pdoc, pc, entityType, ignorePaperSpace );
    }

    @Override
    public void addEntity( final DxfEntity pEntity ) {
        final DxfVertex vertex = ( DxfVertex ) pEntity;
        if ( vertex.isControlPoint() ) {
            if ( _controlPoints == null ) {
                _controlPoints = new ArrayList<>( MAXIMUM_NUMBER_OF_VERTICES );
            }
            _controlPoints.add( vertex );
        }
        else if ( !vertex.isVertex2D() ) {
            if ( _vertices == null ) {
                _vertices = new ArrayList<>( MAXIMUM_NUMBER_OF_VERTICES );
            }
            _vertices.add( vertex );
            _vertexCount++;
        }
        else {
            final double sw = vertex.getStartWidth();
            final double ew = vertex.getEndWidth();
            final double bulge = vertex.getBulge();
            final PolyVertex newvertex = new PolyVertex( vertex._x,
                                                         vertex._y,
                                                         vertex._z,
                                                         sw,
                                                         ew,
                                                         bulge );
            if ( _polyVertices == null ) {
                _polyVertices = new ArrayList<>( MAXIMUM_NUMBER_OF_VERTICES );
            }
            _polyVertices.add( newvertex );
            _hasWidth = _hasWidth || ( sw > 0 ) || ( ew > 0 );
        }
    }

    @Override
    public boolean convertToFxShapes( final DxfShapeContainer dxfShapeContainer,
                                      final Affine transform,
                                      final double strokeScale ) {
        if ( !_dxfDoc.getLayer( _layer ).isLayerOn() ) {
            return false;
        }
        if ( isDegenerate() ) {
            return false;
        }

        final boolean succeeded = PolylineUtilities.convertToFxShapes( dxfShapeContainer,
                                                                       transform,
                                                                       strokeScale,
                                                                       this,
                                                                       isVertex2D(),
                                                                       _polyVertices,
                                                                       _vertices,
                                                                       needClose(),
                                                                       false );

        return succeeded;
    }

    protected boolean isClosed() {
        return ( _polyFlags & FLAG_CLOSED ) != 0;
    }

    protected boolean isDegenerate() {
        return ( _vertices == null ) || ( _vertices.size() < 2 );
    }

    protected boolean isVertex2D() {
        return ( ( _polyFlags & FLAG_3DPOLYLINE ) == 0 );
    }

    protected boolean needClose() {
        return ( ( _polyFlags & FLAG_CLOSED ) != 0 ) && ( PolylineUtilities
                .compareVertex2D( _polyVertices.get( 0 ),
                                  _polyVertices.get( _polyVertices.size() - 1 ) ) != 0 );
    }

    @Override
    @SuppressWarnings("nls")
    protected void parseEntityProperties( final DxfPairContainer pc ) {
        _elevationX = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE10, "0" ) );
        _elevationY = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE20, "0" ) );
        _elevationZ = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE30, "0" ) );

        _thickness = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.THICKNESS, "0" ) );

        _polyFlags = NumberUtilities.parseInteger( pc.getValue( DxfGroupCodes.FLAGS, "0" ) );

        _startWidth = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE40, "0" ) );
        _endWidth = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE41, "0" ) );

        _surfaceType = NumberUtilities.parseInteger( pc.getValue( DxfGroupCodes.CODE75, "0" ) );

        _extrusionX = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.NORMAL_X, "0" ) );
        _extrusionY = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.NORMAL_Y, "0" ) );
        _extrusionZ = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.NORMAL_Z, "0" ) );
    }

}// class DxfPolyline
