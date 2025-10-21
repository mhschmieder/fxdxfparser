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
import com.mhschmieder.fxdxfparser.loader.DxfBlock;
import com.mhschmieder.fxdxfparser.loader.DxfEntityContainer;
import com.mhschmieder.fxdxfparser.loader.DxfLineType;
import com.mhschmieder.fxdxfparser.reader.DxfGroupCodes;
import com.mhschmieder.fxdxfparser.reader.DxfPairContainer;
import com.mhschmieder.fxdxfparser.reader.DxfReaderException;
import com.mhschmieder.fxdxfparser.reader.EntityType;
import com.mhschmieder.fxdxfparser.structure.DxfDocument;
import com.mhschmieder.jcommons.lang.NumberUtilities;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

public class DxfInsert extends DxfEntity implements DxfEntityContainer {

    // private ArrayList< DxfAttrib > _attributes;
    private String _blockName;
    private Affine _blockTransform;
    private double _strokeScale;

    public DxfInsert( final DxfDocument doc,
                      final DxfPairContainer pc,
                      final EntityType entityType,
                      final boolean ignorePaperSpace )
            throws DxfReaderException {
        super( doc, pc, entityType, ignorePaperSpace );
    }

    @Override
    public void addEntity( final DxfEntity entity ) {
        // if ( entity instanceof DxfAttrib ) {
        // if ( _attributes == null ) {
        // _attributes = new ArrayList<>();
        // }
        // _attributes.add( ( DxfAttrib ) ( entity ) );
        // entity.initialize();
        // }
    }

    @Override
    public boolean convertToFxShapes( final DxfShapeContainer dxfShapeContainer,
                                      final Affine transform,
                                      final double strokeScale ) {
        // Ignore blocks if they are on switched-off layers.
        if ( !_dxfDoc.getLayer( _layer ).isLayerOn() ) {
            return false;
        }

        // NOTE: Block References must have a valid Block Name.
        if ( ( _blockName == null ) || _blockName.trim().isEmpty() ) {
            return false;
        }

        final DxfBlock block = _dxfDoc.getBlock( _blockName );
        if ( block == null ) {
            return false;
        }

        final Affine resultante = new Affine( transform );
        resultante.append( _blockTransform );

        final DxfLineType lineType = getLineType();
        block.setCurrentLineType( lineType );
        final int colorIndex = getRealColorIndex();
        block.setCurrentColor( colorIndex );
        // block.setCurrentAttributes( _attributes );

        final double totalStrokeScale = _strokeScale * strokeScale;
        block.convertToFxShapes( dxfShapeContainer, resultante, totalStrokeScale );

        return true;
    }

    @Override
    @SuppressWarnings("nls")
    protected void parseEntityProperties( final DxfPairContainer pc ) {
        // _attributes = null;

        _blockName = pc.getValue( DxfGroupCodes.CODE2 );

        final double rotationAngleDegrees = NumberUtilities
                .parseDouble( pc.getValue( DxfGroupCodes.CODE50, "0" ) );

        final double insertX = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE10 ) );
        final double insertY = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE20 ) );

        final double scaleX = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE41, "1" ) );
        final double scaleY = NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE42, "1" ) );

        // Cambio sentido
        final Rotate rotate = new Rotate( rotationAngleDegrees % 360d );

        // Escalado
        final Scale scale = new Scale( scaleX, scaleY );

        // Traslacion
        final Translate translate = new Translate( insertX, insertY );

        // NOTE: We cannot translate as a concatenation at this point, so we
        // set those matrix values directly after appending scale to rotation.
        _blockTransform = new Affine( rotate );
        _blockTransform.append( scale );
        _blockTransform.setTx( translate.getX() );
        _blockTransform.setTy( translate.getY() );
        _blockTransform.setTz( 0.0d );

        // Cache the inverse magnitude of the scaling, to apply to strokes.
        // NOTE: We take the average, in case x-scaling and y-scaling differ.
        final double averageScaleFactor = 0.5d * ( scaleX + scaleY );
        _strokeScale = 1.0d / averageScaleFactor;
    }

}// class DxfInsert