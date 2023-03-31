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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Locale;

public class DxfReader {

    // Pila de un único par usada durante el analisis
    private DxfPair              _cachePair;

    private final BufferedReader _inBuffer;
    private final DxfParser      _parser;

    // seguimiento del numero de líneas leidas
    private int                  _line;

    private final boolean        _ignoreControlString;

    // ----------------------------
    // DxfReader
    // Parametros pReader >> objeto Reader del que extraer el DXF
    // pTParser >> Clase que solicita el analisis del DXF
    // -----------------------------
    public DxfReader( final BufferedReader pReader, final DxfParser pParser ) {
        _cachePair = null;
        _inBuffer = pReader;
        _parser = pParser;
        _line = 1;
        _ignoreControlString = pParser.returnControlStrings();
    }

    private void eatUntil( final int pCode, final String pVal ) throws DxfReaderException {
        while ( true ) {
            final DxfPair pair = readPair();
            if ( ( pair.getKey() == pCode ) && pair.getValue().equalsIgnoreCase( pVal ) ) {
                return;
            }
        }
    }

    @SuppressWarnings("nls")
    private void parseBlocksSection() throws DxfReaderException {
        final DxfPairContainer struct = new DxfPairContainer();

        boolean blocksSectionParsing = true;
        while ( blocksSectionParsing ) {
            struct.clear();
            final String name = parseStructure( struct ).toUpperCase( Locale.ENGLISH );
            switch ( name ) {
            case "ENDSEC":
                blocksSectionParsing = false;
                break;
            case "BLOCK":
                // Get the Entity Type for the Block.
                final String val = struct.getValue( DxfGroupCodes.CODE2 ).toUpperCase();
                _parser.markBlockStarted( struct, val );
                break;
            case "ENDBLK":
                _parser.markBlockCompleted( struct );
                break;
            default:
                // Enter the parsing for the Blocks section.
                final EntityType entityType = EntityType.canonicalValueOf( name );
                _parser.parseEntity( struct, entityType, true );
                break;
            }
        }
    }

    @SuppressWarnings("nls")
    private void parseEntitiesSection() throws DxfReaderException {
        final DxfPairContainer struct = new DxfPairContainer();

        boolean entitiesSectionParsing = true;
        while ( entitiesSectionParsing ) {
            struct.clear();
            final String name = parseStructure( struct ).toUpperCase( Locale.ENGLISH );
            switch ( name ) {
            case "ENDSEC":
                entitiesSectionParsing = false;
                break;
            default:
                // Enter the parsing for the Entities section.
                final EntityType entityType = EntityType.canonicalValueOf( name );
                _parser.parseEntity( struct, entityType, false );
                break;
            }
        }
    }

    @SuppressWarnings("nls")
    private void parseHeaderSection() throws DxfReaderException {
        final DxfPairContainer struct = new DxfPairContainer();

        boolean headerSectionParsing = true;
        while ( headerSectionParsing ) {
            final String name = parseStructure( struct );
            switch ( name ) {
            case "ENDSEC":
                headerSectionParsing = false;
                break;
            default:
                // Enter the parsing for the Header Variables.
                _parser.parseHeaderVariables( struct );
                break;
            }
        }
    }

    // ----------------------------
    // parseStructure
    // Descripcion Extrae los elementos de un bloque estructural de Autocad
    // (aquel que esta delimitado por codigos 0)
    // Parametros
    // Valor devuelto parametro struct -> los pares codigo/valor que conforman
    // la estructura
    // (String) -> el nombre de la estructura tal y como aparece en el DXF
    // -----------------------------
    @SuppressWarnings("nls")
    private String parseStructure( final DxfPairContainer struct ) throws DxfReaderException {
        boolean isFirst = true; // Una estructura no siempre comienza por 0, en
        // cuyo caso la estructura no tiene nombre
        String structName = "";
        boolean ignoreOn = false;

        boolean structureParsing = true;
        while ( structureParsing ) {
            final DxfPair pair = readPair();
            final int code = pair.getKey();
            final String val = pair.getValue();
            final int codeint = code;

            if ( codeint != 0 ) {
                isFirst = false;
            }

            if ( ( codeint == 102 ) && _ignoreControlString ) {
                ignoreOn = !ignoreOn;
            }

            if ( ( codeint < 0 ) || ignoreOn ) {}
            else if ( codeint > 0 ) {
                struct.add( code, val );
            }
            else {
                if ( structName.isEmpty() && isFirst ) {
                    structName = val;
                }
                else {
                    pushPair( pair );
                    structureParsing = false;
                }
            }
        }

        return structName;
    }

    @SuppressWarnings("nls")
    private void parseTable( final DxfPairContainer struct ) {
        // tipo de la tabla
        final String val = struct.getValue( DxfGroupCodes.CODE2 ).toUpperCase( Locale.ENGLISH );
        switch ( val ) {
        case "APPID":
            _parser.markTableAppIdStarted( struct );
            break;
        case "BLOCK_RECORD":
            _parser.markTableBlockStarted( struct );
            break;
        case "DIMSTYLE":
            _parser.markTableDimStyleStarted( struct );
            break;
        case "LAYER":
            _parser.markTableLayerStarted( struct );
            break;
        case "STYLE":
            _parser.markTableStyleStarted( struct );
            break;
        case "LTYPE":
            _parser.markTableLtypeStarted( struct );
            break;
        case "UCS":
            _parser.markTableUcsStarted( struct );
            break;
        case "VIEW":
            _parser.markTableViewStarted( struct );
            break;
        case "VPORT":
            _parser.markTableVportStarted( struct );
            break;
        default:
            _parser.markUnknownTableStarted( struct );
            break;
        }
    }

    @SuppressWarnings("nls")
    private void parseTablesSection() throws DxfReaderException {
        final DxfPairContainer struct = new DxfPairContainer();

        boolean tablesSectionParsing = true;
        while ( tablesSectionParsing ) {
            struct.clear();
            final String name = parseStructure( struct ).toUpperCase( Locale.ENGLISH );
            switch ( name ) {
            case "ENDSEC":
                tablesSectionParsing = false;
                break;
            case "TABLE":
                parseTable( struct );
                break;
            case "ENDTAB":
                _parser.markTableCompleted();
                break;
            default:
                // Enter the parsing for the Tables section.
                _parser.parseTable( struct, name );
                break;
            }
        }
    }

    private void pushPair( final DxfPair pair ) {
        _cachePair = pair;
    }

    private DxfPair readPair() throws DxfReaderException {
        if ( _cachePair != null ) {
            final DxfPair valret = _cachePair;
            _cachePair = null;
            return valret;
        }

        try {
            String s = _inBuffer.readLine();
            _line++;
            final int code = Integer.parseInt( s.trim() );
            s = _inBuffer.readLine();
            _line++;
            s = s.trim();
            return new DxfPair( code, s );
        }
        catch ( final NumberFormatException nfe ) {
            nfe.printStackTrace();
            throw new DxfReaderException( "Invalid DXF file: DXF Code is not an integer at line " //$NON-NLS-1$
                    + _line + ". Is this a binary file?" ); //$NON-NLS-1$
        }
        catch ( final IOException ioe ) {
            ioe.printStackTrace();
            throw new DxfReaderException( "Invalid DXF file: DXF Code is not text-readable at line " //$NON-NLS-1$
                    + _line + ". Is this a binary file?" ); //$NON-NLS-1$
        }
        catch ( final NullPointerException npe ) {
            // Ignore Null Pointer Exceptions, as they are either due to
            // reaching the end of the stream -- which isn't an error -- or
            // indicate that the original supplied input buffer is null.
            return null;
        }
    }

    // ----------------------------
    // runReader
    // Descripcion Ejecuta el analisis del DXF; lanzara los eventos oportunos en
    // la clase cliente DXFTarget
    // Parametros
    // -----------------------------
    @SuppressWarnings("nls")
    public void runReader() throws DxfReaderException {
        DxfPair pair = readPair();

        boolean endOfFile = false;
        while ( !endOfFile ) {
            final int code = pair.getKey();
            final String value = pair.getValue().toUpperCase( Locale.ENGLISH );
            switch ( code ) {
            case 0:
                switch ( value ) {
                case "EOF":
                    endOfFile = true;
                    continue;
                case "ENDSEC":
                    break;
                case "SECTION":
                    break;
                default:
                    break;
                }
                break;
            case 2:
                switch ( value ) {
                case "HEADER":
                    parseHeaderSection();
                    break;
                case "CLASSES":
                    eatUntil( 0, "ENDSEC" );
                    break;
                case "TABLES":
                    parseTablesSection();
                    break;
                case "BLOCKS":
                    parseBlocksSection();
                    break;
                case "ENTITIES":
                    parseEntitiesSection();
                    break;
                case "OBJECTS":
                    eatUntil( 0, "ENDSEC" );
                    break;
                default:
                    eatUntil( 0, "ENDSEC" );
                    break;
                }
                break;
            default:
                // Ignore all other codes at this level.
                break;
            }

            pair = readPair();
        }

        _parser.read();
    }

}// class DxfReader
