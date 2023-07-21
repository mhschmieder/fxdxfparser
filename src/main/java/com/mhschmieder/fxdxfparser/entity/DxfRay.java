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

import java.util.Collection;

import com.mhschmieder.commonstoolkit.lang.NumberUtilities;
import com.mhschmieder.fxdxfparser.geometry.DxfShapeContainer;
import com.mhschmieder.fxdxfparser.loader.DxfLineType;
import com.mhschmieder.fxdxfparser.reader.DxfGroupCodes;
import com.mhschmieder.fxdxfparser.reader.DxfPairContainer;
import com.mhschmieder.fxdxfparser.reader.DxfReaderException;
import com.mhschmieder.fxdxfparser.reader.EntityType;
import com.mhschmieder.fxdxfparser.structure.DxfDocument;

import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.transform.Affine;

public class DxfRay extends DxfEntity {

    protected double _basePointX;
    protected double _basePointY;
    protected double _basePointZ;

    protected double _directionX;
    protected double _directionY;
    protected double _directionZ;

    public DxfRay( final DxfDocument pdoc,
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

        // Assume 300 meters maximum dimension along any axis, and use this to
        // multiply the unit vector for ray direction. Add this to the start
        // point to get an end point for simple line representation.
        final double endPointX = _basePointX + ( 300d * _directionX );
        final double endPointY = _basePointY + ( 300d * _directionY );

        final Color color = getColor();
        final DxfLineType lineType = getLineType();

        final Line line = new Line( _basePointX, _basePointY, endPointX, endPointY );

        line.getTransforms().add( transform );
        line.setStroke( color );

        if ( ( lineType != null ) && !lineType.isContinuous() ) {
            final double lineTypeScale = _dxfDoc.getGlobalLineTypeScale() * _lineTypeScale;
            final Collection< Double > dashArrayCandidate = lineType.makeDashArray( lineTypeScale );
            final ObservableList< Double > dashArray = line.getStrokeDashArray();
            dashArray.setAll( dashArrayCandidate );
        }

        dxfShapeContainer.addShape( strokeScale, line );

        return true;
    }

    @Override
    protected void parseEntityProperties( final DxfPairContainer pc ) {
        _basePointX = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE10 ) );
        _basePointY = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE20 ) );
        _basePointZ = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE30 ) );

        _directionX = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE11 ) );
        _directionY = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE21 ) );
        _directionZ = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE31 ) );
    }

}// class DxfRay
