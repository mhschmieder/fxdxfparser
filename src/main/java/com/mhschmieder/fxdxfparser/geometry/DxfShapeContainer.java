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
package com.mhschmieder.fxdxfparser.geometry;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;

/**
 * This container allows for encapsulation of multiple JavaFX Shapes for a
 * {@link Group}, for nested grouping and to avoid passing multiple JavaFX
 * Shapes to the {@link Group} base class.
 */
public class DxfShapeContainer extends Group implements ExplicitlyBounded {

    /** Stroke width, to be applied to the entire JavaFX Shapes Group. */
    protected final DoubleProperty strokeWidth;

    /** Minimum point for the Shapes Group; usually from Drawing Limits. */
    private final double           _minX;
    private final double           _minY;

    /** Maximum point for the Shapes Group; usually from Drawing Limits. */
    private final double           _maxX;
    private final double           _maxY;

    // Default constructor, when bounds are not known.
    public DxfShapeContainer() {
        // Always call the super-constructor first!
        this( 0.0d, 0.0d, 0.0d, 0.0d );
    }

    // Fully qualified constructor, when bounds are known.
    // NOTE: It is safer to invoke this constructor than to reset an existing
    // container and then set its bounds and units, as clearing a Group's
    // children may run on a deferred thread.
    public DxfShapeContainer( final double minX,
                              final double minY,
                              final double maxX,
                              final double maxY ) {
        // Always call the superclass constructor first!
        super();

        _minX = minX;
        _minY = minY;
        _maxX = maxX;
        _maxY = maxY;

        strokeWidth = new SimpleDoubleProperty( 1.0d );
    }

    public final void addShape( final double strokeScale, final Shape newChild ) {
        getChildren().add( newChild );

        // Make sure that any changes to stroke width are inherited.
        newChild.strokeWidthProperty().bind( strokeWidthProperty().multiply( strokeScale ) );

        // NOTE: Centered stroke is default, but better safe than sorry, as
        // outside stroke can crash the application if shape is non-manifold.
        newChild.setStrokeType( StrokeType.CENTERED );
    }

    public final void clearShapes() {
        getChildren().clear();
    }

    /**
     * Reinterprets min and max as a {@link Rectangle2D boundary}.
     */
    @Override
    public final Rectangle2D getExplicitBounds() {
        final double width = _maxX - _minX;
        final double height = _maxY - _minY;
        final Rectangle2D explicitBounds = new Rectangle2D( _minX, _minY, width, height );

        return explicitBounds;
    }

    public final double getStrokeWidth() {
        return strokeWidth.get();
    }

    @Override
    public final boolean hasExplicitBounds() {
        final double width = _maxX - _minX;
        final double height = _maxY - _minY;

        final boolean limitsValid = ( width > 0.0d ) && ( height > 0.0d );

        return limitsValid;
    }

    public final void reset() {
        // Clear the added shapes to ensure they can be garbage collected.
        clearShapes();
    }

    /**
     * Sets the stroke on child {@link javafx.scene.shape.Shape shapes} to a
     * uniform scale in this node's frame of reference.
     *
     * @param pStrokeWidth
     *            The width of the stroke, roughly in pixels
     */
    public final void setStrokeWidth( final double pStrokeWidth ) {
        strokeWidth.set( pStrokeWidth );
    }

    /**
     * It is sometimes necessary to bind one stroke width with another.
     * 
     * @return the stroke width property
     */
    public final DoubleProperty strokeWidthProperty() {
        return strokeWidth;
    }

}// class DxfShapeContainer
