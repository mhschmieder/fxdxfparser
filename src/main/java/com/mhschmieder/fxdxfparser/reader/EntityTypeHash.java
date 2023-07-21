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
package com.mhschmieder.fxdxfparser.reader;

import java.util.HashSet;

/**
 * This is a wrapper for a HashSet that lists currently supported DXF Entity
 * Types, for keeping track of them as support is added for new ones.
 */
public final class EntityTypeHash {

    // Total number of currently supported entity types.
    private static final int                   NUMBER_OF_SUPPORTED_ENTITY_TYPES = 19;

    // HashSet for supported Entity Types, for a quick determination of
    // unsupported entities. This is mostly used to gather statistics.
    private static final HashSet< EntityType > _entityTypeSupportedSet          =
                                                                       new HashSet<>( NUMBER_OF_SUPPORTED_ENTITY_TYPES );

    // NOTE: The Viewport entity is commented out, as it isn't needed for 2D
    // drawings and as its DXF definition has changed with AutoCAD
    // 2000/2000i/2002 and thus this R14 parser crashes on reading it in.
    // NOTE: Removed all text-based entities because even the old AWT version
    // was incomplete, incorrect and inferior to other parsers.
    static {
        // _entityTypeSupportedSet.add( EntityType.ACAD_PROXY_ENTITY );
        _entityTypeSupportedSet.add( EntityType.ARC );
        // _entityTypeSupportedSet.add( EntityType.ARCALIGNEDTEXT );
        // _entityTypeSupportedSet.add( EntityType.ATTDEF );
        // _entityTypeSupportedSet.add( EntityType.ATTRIB );
        // _entityTypeSupportedSet.add( EntityType.BODY );
        _entityTypeSupportedSet.add( EntityType.CIRCLE );
        _entityTypeSupportedSet.add( EntityType.DIMENSION );
        _entityTypeSupportedSet.add( EntityType.ELLIPSE );
        _entityTypeSupportedSet.add( EntityType.FACE3D );
        _entityTypeSupportedSet.add( EntityType.FACEDEF );
        // _entityTypeSupportedSet.add( EntityType.HATCH );
        // _entityTypeSupportedSet.add( EntityType.IMAGE );
        _entityTypeSupportedSet.add( EntityType.INSERT );
        // _entityTypeSupportedSet.add( EntityType.LEADER );
        _entityTypeSupportedSet.add( EntityType.LINE );
        _entityTypeSupportedSet.add( EntityType.LWPOLYLINE );
        // _entityTypeSupportedSet.add( EntityType.MLINE );
        // _entityTypeSupportedSet.add( EntityType.MTEXT );
        _entityTypeSupportedSet.add( EntityType.POINT );
        _entityTypeSupportedSet.add( EntityType.POLYFACE3D );
        _entityTypeSupportedSet.add( EntityType.POLYGON3D );
        _entityTypeSupportedSet.add( EntityType.POLYLINE );
        _entityTypeSupportedSet.add( EntityType.RAY );
        // _entityTypeSupportedSet.add( EntityType.REGION );
        // _entityTypeSupportedSet.add( EntityType.RTEXT );
        _entityTypeSupportedSet.add( EntityType.SEQEND );
        // _entityTypeSupportedSet.add( EntityType.SHAPE );
        _entityTypeSupportedSet.add( EntityType.SOLID );
        // _entityTypeSupportedSet.add( EntityType.SOLID3D );
        // _entityTypeSupportedSet.add( EntityType.SPLINE );
        // _entityTypeSupportedSet.add( EntityType.TABLE );
        // _entityTypeSupportedSet.add( EntityType.TEXT );
        // _entityTypeSupportedSet.add( EntityType.TOLERANCE );
        _entityTypeSupportedSet.add( EntityType.TRACE );
        _entityTypeSupportedSet.add( EntityType.VERTEX );
        // _entityTypeSupportedSet.add( EntityType.VIEWPORT );
        // _entityTypeSupportedSet.add( EntityType.WIPEOUT );
        _entityTypeSupportedSet.add( EntityType.XLINE );
    }

    public static boolean isEntityTypeSupported( final EntityType entityType ) {
        return _entityTypeSupportedSet.contains( entityType );
    }

}// class EntityTypeHash
