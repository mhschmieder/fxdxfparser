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
package com.mhschmieder.fxdxfparser.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.mhschmieder.fxdxfparser.entity.DxfEntity;
import com.mhschmieder.fxdxfparser.loader.DxfBlock;
import com.mhschmieder.fxdxfparser.loader.DxfLayer;
import com.mhschmieder.fxdxfparser.loader.DxfLineType;
import com.mhschmieder.fxdxfparser.loader.PropertyOverriding;
import com.mhschmieder.fxdxfparser.physics.DxfDistanceUnit;

public final class DxfDocument {

    /** Nombre del bloque "espacio modelo" */
    public static final String             MODEL_BLOCK      = "*MODEL_SPACE";                             //$NON-NLS-1$

    /** Nombre del bloque "espacio papel" */
    public static final String             PAPER_BLOCK      = "*PAPER_SPACE";                             //$NON-NLS-1$

    // Encapsulation of status of read and unread entities.
    public final DxfStatus                 _dxfStatus;

    // Bloque "espacio modelo"
    public DxfBlock                        _modelSpace;

    // Bloque "espacio papel"
    public DxfBlock                        _paperSpace;

    // Lista de bloques
    private HashMap< String, DxfBlock >    _blocks          = new HashMap<>( 50 );

    // Distance Unit, referred to as Model Space Unit of Measurement.
    private DxfDistanceUnit                _distanceUnit;

    // Escala linetype global
    private double                         _linetypeScale;

    // Limites
    private double                         _limitsMinX;
    private double                         _limitsMinY;
    private double                         _limitsMaxX;
    private double                         _limitsMaxY;

    // Capa por defecto
    private final DxfLayer                 _defaultLayer    = new DxfLayer( "",                           //$NON-NLS-1$
                                                                            0,
                                                                            7,
                                                                            "CONTINUOUS" );               //$NON-NLS-1$
    // Tipo por defecto
    private final DxfLineType              _defaultLineType = new DxfLineType( "CONTINUOUS",              //$NON-NLS-1$
                                                                               0,
                                                                               0,
                                                                               "Solid line",              //$NON-NLS-1$
                                                                               0,
                                                                               null,
                                                                               0 );

    // Lista de entidades registradas
    private HashMap< String, DxfEntity >   _refEntities     = new HashMap<>( 500 );

    // Última entidad añadida al documento
    private DxfEntity                      _lastEntity;

    // Bloques usados como flechas de cota (dimension)
    private ArrayList< String >            _arrows;

    // =================== T A B L A S
    // ---- LAYERS
    private HashMap< String, DxfLayer >    _tblLayer        = new HashMap<>( 10 );

    // ---- LINE TYPES
    private HashMap< String, DxfLineType > _tblLineType     = new HashMap<>( 10 );

    public DxfDocument( final boolean logDxfStatus ) {
        // NOTE: Older DXF files do not contain this header field, and even
        // today one can explicitly specify "unitless".
        _distanceUnit = DxfDistanceUnit.UNITLESS;

        _dxfStatus = logDxfStatus ? new DxfStatus() : null;

        // Estos dos bloques son fijos
        _modelSpace = new DxfBlock( this, MODEL_BLOCK, 0.0d, 0.0d, 0, 20 );
        addBlock( _modelSpace );
        _paperSpace = new DxfBlock( this, PAPER_BLOCK, 0.0d, 0.0d, 0, 10 );
        addBlock( _paperSpace );
    }

    public void addArrowBlock( final String pblockName ) {
        if ( _arrows == null ) {
            _arrows = new ArrayList<>();
        }

        for ( final String blk : _arrows ) {
            if ( blk.equalsIgnoreCase( pblockName ) ) {
                return;
            }
        }

        _arrows.add( pblockName ); // lo añade si no estaba ya
    }

    public void addBlock( final DxfBlock pblock ) {
        _blocks.put( pblock._name.toUpperCase(), pblock );
    }

    public void addEntityByRef( final DxfEntity pent ) {
        _refEntities.put( pent.getHandle(), pent );
    }

    public void addLayer( final String pName,
                          final int pFlags,
                          final int pColor,
                          final String pLinetype ) {
        final String layerName = pName.toUpperCase();
        _tblLayer.put( layerName, new DxfLayer( layerName, pFlags, pColor, pLinetype ) );
    }

    public void addLineType( final String pName,
                             final int pFlags,
                             final int pComplexFlags,
                             final String pDesc,
                             final int pNumItems,
                             final double[] pPattern,
                             final double pPatternLength ) {
        final String lineTypeName = pName.toUpperCase( Locale.ENGLISH );
        _tblLineType.put( lineTypeName,
                          new DxfLineType( lineTypeName,
                                           pFlags,
                                           pComplexFlags,
                                           pDesc,
                                           pNumItems,
                                           pPattern,
                                           pPatternLength ) );
    }

    public void clearDocument() {
        if ( _modelSpace != null ) {
            _modelSpace.clearBlock();
            _modelSpace = null;
        }
        if ( _paperSpace != null ) {
            _paperSpace.clearBlock();
            _paperSpace = null;
        }
        if ( _arrows != null ) {
            _arrows.clear();
            _arrows = null;
        }
        if ( _blocks != null ) {
            _blocks.clear();
            _blocks = null;
        }
        if ( _refEntities != null ) {
            _refEntities.clear();
            _refEntities = null;
        }
        if ( _tblLayer != null ) {
            _tblLayer.clear();
            _tblLayer = null;
        }
        if ( _tblLineType != null ) {
            _tblLineType.clear();
            _tblLineType = null;
        }
    }

    public DxfBlock getBlock( final String name ) {
        return _blocks.get( name.toUpperCase() );
    }

    /**
     * Obtiene los nombres de todos los bloques contenidos en el documento.
     *
     * @return array con los nombres de los bloques
     */
    public String[] getBlockNames() {
        if ( ( _blocks == null ) || ( _blocks.size() == 0 ) ) {
            return null;
        }

        final String[] blockNames = new String[ _blocks.size() ];
        final Collection< DxfBlock > blockCollection = _blocks.values();
        int i = 0;
        for ( final DxfBlock block : blockCollection ) {
            if ( block != null ) {
                blockNames[ i++ ] = block.getName();
            }
        }

        return blockNames;
    }

    // public Graphics2D getDefaultGraphics2D() {
    // return _graphicsMemory;
    // }

    public DxfDistanceUnit getDistanceUnit() {
        return _distanceUnit;
    }

    public DxfStatus getDxfStatus() {
        return _dxfStatus;
    }

    public DxfEntity getEntityByRef( final String pHandle ) {
        return _refEntities.get( pHandle );
    }

    public double getGlobalLineTypeScale() {
        return _linetypeScale;
    }

    public DxfEntity getLastAddedEntity() {
        return _lastEntity;
    }

    /**
     * @param pName
     *            Nombre de la capa buscada
     * @return objeto DxfLayer de nombre <code>pName</code>
     */
    public DxfLayer getLayer( final String pName ) {
        final String layerName = pName.toUpperCase();
        final DxfLayer layer = _tblLayer.get( layerName );
        if ( layer == null ) {
            return _defaultLayer;
        }
        return layer;
    }

    /**
     * Obtiene los nombres de todas las capas contenidas en el documento.
     *
     * @return array con los nombres de las capas
     */
    public List< String > getLayerNames() {
        if ( ( _tblLayer == null ) || ( _tblLayer.size() == 0 ) ) {
            return new ArrayList<>();
        }

        final ArrayList< String > layerNames = new ArrayList<>( _tblLayer.size() );
        final Collection< DxfLayer > layerCollection = _tblLayer.values();
        for ( final DxfLayer layer : layerCollection ) {
            if ( layer != null ) {
                layerNames.add( layer.getName() );
            }
        }

        return layerNames;
    }

    public double getLimitsMaxX() {
        return _limitsMaxX;
    }

    public double getLimitsMaxY() {
        return _limitsMaxY;
    }

    public double getLimitsMinX() {
        return _limitsMinX;
    }

    public double getLimitsMinY() {
        return _limitsMinY;
    }

    public DxfLineType getLineType( final String pName ) {
        final String lineTypeName = pName.toUpperCase( Locale.ENGLISH );
        final DxfLineType lineType = _tblLineType.get( lineTypeName );
        if ( lineType == null ) {
            return _defaultLineType;
        }
        return lineType;
    }

    /**
     * Este método es invocado cuando la lectura del DXF finaliza. Averigua los
     * bloques que serán usados como flechas de la entidad DIMENSION. Estos
     * bloques se almacenan en la propiedad <CODE>
     * arrows </CODE>.
     */
    public void initialize() {
        // Los bloques que forman las flechas de las dimensiones tienen color
        // BYBLOCK
        if ( _arrows != null ) {
            final PropertyOverriding pover = new PropertyOverriding();
            pover.setColorIndex( 0 ); // BYBLOCK
            for ( final String blkname : _arrows ) {
                final DxfBlock blk = getBlock( blkname );
                if ( blk != null ) {
                    blk.setPropertyOverriding( pover );
                }
            }
        }
    }

    public void setDistanceUnit( final DxfDistanceUnit distanceUnit ) {
        _distanceUnit = distanceUnit;
    }

    public void setGlobalLinetypeScale( final double pLScale ) {
        _linetypeScale = pLScale;
    }

    public void setLastAddedEntity( final DxfEntity pent ) {
        _lastEntity = pent;
    }

    public void setLimitsMax( final double limitsMaxX, final double limitsMaxY ) {
        _limitsMaxX = limitsMaxX;
        _limitsMaxY = limitsMaxY;
    }

    public void setLimitsMin( final double limitsMinX, final double limitsMinY ) {
        _limitsMinX = limitsMinX;
        _limitsMinY = limitsMinY;
    }

}// class DxfDocument