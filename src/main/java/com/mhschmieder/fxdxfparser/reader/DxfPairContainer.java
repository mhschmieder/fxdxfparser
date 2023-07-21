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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class DxfPairContainer {

    private class DxfPairContainerIterator implements Iterator< String > {
        private final int         _keyRef;
        private String            _valueRef;
        final Iterator< DxfPair > _iter;

        DxfPairContainerIterator( final int pKeyref ) {
            _keyRef = pKeyref;
            _valueRef = null;
            _iter = iterator();
        }

        @Override
        public void forEachRemaining( final Consumer< ? super String > action ) {
            // TODO: Implement this method in case it gets called internally
            // by the JRE via class override. Not likely as it's in Streams API.
        }

        @Override
        public boolean hasNext() {
            if ( _valueRef != null ) {
                return true;
            }

            while ( _iter.hasNext() ) {
                final DxfPair pair = _iter.next();
                if ( _keyRef == pair.getKey() ) {
                    _valueRef = pair.getValue();
                    return true;
                }
            }

            return false;
        }

        @Override
        public String next() throws NoSuchElementException {
            if ( _valueRef == null ) {
                if ( !hasNext() ) {
                    throw new NoSuchElementException();
                }
            }

            final String temp = _valueRef;
            _valueRef = null;
            return temp;
        }

        @Override
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }
    }

    private static String getValueAux( final int pKey,
                                       final String pDefault,
                                       final Iterator< DxfPair > pairIterator ) {
        while ( pairIterator.hasNext() ) {
            final DxfPair pair = pairIterator.next();
            if ( pKey == pair.getKey() ) {
                return pair.getValue();
            }
        }

        return pDefault;
    }

    private final List< DxfPair > _pairs;

    private final int             _subclassMarker = 100;

    public DxfPairContainer() {
        _pairs = new ArrayList<>( 1000 );
    }

    public void add( final int pKey, final String pVal ) {
        _pairs.add( new DxfPair( pKey, pVal ) );
    }

    public void clear() {
        _pairs.clear();
    }

    public List< DxfPair > getPairs() {
        return _pairs;
    }

    public DxfPairContainer getSubclassPairs( final String pSubclass ) {
        boolean pesca = false;
        DxfPairContainer pairContainer = null;

        for ( final DxfPair pair : _pairs ) {
            if ( pesca ) {
                if ( _subclassMarker == pair.getKey() ) {
                    break; // Nuevo classMarker, se acabó el nuestro
                }
                else if ( pairContainer != null ) {
                    pairContainer.add( pair.getKey(), pair.getValue() );
                }
            }
            else {
                if ( ( _subclassMarker == pair.getKey() )
                        && ( pSubclass.compareTo( pair.getValue() ) == 0 ) ) {
                    pairContainer = new DxfPairContainer();
                    pesca = true;
                }
            }
        }

        return pairContainer;
    }

    // Sólo para DXF CODES (keys) fijos (no opcionales)
    public String getSubclassValue( final int pKey, final String pSubclass ) {
        final Iterator< DxfPair > it = iterator();
        while ( it.hasNext() ) {
            final DxfPair pair = it.next();
            if ( ( _subclassMarker == pair.getKey() )
                    && pSubclass.equalsIgnoreCase( pair.getValue() ) ) {
                return getValueAux( pKey, null, it );
            }
        }
        return null;
    }

    /**
     * null default return value
     *
     * @param pKey The key to use for the pair's value
     * @return The value associated with the supplied key
     * @see #getValueAux(int, String, Iterator)
     */
    public String getValue( final int pKey ) {
        return getValueAux( pKey, null, iterator() );
    }

    public String getValue( final int pKey, final String pDefault ) {
        return getValueAux( pKey, pDefault, iterator() );
    }

    public Iterator< DxfPair > iterator() {
        return _pairs.iterator();
    }

    /**
     * This method finds the first occurrence of a specific DXF Group Code,
     * and returns an Iterator that is ready to start looping over a section of
     * the Entity that has repeated group codes, such as vertex coordinate sets.
     *
     * @param pKey
     *            The DXF Group Code that serves as the key to look up in the
     *            overall pair structure
     * @return An Iterator that is poised to start at the first instance of the
     *         required key (DXF Group Code)
     */
    public Iterator< DxfPair > iterator( final int pKey ) {
        final ListIterator< DxfPair > it = _pairs.listIterator();
        final String s = getValueAux( pKey, null, it );
        if ( s != null ) {
            it.previous();
            return it;
        }

        return null;
    }

    /**
     * This method finds the first occurrence of a specific DXF Group Code,
     * and returns an Iterator that is ready to start looping over a section of
     * the Entity that has repeated group codes, such as vertex coordinate sets.
     *
     * @param pKey
     *            The DXF Group Code that serves as the key to look up in the
     *            overall pair structure
     * @return An Iterator that is poised to start at the first instance of the
     *         required key (DXF Group Code)
     */
    public Iterator< String > iteratorForValue( final int pKey ) {
        return new DxfPairContainerIterator( pKey );
    }

    public int size() {
        return _pairs.size();
    }

    @SuppressWarnings("nls")
    @Override
    public String toString() {
        final StringBuilder valret = new StringBuilder( "PAIRS: \n" );
        for ( final DxfPair pair : _pairs ) {
            valret.append( pair.getKey() );
            valret.append( " / " );
            valret.append( pair.getValue() );
            valret.append( "\n" );
        }

        return valret.toString();
    }

}// class DxfPairContainer
