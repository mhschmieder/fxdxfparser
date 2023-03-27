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
package com.mhschmieder.fxdxftoolkit.entity;

import java.util.Collection;

import com.mhschmieder.fxdxftoolkit.geometry.FxShapeContainer;
import com.mhschmieder.fxdxftoolkit.loader.DxfLineType;
import com.mhschmieder.fxdxftoolkit.reader.DxfPairContainer;
import com.mhschmieder.fxdxftoolkit.reader.DxfReaderException;
import com.mhschmieder.fxdxftoolkit.reader.EntityType;
import com.mhschmieder.fxdxftoolkit.structure.DxfDocument;

import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.transform.Affine;

public class DxfXLine extends DxfRay {

    public DxfXLine( final DxfDocument doc,
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

        // Assume 300 meters maximum dimension along any axis, and use this to
        // multiply the unit vector for ray direction. Add this to the start
        // point to get an end point for simple line representation.
        final double startPointX = _basePointX - ( 150d * _directionX );
        final double startPointY = _basePointY - ( 150d * _directionY );
        final double endPointX = _basePointX + ( 150d * _directionX );
        final double endPointY = _basePointY + ( 150d * _directionY );

        final Color color = getColor();
        final DxfLineType lineType = getLineType();

        final Line line = new Line( startPointX, startPointY, endPointX, endPointY );

        line.getTransforms().add( transform );
        line.setStroke( color );

        if ( ( lineType != null ) && !lineType.isContinuous() ) {
            final double lineTypeScale = _dxfDoc.getGlobalLineTypeScale() * _lineTypeScale;
            final Collection< Double > dashArrayCandidate = lineType.makeDashArray( lineTypeScale );
            final ObservableList< Double > dashArray = line.getStrokeDashArray();
            dashArray.setAll( dashArrayCandidate );
        }

        fxShapeContainer.addShape( strokeScale, line );

        return true;
    }

}// class DxfXLine
