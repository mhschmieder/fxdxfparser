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
package com.mhschmieder.fxdxftoolkit.reader;

import java.io.BufferedReader;

import com.mhschmieder.fxdxftoolkit.geometry.FxShapeContainer;
import com.mhschmieder.fxdxftoolkit.loader.DxfBlock;
import com.mhschmieder.fxdxftoolkit.physics.DxfDistanceUnit;
import com.mhschmieder.fxdxftoolkit.structure.DxfDocument;
import com.mhschmieder.fxdxftoolkit.structure.DxfStatus;

import javafx.scene.transform.Affine;

/**
 * DxfLoader imports an AutoCAD DXF file into JavaFX as 2D Scene Graph Nodes.
 */
public class DxfLoader {

    private String      _currentBlock;
    private DxfDocument _dxfDoc;

    public DxfLoader() {
        _currentBlock = DxfDocument.MODEL_BLOCK;
    }

    /** Clear the document and nullify its reference, to free up resources. */
    public final void clearDocument() {
        if ( _dxfDoc != null ) {
            _dxfDoc.clearDocument();
            _dxfDoc = null;
        }
    }

    /**
     * Convert the loaded document's Model Space Block to a flat 2D slice at
     * z=0, using JavaFX Shapes as the target, hosted by a geometry container
     * that holds the generic JavaFX Shape conversions.
     *
     * @param fxShapeContainer
     *            The Geometry Container for the generic converted Shapes, as
     *            JavaFX Geometry Nodes
     */
    public final void convertToFxShapes( final FxShapeContainer fxShapeContainer ) {
        // Vectorize the entire Model Space block into generic shapes.
        final DxfBlock dxfBlock = _dxfDoc.getBlock( _currentBlock );
        final Affine defaultAffine = new Affine();
        final double defaultStrokeScale = 1.0d;
        dxfBlock.convertToFxShapes( fxShapeContainer, defaultAffine, defaultStrokeScale );

        // Clear the now-redundant and unneeded Model Space Block.
        dxfBlock.clearBlock();

        // Also clear the now-redundant DXF Document container.
        clearDocument();
    }

    /**
     * Obtiene el nombre del bloque que se añadirá a la escena.
     *
     * @return nombre del bloque que se añadirá a la escena
     */
    public final String getCurrentBlock() {
        return _currentBlock;
    }

    public final DxfDistanceUnit getDistanceUnit() {
        return isDocumentValid() ? _dxfDoc.getDistanceUnit() : DxfDistanceUnit.UNITLESS;
    }

    /**
     * Obtiene el objeto DxfDocument que representa el archivo DXF
     *
     * @return objeto DxfDocument
     */
    public final DxfDocument getDocument() {
        return _dxfDoc;
    }

    public final DxfStatus getDxfStatus() {
        return isDocumentValid() ? _dxfDoc.getDxfStatus() : null;
    }

    public final double getLimitsMaxX() {
        return isDocumentValid() ? _dxfDoc.getLimitsMaxX() : 0.0d;
    }

    public final double getLimitsMaxY() {
        return isDocumentValid() ? _dxfDoc.getLimitsMaxY() : 0.0d;
    }

    public final double getLimitsMinX() {
        return isDocumentValid() ? _dxfDoc.getLimitsMinX() : 0.0d;
    }

    public final double getLimitsMinY() {
        return isDocumentValid() ? _dxfDoc.getLimitsMinY() : 0.0d;
    }

    // Invalidate (i.e. nullify) the document reference so it isn't accidentally
    // accessed for additional operations once its resources are freed (this
    // also serves as a flag that a new document hasn't been loaded yet).
    public final void invalidateDocument() {
        _dxfDoc = null;
    }

    public final boolean isDocumentValid() {
        return _dxfDoc != null;
    }

    /**
     * Carga el archivo DXF. Una vez invocado este método, se pueden acceder al
     * resto de métodos de esta clase, como getDocument.
     *
     * @param bufferedReader Buffered Reader to store the loaded DXF document
     * @param ignorePaperSpace {@code true} if Paper Space block should be ignored
     * @param logDxfStatus {@code true} if the status of DXF load should be logged
     * @throws DxfReaderException if Out of Memory occurs during parsing
     */
    public final void loadDocument( final BufferedReader bufferedReader,
                                    final boolean ignorePaperSpace,
                                    final boolean logDxfStatus )
            throws DxfReaderException {
        final DxfParser parser = new DxfParser( ignorePaperSpace, logDxfStatus );
        final DxfReader reader = new DxfReader( bufferedReader, parser );

        try {
            reader.runReader();
        }
        catch ( final OutOfMemoryError | Exception e ) {
            // NOTE: We now use this as a catch-all, even if Out of Memory, as
            // it improves the ability of downstream clients to recover and also
            // to release unused memory as references go away much quicker.
            throw new DxfReaderException( "Error in DXF file: " //$NON-NLS-1$
                    + e.getLocalizedMessage() );
        }

        _dxfDoc = parser.getDocument();
    }

    /**
     * Establece el bloque que se añadirá a la escena.
     *
     * @param block
     *            nombre del bloque
     * @see DxfDocument#getBlockNames
     */
    public final void setCurrentBlock( final String block ) {
        if ( block == null ) {
            throw new IllegalArgumentException();
        }

        _currentBlock = block;
    }

}// class DxfLoader
