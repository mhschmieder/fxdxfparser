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
package com.mhschmieder.fxdxfparser.loader;

import java.util.Locale;

import org.apache.commons.math3.util.FastMath;

/**
 * DxfLayer representa la definición de una capa de Autocad. La información es
 * extraída de la correspondiente entrada de la tabla LAYER de un archivo DXF.
 */

public final class DxfLayer {
    // *********************************************************
    /** capa activada */
    private static final int LAYER_ON  = 0;

    /** capa desactivada */
    private static final int LAYER_OFF = 1;

    /** Nombre de la capa */
    private String           name;

    /** flags, tal y como se define en la especificación DXF */
    // private int flags;
    /** índice de color de la capa */
    private int              color;

    /** tipo de línea de la capa */
    private String           linetype;

    /** _estado actual de la capa: activado/desactivado */
    private int              state;

    protected DxfLayer() {}

    public DxfLayer( final String pName,
                     final int pFlags,
                     final int pColor,
                     final String pLinetype ) {
        if ( pColor < 0 ) {
            state = LAYER_OFF;
        }
        else {
            state = LAYER_ON;
        }

        name = pName;
        // flags = pFlags;
        color = FastMath.abs( pColor );
        linetype = pLinetype.toUpperCase( Locale.ENGLISH );
    }

    /**
     * Obtiene el índice de color de la capa.
     *
     * @return índice de color de la capa.
     */
    public int getColorIndex() {
        return color;
    }

    public String getLineTypeName() {
        return linetype;
    }

    /**
     * Obtiene el nombre de la capa
     *
     * @return nombre de la capa
     */
    public String getName() {
        return name;
    }

    /**
     * Indica el _estado actual de la capa (activa / inactiva).
     *
     * @return true si la capa está activada (es visible)
     */
    public boolean isLayerOn() {
        return state == LAYER_ON;
    }

    /**
     * Establece el _estado de visibilidad de la capa. Si la capa se desactiva,
     * las entidades que se encuentren en la misma no se añadirán a la escena
     * Java3D.
     *
     * @param pOn
     *            true si se quiere hacer visible la capa
     */
    public void setLayerOn( final boolean pOn ) {
        state = ( pOn ? LAYER_ON : LAYER_OFF );
    }

}// final class DxfLayer
