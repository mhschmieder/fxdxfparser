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

import com.mhschmieder.fxdxfparser.reader.EntityType;

import java.util.HashMap;

public class DxfStatus {

    public static final void addToEntitiesByType( final EntityType entityType,
                                                  final HashMap< EntityType, Integer > numberOfEntitiesByType ) {
        if ( entityType == null ) {
            return;
        }

        final int previousEntityCount = numberOfEntitiesByType.containsKey( entityType )
            ? numberOfEntitiesByType.get( entityType )
            : 0;
        final int currentEntityCount = previousEntityCount + 1;
        numberOfEntitiesByType.put( entityType, currentEntityCount );
    }

    public int                                  _numberOfBlockContextEntitiesRead                    =
                                                                                  0;
    public int                                  _numberOfModelSpaceEntitiesRead                      =
                                                                                0;
    public int                                  _numberOfPaperSpaceEntitiesRead                      =
                                                                                0;

    public int                                  _numberOfBlockContextEntitiesIgnored                 =
                                                                                     0;
    public int                                  _numberOfModelSpaceEntitiesIgnored                   =
                                                                                   0;
    public int                                  _numberOfPaperSpaceEntitiesIgnored                   =
                                                                                   0;

    public int                                  _numberOfUnsupportedBlockContextEntities             =
                                                                                         0;
    public int                                  _numberOfUnsupportedModelAndPaperSpaceEntities       =
                                                                                               0;

    public final HashMap< EntityType, Integer > _numberOfBlockContextEntitiesReadByType              =
                                                                                        new HashMap<>( 100 );
    public final HashMap< EntityType, Integer > _numberOfModelSpaceEntitiesReadByType                =
                                                                                      new HashMap<>( 100 );
    public final HashMap< EntityType, Integer > _numberOfPaperSpaceEntitiesReadByType                =
                                                                                      new HashMap<>( 100 );

    public final HashMap< EntityType, Integer > _numberOfBlockContextEntitiesIgnoredByType           =
                                                                                           new HashMap<>( 100 );
    public final HashMap< EntityType, Integer > _numberOfModelSpaceEntitiesIgnoredByType             =
                                                                                         new HashMap<>( 100 );
    public final HashMap< EntityType, Integer > _numberOfPaperSpaceEntitiesIgnoredByType             =
                                                                                         new HashMap<>( 100 );

    public final HashMap< EntityType, Integer > _numberOfUnsupportedBlockContextEntitiesByType       =
                                                                                               new HashMap<>( 100 );
    public final HashMap< EntityType, Integer > _numberOfUnsupportedModelAndPaperSpaceEntitiesByType =
                                                                                                     new HashMap<>( 100 );

    public final void addToBlockContextEntitiesIgnored( final EntityType entityType ) {
        addToEntitiesByType( entityType, _numberOfBlockContextEntitiesIgnoredByType );
    }

    public final void addToBlockContextEntitiesRead( final EntityType entityType ) {
        addToEntitiesByType( entityType, _numberOfBlockContextEntitiesReadByType );
    }

    public final void addToModelSpaceEntitiesIgnored( final EntityType entityType ) {
        addToEntitiesByType( entityType, _numberOfModelSpaceEntitiesIgnoredByType );
    }

    public final void addToModelSpaceEntitiesRead( final EntityType entityType ) {
        addToEntitiesByType( entityType, _numberOfModelSpaceEntitiesReadByType );
    }

    public final void addToPaperSpaceEntitiesIgnored( final EntityType entityType ) {
        addToEntitiesByType( entityType, _numberOfPaperSpaceEntitiesIgnoredByType );
    }

    public final void addToPaperSpaceEntitiesRead( final EntityType entityType ) {
        addToEntitiesByType( entityType, _numberOfPaperSpaceEntitiesReadByType );
    }

    public final void addToUnsupportedBlockContextEntities( final EntityType entityType ) {
        addToEntitiesByType( entityType, _numberOfUnsupportedBlockContextEntitiesByType );
    }

    public final void addToUnsupportedModelAndPaperSpaceEntities( final EntityType entityType ) {
        addToEntitiesByType( entityType, _numberOfUnsupportedModelAndPaperSpaceEntitiesByType );
    }

}// class DxfStatus
