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
package com.mhschmieder.fxdxfparser.loader;

import com.mhschmieder.fxdxfparser.entity.DxfEntity;
import com.mhschmieder.fxdxfparser.geometry.DxfShapeContainer;
import com.mhschmieder.fxdxfparser.reader.DxfGroupCodes;
import com.mhschmieder.fxdxfparser.reader.DxfPairContainer;
import com.mhschmieder.fxdxfparser.structure.DxfDocument;
import com.mhschmieder.jcommons.lang.NumberUtilities;
import javafx.geometry.Point2D;
import javafx.scene.transform.Affine;

import java.util.ArrayList;

public final class DxfBlock implements DxfDrawable, DxfEntityContainer {

    private final DxfDocument            _dxfDoc;

    /** Nombre del bloque */
    public String                        _name;

    /** Punto de inserción */
    private Point2D                      _origin;

    private int                          _flags;
    private final ArrayList< DxfEntity > _entities;
    private PropertyOverriding           _propertyOverriding;

    // **** Propiedades provisionales: ****
    /** Tipo de línea byBlock */
    private DxfLineType                  _linetype;

    /** Color byBlock */
    private int                          _colorIndex;

    // Atributos del bloque
    // private ArrayList< DxfAttrib > _attributes;

    public DxfBlock( final DxfDocument pdoc,
                     final DxfPairContainer pc,
                     final String pname,
                     final int initialCapacity ) {
        _dxfDoc = pdoc;
        _entities = new ArrayList<>( initialCapacity );

        parse( pc, pname );
    }

    public DxfBlock( final DxfDocument doc,
                     final String name,
                     final double x,
                     final double y,
                     final int flags,
                     final int initialCapacity ) {
        _dxfDoc = doc;
        _entities = new ArrayList<>( initialCapacity );

        initBlock( name, x, y, flags );
    }

    @Override
    public void addEntity( final DxfEntity entity ) {
        _entities.add( entity );
        entity.setParentBlock( this );
        entity.initialize();
        _dxfDoc.setLastAddedEntity( entity );
    }

    public void clearBlock() {
        if ( _entities != null ) {
            _entities.clear();
        }
        // if ( _attributes != null ) {
        // _attributes.clear();
        // }
    }

    // public final DxfAttrib getAttribute( final String tag ) {
    // if ( _attributes == null ) {
    // return null;
    // }
    //
    // for ( final DxfAttrib attribute : _attributes ) {
    // if ( attribute._tag.equalsIgnoreCase( tag ) ) {
    // return attribute;
    // }
    // }
    //
    // return null;
    // }

    @Override
    public boolean convertToFxShapes( final DxfShapeContainer geometryContainer,
                                      final Affine transform,
                                      final double strokeScale ) {
        if ( ( _entities == null ) || _entities.isEmpty() ) {
            return true;
        }

        int numberOfFailures = 0;
        for ( final DxfEntity dxfEntity : _entities ) {
            dxfEntity.setCurrentPropertyOverriding( _propertyOverriding );
            final boolean succeeded = dxfEntity
                    .convertToFxShapes( geometryContainer, transform, strokeScale );
            if ( !succeeded ) {
                numberOfFailures++;
            }
            dxfEntity.setCurrentPropertyOverriding( null );
        }

        final boolean retval = numberOfFailures == 0;

        return retval;
    }

    public int getCurrentColorIndex() {
        return _colorIndex;
    }

    public DxfLineType getCurrentLineType() {
        return _linetype;
    }

    public int getEntitiesCount() {
        return _entities.size();
    }

    public String getName() {
        return _name;
    }

    private void initBlock( final String pname,
                            final double px,
                            final double py,
                            final int pflags ) {
        _origin = new Point2D( px, py );
        _name = pname;
        _flags = pflags;
        _colorIndex = 7;
        _linetype = null;
    }

    // public final void setCurrentAttributes( final ArrayList< DxfAttrib >
    // attributes ) {
    // _attributes = attributes;
    // }

    private void parse( final DxfPairContainer pc, final String name ) {
        initBlock( name,
                   NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE10 ) ), // x
                   NumberUtilities.parseDouble( pc.getValue( DxfGroupCodes.CODE20 ) ), // y
                   NumberUtilities.parseInteger( pc.getValue( DxfGroupCodes.FLAGS ) ) ); // flags
    }

    public void setCurrentColor( final int color ) {
        _colorIndex = color;
    }

    public void setCurrentLineType( final DxfLineType linetype ) {
        _linetype = linetype;
    }

    public void setPropertyOverriding( final PropertyOverriding propertyOverriding ) {
        _propertyOverriding = propertyOverriding;
    }

}// class DxfBlock
