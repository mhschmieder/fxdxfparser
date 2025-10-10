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
import com.mhschmieder.fxdxfparser.loader.DxfBlock;
import com.mhschmieder.fxdxfparser.loader.DxfColors;
import com.mhschmieder.fxdxfparser.loader.DxfDrawable;
import com.mhschmieder.fxdxfparser.loader.DxfLayer;
import com.mhschmieder.fxdxfparser.loader.DxfLineType;
import com.mhschmieder.fxdxfparser.loader.PropertyOverriding;
import com.mhschmieder.fxdxfparser.reader.DxfGroupCodes;
import com.mhschmieder.fxdxfparser.reader.DxfPairContainer;
import com.mhschmieder.fxdxfparser.reader.DxfReaderException;
import com.mhschmieder.fxdxfparser.reader.EntityType;
import com.mhschmieder.fxdxfparser.structure.DxfDocument;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

public abstract class DxfEntity implements DxfDrawable {

    private String             _handle;
    private String             _ownerId;

    private EntityType         _entityType;
    public boolean             _inPaperSpace;
    protected String           _layer;
    private int                _colorIndex;
    private String             _lineType;
    public double              _lineTypeScale;
    protected boolean          _visible;

    public DxfDocument         _dxfDoc;

    private DxfBlock           _parent;

    private PropertyOverriding _propertyOverriding;

    protected DxfEntity( final DxfDocument pdoc,
                         final DxfPairContainer pc,
                         final EntityType entityType,
                         final boolean ignorePaperSpace )
            throws DxfReaderException {
        _dxfDoc = pdoc;

        final boolean paperSpaceIgnored =
                                        !parseCommonProperties( pc, entityType, ignorePaperSpace );
        if ( paperSpaceIgnored ) {
            throw new DxfReaderException( "DXF Paper Space Entity Ignored" ); //$NON-NLS-1$
        }

        parseEntityProperties( pc );

        pdoc.addEntityByRef( this );
    }

    @Override
    public boolean convertToFxShapes( final DxfShapeContainer dxfShapeContainer,
                                      final Affine transform,
                                      final double strokeScale ) {
        return false; // nada que dibujar
    }

    /**
     * @return color of the entity
     * @see #getColorIndex
     */
    public Color getColor() {
        int colorIndex;
        if ( ( _propertyOverriding != null ) && _propertyOverriding.flagColor() ) {
            colorIndex = _propertyOverriding.getColorIndex();
        }
        else {
            colorIndex = _colorIndex;
        }

        // DXF Color Index has a specific implementation that is unusual in that
        // the two extrema are special flags for other behavior. The minimum
        // index says to get the color from the Parent; whereas the maximum
        // index says to get it from the Layer. Otherwise use the index as-is.
        final int colorIndexAdjusted = ( colorIndex == DxfColors.COLOR_BY_BLOCK_INDEX )
            ? _parent.getCurrentColorIndex()
            : ( colorIndex == DxfColors.COLOR_BY_LAYER_INDEX )
                ? _dxfDoc.getLayer( _layer ).getColorIndex()
                : colorIndex;

        return DxfColors.indexToColor( colorIndexAdjusted );
    }

    /**
     * @return índice del color de la entidad. Puede devolver los índices 0
     *         (byBlock) y 256 (byLayer).
     * @see #getColor
     * @see #getRealColorIndex
     */
    public int getColorIndex() {
        return _colorIndex;
    }

    /**
     * @return índice del color del bloque al que pertenece la entidad.
     *         Devuelve valores comprendidos entre 1 y 255.
     * @see #getColorIndex
     */
    public int getColorIndexByBlock() {
        return _parent.getCurrentColorIndex();
    }

    /**
     * @return índice del color de la capa a la que pertenece la entidad.
     *         Devuelve valores comprendidos entre 1 y 255.
     * @see #getColorIndex
     */
    public int getColorIndexByLayer() {
        return _dxfDoc.getLayer( _layer ).getColorIndex();
    }

    /**
     * @return tipo de la entidad.
     */
    public EntityType getEntityType() {
        return _entityType;
    }

    /**
     * @return identificador único (referencia) de esta entidad
     * @see #getParentHandle
     * @see DxfDocument#getEntityByRef
     */
    public String getHandle() {
        return _handle;
    }

    /**
     * @return Capa de la entidad
     */
    public DxfLayer getLayer() {
        return _dxfDoc.getLayer( _layer );
    }

    /**
     * @return Tipo de línea de la entidad
     */
    @SuppressWarnings("nls")
    public DxfLineType getLineType() {
        DxfLineType ret;
        if ( _lineType.equalsIgnoreCase( "BYLAYER" ) ) {
            ret = _dxfDoc.getLineType( _dxfDoc.getLayer( _layer ).getLineTypeName() );
        }
        else if ( _lineType.equalsIgnoreCase( "BYBLOCK" ) ) {
            ret = _parent.getCurrentLineType();
        }
        else {
            ret = _dxfDoc.getLineType( _lineType );
        }

        return ret;
    }

    /**
     * @return Bloque padre de la entidad. Puede ser null si el padre es una
     *         entidad. En tal caso debería accederse a la entidad a través de
     *         getParentHandle
     * @see #getParentHandle
     */
    protected DxfBlock getParentBlock() {
        return _parent;
    }

    /**
     * @return identificador único (referencia) de la entidad paterna
     * @see #getHandle
     */
    public String getParentHandle() {
        return _ownerId;
    }

    /**
     * @return índice del color de la entidad. Se resuelve el color en caso de
     *         que sea byBlock/byLayer. Por tanto devuelve valores comprendidos
     *         entre 1 y 255.
     * @see #getColorIndex
     */
    protected int getRealColorIndex() {
        final int colorIndex =
                             ( ( _propertyOverriding != null ) && _propertyOverriding.flagColor() )
                                 ? _propertyOverriding.getColorIndex()
                                 : _colorIndex;

        int realColorIndex = colorIndex;
        
        switch ( colorIndex ) {
        case DxfColors.COLOR_BY_BLOCK_INDEX:
            realColorIndex = getColorIndexByBlock();
            break;
        case DxfColors.COLOR_BY_LAYER_INDEX:
            realColorIndex = getColorIndexByLayer();
            break;
        default:
            break;
        }
        
        return realColorIndex;
    }

    protected void initEntity( final EntityType entityType,
                               final boolean inPaperSpace,
                               final String layer,
                               final int colorIndex,
                               final String lineType,
                               final double lineTypeScale,
                               final boolean visible ) {
        _entityType = entityType;
        _inPaperSpace = inPaperSpace;
        _layer = layer;
        _colorIndex = colorIndex;
        _lineType = lineType;
        _lineTypeScale = lineTypeScale;
        _visible = visible;
    }

    public void initialize() {}

    @SuppressWarnings("nls")
    private boolean parseCommonProperties( final DxfPairContainer pc,
                                           final EntityType entityType,
                                           final boolean ignorePaperSpace ) {
        // First parse the properties common to all Objects.
        parseObjectProperties( pc );

        // Now gather the sub-class pairs for the properties common to all
        // Entities. These are mandatory and guaranteed to be present.
        DxfPairContainer pce = pc.getSubclassPairs( "AcDbEntity" );
        if ( pce == null ) {
            pce = pc;
        }

        final boolean inPaperSpace = NumberUtilities
                .parseInteger( pce.getValue( DxfGroupCodes.PAPER_SPACE, "0" ) ) != 0;
        if ( inPaperSpace && ignorePaperSpace ) {
            return false;
        }

        // NOTE: Line Weight and Plot Style are ignored, as they relate to
        // plotting and not to on-screen representations so are irrelevant in
        // most application contexts for graphics import.
        initEntity( entityType,
                    inPaperSpace,
                    pce.getValue( DxfGroupCodes.LAYER ),
                    NumberUtilities.parseInteger( pce
                            .getValue( DxfGroupCodes.COLOR,
                                       Integer.toString( DxfColors.COLOR_BY_LAYER_INDEX ) ) ),
                    pce.getValue( DxfGroupCodes.LINE_TYPE, "BYLAYER" ),
                    NumberUtilities.parseDouble( pce.getValue( DxfGroupCodes.LINE_TYPE_SCALE, "1" ) ),
                    NumberUtilities.parseInteger( pce.getValue( DxfGroupCodes.VISIBLE, "0" ) ) != 0 );

        return true;
    }

    protected abstract void parseEntityProperties( final DxfPairContainer pc );

    private final void parseObjectProperties( final DxfPairContainer pc ) {
        // The handle is used for stuff that gets parsed after the main loop, so
        // that (for instance) vertices can be attached to their faces, edges,
        // or polylines. It is an entity reference, not the same as Owner ID.
        setHandle( pc.getValue( DxfGroupCodes.HANDLE ) );

        // NOTE: This bottom-up Owner ID is redundant due to the top-down
        // ownership structure, and is ignored by other parsers, but is used in
        // this one by the Attribute parser, which needs to be reviewed for
        // correctness. It might then be best to delete this code altogether.
        setOwnerId( pc.getValue( DxfGroupCodes.OWNER_ID ) );
    }

    public void setCurrentPropertyOverriding( final PropertyOverriding propertyOverriding ) {
        _propertyOverriding = propertyOverriding;
    }

    protected void setHandle( final String handle ) {
        _handle = handle;
    }

    protected void setOwnerId( final String ownerId ) {
        _ownerId = ownerId;
    }

    public void setParentBlock( final DxfBlock parent ) {
        _parent = parent;
    }

    /**
     * This method returns the name of the DXF Entity , which is the
     * all-upper-case representation of its enumeration, accounting for naming
     * inversions due to Java restrictions on starting all names with letters.
     *
     * @return The name of the DXF Entity
     * @see #getParentHandle
     * @see DxfDocument#getEntityByRef
     */
    @Override
    public final String toString() {
        return _entityType.toString();
    }

}// class DxfEntity
