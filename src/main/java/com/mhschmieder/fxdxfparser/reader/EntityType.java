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

import java.util.Locale;

/**
 * This is an enumeration of all known DXF entity types, supported or not.
 * <p>
 * Some Entity Types are referenced indirectly via DXF codes, such as FaceDef
 * (from Vertex), Polyface3D (from Polyline), and Polygon3D (from Polyline).
 * <p>
 * TODO: Review whether Table would ever be referenced in this context.
 */
public enum EntityType {
    ACAD_PROXY_ENTITY,
    ARC,
    ARCALIGNEDTEXT,
    ATTDEF,
    ATTRIB,
    BODY,
    CIRCLE,
    DIMENSION,
    ELLIPSE,
    FACE3D,
    FACEDEF,
    HATCH,
    IMAGE,
    INSERT,
    LEADER,
    LINE,
    LWPOLYLINE,
    MLINE,
    MTEXT,
    PDFUNDERLAY,
    POINT,
    POLYFACE3D,
    POLYGON3D,
    POLYLINE,
    RAY,
    REGION,
    RTEXT,
    SEQEND,
    SHAPE,
    SOLID,
    SOLID3D,
    SPLINE,
    TABLE,
    TEXT,
    TOLERANCE,
    TRACE,
    UNRECOGNIZED_ENTITY,
    VERTEX,
    VIEWPORT,
    WIPEOUT,
    XLINE;

    @SuppressWarnings("nls")
    public static EntityType canonicalValueOf( final String entityType ) {
        // NOTE: Java does not allow symbols to start with anything other than
        // letters, so we have to invert the spelling of entities that start
        // with numbers in DXF.
        final String canonicalEntityType = entityType.toUpperCase( Locale.ENGLISH );
        
        EntityType entityTypeValue = UNRECOGNIZED_ENTITY;
        
        switch ( canonicalEntityType ) {
        case "3DFACE":
            entityTypeValue = FACE3D;
            break;
        case "3DSOLID":
            entityTypeValue = SOLID3D;
            break;
        // $CASES-OMITTED$
        default:
            try {
                entityTypeValue = valueOf( canonicalEntityType );
            }
            catch ( final Exception e ) {
                e.printStackTrace();
                entityTypeValue = UNRECOGNIZED_ENTITY;
            }
        }
        
        return entityTypeValue;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString() {
        // NOTE: Java does not allow symbols to start with anything other than
        // letters, so we have to invert the spelling of entities that start
        // with numbers in DXF.
        String stringName = null;
        
        switch ( this ) {
        case FACE3D:
            stringName = "3DFACE";
            break;
        case SOLID3D:
            stringName = "3DSOLID";
            break;
        // $CASES-OMITTED$
        default:
            stringName = super.toString();
            break;
        }
        
        return stringName;
    }

}// enum EntityType
