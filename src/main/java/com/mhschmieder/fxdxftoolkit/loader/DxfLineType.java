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
package com.mhschmieder.fxdxftoolkit.loader;

import java.util.ArrayList;
import java.util.Collection;

/**
 * DxfLineType contiene la definición de un linetype de Autocad. La
 * información es extraída de la correspondiente entrada de la tabla LTYPE de
 * un archivo DXF.
 * <P>
 * No soporta tipos de línea complejos (shapes, etc). Actualmente las linetypes
 * complejas no se cargan, con lo cual todas las primitivas que las usen
 * aparecerán como líneas continuas.
 */
public final class DxfLineType {

    private final String   _name;
    private final String   _desc;

    private final double[] _pattern;
    private final double   _patternLength;

    private final int      _patternItemCount;

    private final int      _flags;
    private final int      _complexFlags;

    public DxfLineType( final String pName,
                        final int pFlags,
                        final int pComplexFlags,
                        final String pDesc,
                        final int pNumItems,
                        final double[] pPattern,
                        final double pPatternLength ) {
        _name = pName;
        _flags = pFlags;
        _complexFlags = pComplexFlags;
        _desc = pDesc;
        _patternItemCount = pNumItems;
        _pattern = pPattern;
        _patternLength = pPatternLength;
    }

    /**
     * @return descripción del tipo de línea
     */
    public String getDescription() {
        return _desc;
    }

    /**
     * @return nombre del tipo de línea
     */
    public String getName() {
        return _name;
    }

    public double getPatternLength() {
        return _patternLength;
    }

    /**
     * Indica si el objeto representa un tipo de línea continuo. Más
     * concretamente, si el objeto no define ningún patrón (<code>
     * patternItemCount == 0</code> ), entonces es un tipo de línea continuo
     * (linetype CONTINUOUS de Autocad).
     * 
     * @return {@code true} if the line type is continuous
     */
    public boolean isContinuous() {
        /*
         * En tal caso, no es posible hacer uso del método design, puesto que
         * no hay patrón a seguir
         */
        return _patternItemCount == 0;
    }

    @SuppressWarnings("nls")
    public Collection< Double > makeDashArray( final double lineTypeScale ) {
        // Clean up the descriptor to balance on/off patterns.
        String descriptionCorrected = _desc.trim();
        if ( ( descriptionCorrected.length() % 2 ) == 1 ) {
            descriptionCorrected += " ";
        }

        // Make sure the paired on/off values are complete, to avoid indexing
        // exceptions inside the pattern loop.
        final char[] pattern = descriptionCorrected.toCharArray();
        final int numberOfDashes = pattern.length - ( pattern.length % 2 );
        final int dashArrayLength = numberOfDashes * 2;

        final ArrayList< Double > dashArray = new ArrayList<>( dashArrayLength );

        // TODO: Determine whether the older linestrip-oriented algorithm to
        // "design" the pattern is still needed, and if so, convert its output
        // to a simple/efficient dash array vs. an array of line/shape objects.
        // NOTE: Probably instead, we need to use the cached pattern, and check
        // each number for negative, zero, or positive, and apply accordingly,
        // with line type scale. See p. 140 in the AutoCAD 2000 Objects book for
        // details, and refer to the older design() method in the archived
        // LineTypeDef file. It is likely that the description is equivalent
        // though, as it is the only field that is exposed via Active X API's.
        int dashArrayIndex = 0;
        for ( int dashTypeIndex = 0; dashTypeIndex < numberOfDashes; dashTypeIndex++ ) {
            double onLength = 3.0d;
            double offLength = 3.0d;

            // AutoCAD defines a dash as 0.25, a gap as 0.5, and a dot as 0, in
            // terms of their ratio relative to an overall pattern length of 1.
            // This basis is supposed to be scaled by the Global LineType Scale.
            final char dashType = pattern[ dashTypeIndex ];
            if ( dashType == ' ' ) {
                // A space means the full pattern length goes to the "off" part.
                onLength = 0.0d;
                offLength = 10.0d;
            }
            else if ( dashType == '.' ) {
                onLength = 1.0d;
                offLength = 3.0d;
            }
            else if ( dashType == '-' ) {
                onLength = 5.0d;
                offLength = 3.0d;
            }
            else if ( dashType == '_' ) {
                // A gap means the full pattern length foes to the "on" part.
                onLength = 10.0d;
                offLength = 0.0d;
            }

            // Modify the core pattern by the Global Line Type Scale.
            dashArray.add( dashArrayIndex++, onLength * lineTypeScale );
            dashArray.add( dashArrayIndex++, offLength * lineTypeScale );
        }

        return dashArray;
    }

    /**
     * @return nombre del tipo de línea y a continuación su descripción
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return _name + " " + _desc;
    }

}// class DxfLineType
