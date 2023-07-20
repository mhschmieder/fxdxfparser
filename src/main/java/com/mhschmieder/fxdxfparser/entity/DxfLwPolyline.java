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
import java.util.Iterator;

import com.mhschmieder.commonstoolkit.lang.NumberUtilities;
import com.mhschmieder.fxdxfparser.geometry.DxfShapeContainer;
import com.mhschmieder.fxdxfparser.geometry.PolyVertex;
import com.mhschmieder.fxdxfparser.geometry.PolylineUtilities;
import com.mhschmieder.fxdxfparser.reader.DxfGroupCodes;
import com.mhschmieder.fxdxfparser.reader.DxfPair;
import com.mhschmieder.fxdxfparser.reader.DxfPairContainer;
import com.mhschmieder.fxdxfparser.reader.DxfReaderException;
import com.mhschmieder.fxdxfparser.reader.EntityType;
import com.mhschmieder.fxdxfparser.structure.DxfDocument;

import javafx.scene.transform.Affine;

public class DxfLwPolyline extends DxfEntity {

    protected static final int        FLAG_CLOSED = 1;

    protected int                     _numberOfVertices;

    protected int                     _polyFlags;

    protected double                  _constantWidth;
    protected double                  _elevation;
    protected double                  _thickness;

    protected ArrayList< PolyVertex > _polyVertices;

    protected double                  _extrusionX;
    protected double                  _extrusionY;
    protected double                  _extrusionZ;

    protected boolean                 _hasWidth;

    public DxfLwPolyline( final DxfDocument pdoc,
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
        if ( isDegenerate() ) {
            return false;
        }

        final boolean succeeded = PolylineUtilities.convertToFxShapes( dxfShapeContainer,
                                                                       transform,
                                                                       strokeScale,
                                                                       this,
                                                                       true,
                                                                       _polyVertices,
                                                                       null,
                                                                       needClose(),
                                                                       _hasWidth );

        return succeeded;
    }

    protected boolean isClosed() {
        return ( _polyFlags & DxfLwPolyline.FLAG_CLOSED ) != 0;
    }

    protected boolean isDegenerate() {
        return ( _polyVertices == null ) || ( _polyVertices.size() < 2 );
    }

    protected boolean needClose() {
        return ( ( _polyFlags & DxfLwPolyline.FLAG_CLOSED ) != 0 ) && ( PolylineUtilities
                .compareVertex2D( _polyVertices.get( 0 ),
                                  _polyVertices.get( _polyVertices.size() - 1 ) ) != 0 );
    }

    @Override
    @SuppressWarnings("nls")
    protected void parseEntityProperties( final DxfPairContainer pc ) {
        _numberOfVertices = NumberUtilities.parseInteger( pc.getValue( DxfGroupCodes.CODE90 ) );

        _polyFlags = NumberUtilities.parseInteger( pc.getValue( DxfGroupCodes.FLAGS, "0" ) );

        _constantWidth = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE43, "0" ) );
        _elevation = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.ELEVATION, "0" ) );
        _thickness = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.THICKNESS, "0" ) );

        // Dynamically determine whether the polyline has width.
        _hasWidth = _constantWidth > 0;

        // Loop over the polyline vertices.
        parsePolylineVertices( pc );

        _extrusionX = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.NORMAL_X, "0" ) );
        _extrusionY = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.NORMAL_Y, "0" ) );
        _extrusionZ = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.NORMAL_Z, "0" ) );
    }

    protected final void parsePolylineVertices( final DxfPairContainer pc ) {
        // Loop over the polyline vertices, which always start with the
        // x-coordinate (this is guaranteed -- though DXF isn't otherwise
        // well-structured and specific in the order of entity properties).
        //
        // Format: *(CODE10, CODE20, CODE30 [,CODE40][,CODE41][,CODE42])
        _polyVertices = new ArrayList<>( _numberOfVertices );
        PolyVertex polyVertex = new PolyVertex();
        final Iterator< DxfPair > it = pc.iterator( DxfGroupCodes.CODE10 );
        while ( it.hasNext() ) {
            final DxfPair pair = it.next();
            switch ( pair.getKey() ) {
            case DxfGroupCodes.CODE10:
                polyVertex =
                           new PolyVertex( 0.0d, 0.0d, 0.0d, _constantWidth, _constantWidth, 0.0d );
                _polyVertices.add( polyVertex );

                polyVertex._x = NumberUtilities.parseDouble( pair.getValue() );
                break;
            case DxfGroupCodes.CODE20:
                polyVertex._y = NumberUtilities.parseDouble( pair.getValue() );
                break;
            case DxfGroupCodes.CODE30:
                polyVertex._z = NumberUtilities.parseDouble( pair.getValue() );
                break;
            case DxfGroupCodes.CODE40:
                if ( _constantWidth <= 0.0d ) {
                    polyVertex._startWidth = NumberUtilities.parseDouble( pair.getValue() );
                    _hasWidth |= polyVertex._startWidth > 0.0d;
                }
                break;
            case DxfGroupCodes.CODE41:
                if ( _constantWidth <= 0.0d ) {
                    polyVertex._endWidth = NumberUtilities.parseDouble( pair.getValue() );
                    _hasWidth |= polyVertex._endWidth > 0.0d;
                }
                break;
            case DxfGroupCodes.CODE42:
                polyVertex._bulge = NumberUtilities.parseDouble( pair.getValue() );
                break;
            default:
                break;
            }
        }
    }

}// class DxfLwPolyline
