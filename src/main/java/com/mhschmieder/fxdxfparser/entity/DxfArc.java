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

import com.mhschmieder.fxdxfparser.geometry.DxfShapeContainer;
import com.mhschmieder.fxdxfparser.loader.DxfLineType;
import com.mhschmieder.fxdxfparser.reader.DxfGroupCodes;
import com.mhschmieder.fxdxfparser.reader.DxfPairContainer;
import com.mhschmieder.fxdxfparser.reader.DxfReaderException;
import com.mhschmieder.fxdxfparser.reader.EntityType;
import com.mhschmieder.fxdxfparser.structure.DxfDocument;
import com.mhschmieder.jcommons.lang.NumberUtilities;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.transform.Affine;

import java.util.Collection;

public class DxfArc extends DxfEntity {

    protected double _thickness;

    protected double _centerX;
    protected double _centerY;
    protected double _centerZ;

    protected double _radius;

    protected double _startAngle;
    protected double _endAngle;

    protected double _extrusionX;
    protected double _extrusionY;
    protected double _extrusionZ;

    public DxfArc( final DxfDocument pdoc,
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
        final DxfLineType lineType = getLineType();

        // Both DXF and JavaFX set zero degrees to the positive x-axis and
        // measure angles counter-clockwise, but JavaFX confuses matters by
        // asking for an angle extent instead of an end angle, so we essentially
        // have to invert the angle differential and then do a simple test to
        // determine angle stroke direction (which we compensate using a 360
        // degree offset). Accurate documentation was hard to come by!
        double arcExtentDeg = _startAngle - _endAngle;
        if ( _endAngle < _startAngle ) {
            arcExtentDeg -= 360d;
        }
        final Arc arc = new Arc( _centerX, _centerY, _radius, _radius, -_startAngle, arcExtentDeg );

        arc.getTransforms().add( transform );
        arc.setStroke( color );

        // Arc is an outline-only entity.
        arc.setFill( null );

        if ( ( lineType != null ) && !lineType.isContinuous() ) {
            final double lineTypeScale = _dxfDoc.getGlobalLineTypeScale() * _lineTypeScale;
            final Collection< Double > dashArrayCandidate = lineType.makeDashArray( lineTypeScale );
            final ObservableList< Double > dashArray = arc.getStrokeDashArray();
            dashArray.setAll( dashArrayCandidate );
        }

        dxfShapeContainer.addShape( strokeScale, arc );

        return true;
    }

    @Override
    @SuppressWarnings("nls")
    protected void parseEntityProperties( final DxfPairContainer pc ) {
        _thickness = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.THICKNESS, "0" ) );

        _centerX = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE10 ) );
        _centerY = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE20 ) );
        _centerZ = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE30 ) );

        _radius = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE40 ) );

        _startAngle = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE50 ) );
        _endAngle = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE51 ) );

        _extrusionX = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.NORMAL_X, "0" ) );
        _extrusionY = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.NORMAL_Y, "0" ) );
        _extrusionZ = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.NORMAL_Z, "0" ) );
    }

}// class DxfArc