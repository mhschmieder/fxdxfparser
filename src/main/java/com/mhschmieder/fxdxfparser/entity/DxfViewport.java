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

import com.mhschmieder.commonstoolkit.lang.NumberUtilities;
import com.mhschmieder.fxdxfparser.reader.DxfGroupCodes;
import com.mhschmieder.fxdxfparser.reader.DxfPairContainer;
import com.mhschmieder.fxdxfparser.reader.DxfReaderException;
import com.mhschmieder.fxdxfparser.reader.EntityType;
import com.mhschmieder.fxdxfparser.structure.DxfDocument;

import javafx.geometry.Point2D;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Scale;

public class DxfViewport extends DxfEntity {

    protected double  _centerX;
    protected double  _centerY;
    protected double  _width;
    protected double  _height;

    protected double  _viewCenterX;
    protected double  _viewCenterY;
    protected double  _viewWidth;
    protected double  _viewHeight;

    protected int     _id;

    protected Affine  _blockTransform;

    protected Point2D _boundsMax;
    protected Point2D _boundsMin;

    public DxfViewport( final DxfDocument pdoc,
                        final DxfPairContainer pc,
                        final EntityType entityType,
                        final boolean ignorePaperSpace )
            throws DxfReaderException {
        super( pdoc, pc, entityType, ignorePaperSpace );
    }

    private void calcBoundingBox() {
        final double x1 = _centerX - ( _width / 2 );
        final double x2 = _centerX + ( _width / 2 );
        final double y1 = _centerY - ( _height / 2 );
        final double y2 = _centerY + ( _height / 2 );

        _boundsMax = new Point2D( x2, y2 );
        _boundsMin = new Point2D( x1, y1 );
    }

    public Point2D getBoundsMax() {
        return _boundsMax;
    }

    public Point2D getBoundsMin() {
        return _boundsMin;
    }

    public double getHeight() {
        return _height;
    }

    public double getWidth() {
        return _width;
    }

    @Override
    protected void parseEntityProperties( final DxfPairContainer pc ) {
        _centerX = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE10 ) );
        _centerY = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE20 ) );

        _width = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE40 ) );
        _height = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE41 ) );

        _viewCenterX = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE12 ) );
        _viewCenterY = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE22 ) );

        _viewHeight = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE45 ) );
        _viewWidth = ( _width * _viewHeight ) / _height;

        _id = NumberUtilities.parseInteger( pc.getValue( DxfGroupCodes.CODE69 ) );

        final double scaleFactor = _height / _viewHeight;
        _blockTransform = new Affine( new Scale( scaleFactor, scaleFactor ) );

        // traslación
        final double vcsx = _viewCenterX * scaleFactor;
        final double vcsy = _viewCenterY * scaleFactor;
        _blockTransform.appendTranslation( _centerX - vcsx, _centerY - vcsy );

        calcBoundingBox();
    }

}// class DxfViewport
