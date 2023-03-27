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
package com.mhschmieder.fxdxftoolkit.entity;

import com.mhschmieder.fxdxftoolkit.geometry.FaceType;
import com.mhschmieder.fxdxftoolkit.reader.DxfGroupCodes;
import com.mhschmieder.fxdxftoolkit.reader.DxfPairContainer;
import com.mhschmieder.fxdxftoolkit.reader.DxfReaderException;
import com.mhschmieder.fxdxftoolkit.reader.EntityType;
import com.mhschmieder.fxdxftoolkit.structure.DxfDocument;

public class DxfFaceDef extends DxfEntity {

    public int[] _iv;

    public DxfFaceDef( final DxfDocument pdoc,
                       final DxfPairContainer pc,
                       final EntityType entityType,
                       final boolean ignorePaperSpace )
            throws DxfReaderException {
        super( pdoc, pc, entityType, ignorePaperSpace );
    }

    public FaceType getFaceType() {
        FaceType faceType = FaceType.UNDEFINED;
        
        switch ( _iv.length ) {
        case 1:
            faceType = FaceType.POINT;
            break;
        case 2:
            faceType = FaceType.LINE;
            break;
        case 3:
            faceType = FaceType.TRIANGLE;
            break;
        case 4:
            faceType = FaceType.QUAD;
            break;
        default:
            break; 
        }
        
        return faceType;
    }

    @Override
    @SuppressWarnings("nls")
    protected void parseEntityProperties( final DxfPairContainer pc ) {
        final int iv1 = Integer.parseInt( pc.getValue( DxfGroupCodes.CODE71 ) );
        final int iv2 = Integer.parseInt( pc.getValue( DxfGroupCodes.CODE72, "-1" ) );
        final int iv3 = Integer.parseInt( pc.getValue( DxfGroupCodes.CODE73, "-1" ) );
        final int iv4 = Integer.parseInt( pc.getValue( DxfGroupCodes.CODE74, "-1" ) );

        if ( iv4 < 0 ) {
            if ( iv3 < 0 ) {
                if ( iv2 < 0 ) {
                    _iv = new int[] { iv1 };
                }
                else {
                    _iv = new int[] { iv1, iv2 };
                }
            }
            else {
                _iv = new int[] { iv1, iv2, iv3 };
            }
        }
        else {
            _iv = new int[] { iv1, iv2, iv3, iv4 };
        }
    }

}// class DxfFaceDef
