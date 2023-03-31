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
package com.mhschmieder.fxdxfparser.reader;

import java.util.Iterator;
import java.util.Locale;

import com.mhschmieder.fxdxfparser.entity.DxfArc;
import com.mhschmieder.fxdxfparser.entity.DxfCircle;
import com.mhschmieder.fxdxfparser.entity.DxfDimension;
import com.mhschmieder.fxdxfparser.entity.DxfEllipse;
import com.mhschmieder.fxdxfparser.entity.DxfEntity;
import com.mhschmieder.fxdxfparser.entity.DxfFace3D;
import com.mhschmieder.fxdxfparser.entity.DxfFaceDef;
import com.mhschmieder.fxdxfparser.entity.DxfInsert;
import com.mhschmieder.fxdxfparser.entity.DxfLine;
import com.mhschmieder.fxdxfparser.entity.DxfLwPolyline;
import com.mhschmieder.fxdxfparser.entity.DxfPoint;
import com.mhschmieder.fxdxfparser.entity.DxfPolyFaceMesh;
import com.mhschmieder.fxdxfparser.entity.DxfPolygonMesh;
import com.mhschmieder.fxdxfparser.entity.DxfPolyline;
import com.mhschmieder.fxdxfparser.entity.DxfRay;
import com.mhschmieder.fxdxfparser.entity.DxfSolid;
import com.mhschmieder.fxdxfparser.entity.DxfVertex;
import com.mhschmieder.fxdxfparser.entity.DxfXLine;
import com.mhschmieder.fxdxfparser.loader.DxfBlock;
import com.mhschmieder.fxdxfparser.loader.DxfEntityContainer;
import com.mhschmieder.fxdxfparser.physics.DxfDistanceUnit;
import com.mhschmieder.fxdxfparser.structure.DxfDocument;

public class DxfParser {

    // Enumeration of DXF Tables.
    private static final int  UNKNOWN      = -1;
    private static final int  APPID        = 1;
    private static final int  BLOCK_RECORD = 2;
    private static final int  DIMSTYLE     = 3;
    private static final int  LAYER        = 4;
    private static final int  LTYPE        = 5;
    private static final int  STYLE        = 6;
    private static final int  UCS          = 7;
    private static final int  VIEW         = 8;
    private static final int  VPORT        = 9;

    private int               _entry       = 0;

    // --- Temporales ----
    private DxfBlock          _newBlock;
    // -------------------

    // Documento DXF
    private final DxfDocument _dxfDocument;

    // Flag for status of whether block is started and being read.
    private boolean           _blockIsReading;

    // Ignore Paper Space entities if set, to save memory and time.
    protected boolean         _ignorePaperSpace;

    // Flag for whether to log the status of the DXF Load.
    protected boolean         _logDxfStatus;

    public DxfParser( final boolean ignorePaperSpace, final boolean logDxfStatus ) {
        super();

        _ignorePaperSpace = ignorePaperSpace;
        _logDxfStatus = logDxfStatus;

        _blockIsReading = false;

        _dxfDocument = new DxfDocument( logDxfStatus );
    }

    public final DxfDocument getDocument() {
        return _dxfDocument;
    }

    public final void markBlockCompleted( final DxfPairContainer pc ) {
        _blockIsReading = false;
    }

    public final void markBlockStarted( final DxfPairContainer pc, final String blockName ) {
        _blockIsReading = true;

        // Localiza espacio modelo y espacio papel.
        if ( !DxfDocument.MODEL_BLOCK.equalsIgnoreCase( blockName )
                && !DxfDocument.PAPER_BLOCK.equalsIgnoreCase( blockName ) ) {
            _newBlock = new DxfBlock( _dxfDocument, pc, blockName, 10 );
            _dxfDocument.addBlock( _newBlock );
        }
    }

    public final void markTableAppIdStarted( final DxfPairContainer pc ) {
        _entry = APPID;
    }

    public final void markTableBlockStarted( final DxfPairContainer pc ) {
        _entry = BLOCK_RECORD;
    }

    public final void markTableCompleted() {}

    public final void markTableDimStyleStarted( final DxfPairContainer pc ) {
        _entry = DIMSTYLE;
    }

    public final void markTableLayerStarted( final DxfPairContainer pc ) {
        _entry = LAYER;
    }

    public final void markTableLtypeStarted( final DxfPairContainer pc ) {
        _entry = LTYPE;
    }

    public final void markTableStyleStarted( final DxfPairContainer pc ) {
        _entry = STYLE;
    }

    public final void markTableUcsStarted( final DxfPairContainer pc ) {
        _entry = UCS;
    }

    public final void markTableViewStarted( final DxfPairContainer pc ) {
        _entry = VIEW;
    }

    public final void markTableVportStarted( final DxfPairContainer pc ) {
        _entry = VPORT;
    }

    public final void markUnknownTableStarted( final DxfPairContainer pc ) {
        _entry = UNKNOWN;
    }

    @SuppressWarnings("nls")
    public final void parseEntity( final DxfPairContainer pc,
                                   final EntityType entityType,
                                   final boolean blockContext ) {
        final boolean entityTypeSupported = EntityTypeHash.isEntityTypeSupported( entityType );
        if ( !entityTypeSupported ) {
            // Count unsupported entities as they come in, by context.
            if ( _logDxfStatus ) {
                if ( blockContext ) {
                    if ( _blockIsReading ) {
                        _dxfDocument._dxfStatus._numberOfUnsupportedBlockContextEntities++;

                        // Also count by entity type, specific to each context.
                        _dxfDocument._dxfStatus.addToUnsupportedBlockContextEntities( entityType );
                    }
                }
                else {
                    _dxfDocument._dxfStatus._numberOfUnsupportedModelAndPaperSpaceEntities++;

                    // Also count by entity type, specific to each context.
                    _dxfDocument._dxfStatus
                            .addToUnsupportedModelAndPaperSpaceEntities( entityType );
                }
            }

            return;
        }

        DxfEntity newent = null;
        try {
            // To avoid verbose unique names for throw-away variables,
            // pre-declare the ones that are only needed by a couple of more
            // complex entities.
            String handle;

            switch ( entityType ) {
            case ACAD_PROXY_ENTITY:
                break;
            case ARC:
                newent = new DxfArc( _dxfDocument, pc, entityType, _ignorePaperSpace );
                break;
            case ARCALIGNEDTEXT:
                break;
            case ATTDEF:
                // NOTE: Removed because even the old AWT version was
                // incomplete, incorrect and inferior to other parsers.
                // newent = new DxfAttdef( _dxfDocument, pc, entityType,
                // _ignorePaperSpace );
                break;
            case ATTRIB:
                // NOTE: Removed because even the old AWT version was
                // incomplete, incorrect and inferior to other parsers.
                // newent = new DxfAttrib( _dxfDocument, pc, entityType,
                // _ignorePaperSpace );
                //
                // handle = newent.getParentHandle();
                // final DxfEntityContainer attributeContainer = (
                // DxfEntityContainer ) _dxfDocument.getEntityByRef( handle );
                // if ( attributeContainer != null ) {
                // attributeContainer.addEntity( newent );
                // }
                //
                // We already added the entity indirectly.
                // newent = null;
                //
                break;
            case BODY:
                break;
            case CIRCLE:
                newent = new DxfCircle( _dxfDocument, pc, entityType, _ignorePaperSpace );
                break;
            case DIMENSION:
                newent = new DxfDimension( _dxfDocument, pc, entityType, _ignorePaperSpace );
                break;
            case ELLIPSE:
                newent = new DxfEllipse( _dxfDocument, pc, entityType, _ignorePaperSpace );
                break;
            case FACE3D:
                newent = new DxfFace3D( _dxfDocument, pc, entityType, _ignorePaperSpace );
                break;
            case FACEDEF:
                // This entity type is handled indirectly as a sub-case of
                // Vertex.
                break;
            case HATCH:
                break;
            case IMAGE:
                break;
            case INSERT:
                newent = new DxfInsert( _dxfDocument, pc, entityType, _ignorePaperSpace );
                break;
            case LEADER:
                break;
            case LINE:
                newent = new DxfLine( _dxfDocument, pc, entityType, _ignorePaperSpace );
                break;
            case LWPOLYLINE:
                newent = new DxfLwPolyline( _dxfDocument, pc, entityType, _ignorePaperSpace );
                break;
            case MLINE:
                break;
            case MTEXT:
                // NOTE: Removed because even the old AWT version was
                // incomplete, incorrect and inferior to other parsers.
                // newent = new DxfMText( _dxfDocument, pc, entityType,
                // _ignorePaperSpace );
                break;
            case PDFUNDERLAY:
                break;
            case POINT:
                newent = new DxfPoint( _dxfDocument, pc, entityType, _ignorePaperSpace );
                break;
            case POLYFACE3D:
                // This entity type is handled indirectly as a sub-case of
                // Polyline.
                break;
            case POLYGON3D:
                // This entity type is handled indirectly as a sub-case of
                // Polyline.
                break;
            case POLYLINE:
                final int flags = Integer.parseInt( pc.getValue( DxfGroupCodes.FLAGS, "0" ) );
                if ( ( flags & DxfPolyline.FLAG_POLYGON_MESH ) != 0 ) {
                    newent = new DxfPolygonMesh( _dxfDocument,
                                                 pc,
                                                 EntityType.POLYGON3D,
                                                 _ignorePaperSpace );
                }
                else if ( ( flags & DxfPolyline.FLAG_POLY_FACE_MESH ) != 0 ) {
                    newent = new DxfPolyFaceMesh( _dxfDocument,
                                                  pc,
                                                  EntityType.POLYFACE3D,
                                                  _ignorePaperSpace );
                }
                else if ( ( flags & DxfPolyline.FLAG_3DPOLYLINE ) != 0 ) {
                    // NOTE: We do not yet support 3D Polylines, but if we
                    // don't treat them as 2D Polylines for now vs. ignoring
                    // them, a lot of necessary entities get skipped in
                    // important files.
                    newent = new DxfPolyline( _dxfDocument,
                                              pc,
                                              EntityType.POLYLINE,
                                              _ignorePaperSpace );
                }
                else {
                    newent = new DxfPolyline( _dxfDocument,
                                              pc,
                                              EntityType.POLYLINE,
                                              _ignorePaperSpace );
                }
                break;
            case RAY:
                newent = new DxfRay( _dxfDocument, pc, entityType, _ignorePaperSpace );
                break;
            case REGION:
                break;
            case RTEXT:
                break;
            case SEQEND:
                // This isn't an Entity Type per se, but rather a marker for end
                // end of a sequence.
                break;
            case SHAPE:
                break;
            case SOLID:
                newent = new DxfSolid( _dxfDocument, pc, entityType, _ignorePaperSpace );
                break;
            case SOLID3D:
                break;
            case SPLINE:
                break;
            case TABLE:
                break;
            case TEXT:
                // NOTE: Removed because even the old AWT version was
                // incomplete, incorrect and inferior to other parsers.
                // newent = new DxfText( _dxfDocument, pc, entityType,
                // _ignorePaperSpace );
                break;
            case TOLERANCE:
                break;
            case TRACE:
                newent = new DxfSolid( _dxfDocument, pc, entityType, _ignorePaperSpace );
                break;
            case UNRECOGNIZED_ENTITY:
                break;
            case VERTEX:
                final int test = Integer.parseInt( pc.getValue( DxfGroupCodes.CODE71, "0" ) );
                if ( test == 0 ) {
                    newent = new DxfVertex( _dxfDocument, pc, entityType, _ignorePaperSpace );
                }
                else {
                    newent = new DxfFaceDef( _dxfDocument,
                                             pc,
                                             EntityType.FACEDEF,
                                             _ignorePaperSpace );
                }

                handle = newent.getParentHandle();
                if ( handle != null ) { // DOCUMENTOS ACTUALES
                    final DxfEntityContainer vertexContainer = ( DxfEntityContainer ) _dxfDocument
                            .getEntityByRef( handle );
                    if ( vertexContainer != null ) {
                        vertexContainer.addEntity( newent );
                    }
                }
                else { // DOCUMENTOS VIEJOS
                    final DxfEntityContainer vertexContainer = ( DxfEntityContainer ) _dxfDocument
                            .getLastAddedEntity();
                    if ( vertexContainer != null ) {
                        vertexContainer.addEntity( newent );
                    }
                }

                // We already added the entity indirectly.
                newent = null;

                break;
            case VIEWPORT:
                // NOTE: The Viewport entity is commented out, as it isn't
                // needed for 2D drawings and as its DXF definition has changed
                // with AutoCAD 2000/2000i/2002 and thus this R14 parser crashes
                // on reading it in.
                // TODO: Review status, and fix broken code if necessary.
                // newent = new DxfViewport( _dxfDocument, pc, entityType,
                // _ignorePaperSpace );
                break;
            case WIPEOUT:
                break;
            case XLINE:
                newent = new DxfXLine( _dxfDocument, pc, entityType, _ignorePaperSpace );
                break;
            default:
                break;
            }
        }
        catch ( final DxfReaderException e ) {
            // NOTE: This exception is a hack to ignore Paper Space, so don't
            // throw it to the logger.
        }

        if ( newent == null ) {
            if ( _logDxfStatus ) {
                if ( blockContext ) {
                    if ( _blockIsReading ) {
                        _dxfDocument._dxfStatus._numberOfBlockContextEntitiesIgnored++;

                        // Also count by entity type, specific to each context.
                        _dxfDocument._dxfStatus.addToBlockContextEntitiesIgnored( entityType );
                    }
                }
                else {
                    // We know from the context that the entity is null because
                    // it was rejected, which almost definitely means we are
                    // ignoring Paper Space, but we check anyway.
                    if ( _ignorePaperSpace ) {
                        _dxfDocument._dxfStatus._numberOfPaperSpaceEntitiesIgnored++;

                        // Also count by entity type, specific to each context.
                        _dxfDocument._dxfStatus.addToPaperSpaceEntitiesIgnored( entityType );
                    }
                    else {
                        _dxfDocument._dxfStatus._numberOfModelSpaceEntitiesIgnored++;

                        // Also count by entity type, specific to each context.
                        _dxfDocument._dxfStatus.addToModelSpaceEntitiesIgnored( entityType );
                    }
                }
            }

            return;
        }

        _dxfDocument.addEntityByRef( newent );

        // Check whether in blocks or entities section parsing context.
        if ( blockContext ) {
            if ( _blockIsReading ) {
                _newBlock.addEntity( newent );

                if ( _logDxfStatus ) {
                    _dxfDocument._dxfStatus._numberOfBlockContextEntitiesRead++;

                    // Also count by entity type, specific to each context.
                    _dxfDocument._dxfStatus.addToBlockContextEntitiesRead( entityType );
                }
            }
        }
        else {
            // Entities are either in Paper Space or Model Space.
            if ( newent._inPaperSpace ) {
                // If ignoring Paper Space, don't add the entity, but count it.
                if ( !_ignorePaperSpace ) {
                    _dxfDocument._paperSpace.addEntity( newent );

                    if ( _logDxfStatus ) {
                        _dxfDocument._dxfStatus._numberOfPaperSpaceEntitiesRead++;

                        // Also count by entity type, specific to each context.
                        _dxfDocument._dxfStatus.addToPaperSpaceEntitiesRead( entityType );
                    }
                }
                else {
                    if ( _logDxfStatus ) {
                        _dxfDocument._dxfStatus._numberOfPaperSpaceEntitiesIgnored++;

                        // Also count by entity type, specific to each context.
                        _dxfDocument._dxfStatus.addToPaperSpaceEntitiesIgnored( entityType );
                    }
                }
            }
            else {
                _dxfDocument._modelSpace.addEntity( newent );

                if ( _logDxfStatus ) {
                    _dxfDocument._dxfStatus._numberOfModelSpaceEntitiesRead++;

                    // Also count by entity type, specific to each context.
                    _dxfDocument._dxfStatus.addToModelSpaceEntitiesRead( entityType );
                }
            }
        }
    }

    @SuppressWarnings("nls")
    public final void parseHeaderVariables( final DxfPairContainer pc ) {
        // NOTE: Due to deep nesting of parsing logic, we must stick with the
        // older Iterator paradigm vs. the newer "for-each" paradigm.
        final Iterator< DxfPair > it = pc.iterator();
        while ( it.hasNext() ) {
            DxfPair pair = it.next();
            if ( DxfGroupCodes.CODE9 != pair.getKey() ) {
                continue;
            }

            final String variable = pair.getValue().toUpperCase( Locale.ENGLISH );
            switch ( variable ) {
            case "$DIMBLK":
            case "$DIMBLK1":
            case "$DIMBLK2":
                // NOTE: Although DIMBLK is for when arrow heads are the same,
                // we add to a list of arrow heads so there is no need to treat
                // the three cases differently (1 is for left, 2 is for right).
                pair = it.next();
                final String value = pair.getValue();
                if ( !value.trim().isEmpty() ) {
                    _dxfDocument.addArrowBlock( value );
                }
                break;
            case "$INSUNITS":
                pair = it.next();
                final int insunits = Integer.parseInt( pair.getValue() );
                final DxfDistanceUnit dxfDistanceUnit = DxfDistanceUnit
                        .indexToDistanceUnit( insunits );
                _dxfDocument.setDistanceUnit( dxfDistanceUnit );
                break;
            case "$LTSCALE":
                pair = it.next();
                final double ltscale = Double.parseDouble( pair.getValue() );
                _dxfDocument.setGlobalLinetypeScale( ltscale );
                break;
            case "$LIMMIN":
                pair = it.next();
                final double limitsMinX = Double.parseDouble( pair.getValue() );
                pair = it.next();
                final double limitsMinY = Double.parseDouble( pair.getValue() );
                _dxfDocument.setLimitsMin( limitsMinX, limitsMinY );
                break;
            case "$LIMMAX":
                pair = it.next();
                final double limitsMaxX = Double.parseDouble( pair.getValue() );
                pair = it.next();
                final double limitsMaxY = Double.parseDouble( pair.getValue() );
                _dxfDocument.setLimitsMax( limitsMaxX, limitsMaxY );
                break;
            default:
                break;
            }
        }
    }

    @SuppressWarnings("nls")
    public final void parseTable( final DxfPairContainer pc, final String name ) {
        int flags;
        switch ( _entry ) {
        case BLOCK_RECORD:
            break;

        case LAYER:
            flags = Integer.parseInt( pc.getValue( DxfGroupCodes.FLAGS, "0" ) );
            final String lyname = pc.getValue( DxfGroupCodes.CODE2 );
            final String lineType = pc.getValue( DxfGroupCodes.LINE_TYPE );
            // colorNumber : negativo si Layer Off
            final int colorNumber = Integer.parseInt( pc.getValue( DxfGroupCodes.COLOR ) );
            _dxfDocument.addLayer( lyname, flags, colorNumber, lineType );
            break;

        case LTYPE:
            // TODO: Check Code 72 for "is scaled to fit".
            flags = Integer.parseInt( pc.getValue( DxfGroupCodes.FLAGS, "0" ) );
            final int complexflags = Integer.parseInt( pc.getValue( DxfGroupCodes.CODE74, "0" ) );
            final String ltname = pc.getValue( DxfGroupCodes.CODE2 );
            final String desc = pc.getValue( DxfGroupCodes.CODE3, "" );
            final int nummberOfDashes =
                                      Integer.parseInt( pc.getValue( DxfGroupCodes.CODE73, "0" ) );
            final double patternLength = Double
                    .parseDouble( pc.getValue( DxfGroupCodes.CODE40, "0" ) );
            double[] pattern = null;
            if ( nummberOfDashes > 0 ) {
                pattern = new double[ nummberOfDashes ];
                final Iterator< String > it =
                                            pc.iteratorForValue( DxfGroupCodes.LINE_TYPE_SPACING );
                int i = 0;
                final int lastItemIndex = nummberOfDashes - 1;
                while ( it.hasNext() ) {
                    if ( i <= lastItemIndex ) {
                        pattern[ i++ ] = Double.parseDouble( it.next() );
                    }
                }
            }
            _dxfDocument.addLineType( ltname,
                                      flags,
                                      complexflags,
                                      desc,
                                      nummberOfDashes,
                                      pattern,
                                      patternLength );
            break;

        case STYLE:
            // :NOTE Support removed as of MAPP XT 1.2.4, as it wastes memory
            // for no reason since the text support had to be removed due to
            // never working properly even in the original parser.
            break;

        default:
            break;
        }
    }

    public void read() {
        _dxfDocument.initialize();
    }

    public boolean returnControlStrings() {
        return false;
    }

}// class DxfParser