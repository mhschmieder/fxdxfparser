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

import com.mhschmieder.fxdxfparser.geometry.DxfShapeContainer;

import javafx.scene.transform.Affine;

public interface DxfDrawable {

    /**
     * This is the method to call for converting parsed DXF geometry into
     * JavaFX 2D Shapes. This version of the method throws out the
     * z-coordinate rather than providing a projection plane.
     *
     * @param dxfShapeContainer
     *            The Scene Graph Group container for all graphics required to
     *            represent this entity
     * @param transform
     *            The combined transform to apply; including any from nested
     *            blocks
     * @param strokeScale
     *            The initial stroke scale to approximate the client's preferred
     *            line thickness
     * @return {@code true} if the shapes were successfully converted to JavaFX
     */
    boolean convertToFxShapes( final DxfShapeContainer dxfShapeContainer,
                               final Affine transform,
                               final double strokeScale );

}// interface DxfDrawable
