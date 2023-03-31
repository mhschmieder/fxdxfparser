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

/**
 * This is an encapsulation of checked exceptions thrown by this library, for
 * purposes of clearly identifying library calls as the underlying cause.
 *
 * @version 0.1
 *
 * @author David Tejada Francia
 */
public class DxfReaderException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = -7174434948783185369L;

    /**
     * Fully qualified constructor for a library encapsulation of exceptions
     * related to DXF handling. Generally these will be recaptures of Core Java
     * exceptions, wrapped in a library class to better mark the cause or fault.
     *
     * @param message
     *            The full pre-parsed string to include with the exception
     *
     * @since 1.0
     */
    public DxfReaderException( final String message ) {
        super( message );
    }

}// class DxfReaderException
