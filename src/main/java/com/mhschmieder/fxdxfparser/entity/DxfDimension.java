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

import com.mhschmieder.fxdxfparser.geometry.FxShapeContainer;
import com.mhschmieder.fxdxfparser.loader.DxfBlock;
import com.mhschmieder.fxdxfparser.reader.DxfGroupCodes;
import com.mhschmieder.fxdxfparser.reader.DxfPairContainer;
import com.mhschmieder.fxdxfparser.reader.DxfReaderException;
import com.mhschmieder.fxdxfparser.reader.EntityType;
import com.mhschmieder.fxdxfparser.structure.DxfDocument;

import javafx.scene.transform.Affine;

public class DxfDimension extends DxfEntity {

    protected String _block;
    protected String _dimStyle;

    protected double _defPointX;
    protected double _defPointY;

    protected double _textPointX;
    protected double _textPointY;

    // protected int _dimType;
    protected double _actualMeasurement;
    protected String _text;
    protected double _textRotation;
    protected double _horizontalDirection;

    public DxfDimension( final DxfDocument pdoc,
                         final DxfPairContainer pc,
                         final EntityType entityType,
                         final boolean ignorePaperSpace )
            throws DxfReaderException {
        super( pdoc, pc, entityType, ignorePaperSpace );
    }

    @Override
    public boolean convertToFxShapes( final FxShapeContainer fxShapeContainer,
                                      final Affine transform,
                                      final double strokeScale ) {
        if ( !_dxfDoc.getLayer( _layer ).isLayerOn() ) {
            return false;
        }

        final DxfBlock blk = _dxfDoc.getBlock( _block );
        blk.setCurrentColor( getRealColorIndex() );
        blk.convertToFxShapes( fxShapeContainer, transform, strokeScale );

        return true;
    }

    @Override
    @SuppressWarnings("nls")
    protected void parseEntityProperties( final DxfPairContainer pc ) {
        final DxfPairContainer pcdim = pc.getSubclassPairs( "AcDbDimension" );

        _defPointX = Double.parseDouble( pcdim.getValue( DxfGroupCodes.CODE10 ) );
        _defPointY = Double.parseDouble( pcdim.getValue( DxfGroupCodes.CODE20 ) );

        _textPointX = Double.parseDouble( pcdim.getValue( DxfGroupCodes.CODE11 ) );
        _textPointY = Double.parseDouble( pcdim.getValue( DxfGroupCodes.CODE21 ) );

        // NOTE: This is commented out, because it causes run-time exceptions
        // due to not parsing correctly. And as it isn't used anyway, it causes
        // more problems than it solves. In actuality, we should mask for the
        // first three bits and use those to determine the dimension type.
        // _dimType = Integer.valueOf(pcdim.getValue(Dxf.CODE70)).intValue();

        _text = pcdim.getValue( DxfGroupCodes.CODE1 );
        _block = pcdim.getValue( DxfGroupCodes.CODE2 );
        _dimStyle = pcdim.getValue( DxfGroupCodes.CODE3 );
        _actualMeasurement = Double.parseDouble( pcdim.getValue( DxfGroupCodes.CODE42, "0" ) );
        _textRotation = Double.parseDouble( pcdim.getValue( DxfGroupCodes.CODE53, "0" ) );
        _horizontalDirection = Double.parseDouble( pcdim.getValue( DxfGroupCodes.CODE51, "0" ) );
    }

}// class DxfDimension
