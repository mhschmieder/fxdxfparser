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
package com.mhschmieder.fxdxfparser.physics;

/**
 * The Distance Unit is the standard linear unit of measurement. It includes the
 * "Unitless" field because sometimes this is an interim value until units are
 * known, or units weren't specified but we need to track that as distinct from
 * units not initialized within the client code.
 */
public enum DxfDistanceUnit {
    UNITLESS,
    INCHES,
    FEET,
    MILES,
    MILLIMETERS,
    CENTIMETERS,
    METERS,
    KILOMETERS,
    MICROINCHES,
    MILS,
    YARDS,
    ANGSTROMS,
    NANOMETERS,
    MICRONS,
    DECIMETERS,
    DECAMETERS,
    HECTOMETERS,
    GIGAMETERS,
    ASTRONOMICAL_UNITS,
    LIGHT_YEARS,
    PARSECS;

    public static DxfDistanceUnit indexToDistanceUnit( final int dxfDistanceUnitIndex ) {
        DxfDistanceUnit dxfDistanceUnit = DxfDistanceUnit.UNITLESS;

        switch ( dxfDistanceUnitIndex ) {
        case 0:
            dxfDistanceUnit = DxfDistanceUnit.UNITLESS;
            break;
        case 1:
            dxfDistanceUnit = DxfDistanceUnit.INCHES;
            break;
        case 2:
            dxfDistanceUnit = DxfDistanceUnit.FEET;
            break;
        case 3:
            dxfDistanceUnit = DxfDistanceUnit.MILES;
            break;
        case 4:
            dxfDistanceUnit = DxfDistanceUnit.MILLIMETERS;
            break;
        case 5:
            dxfDistanceUnit = DxfDistanceUnit.CENTIMETERS;
            break;
        case 6:
            dxfDistanceUnit = DxfDistanceUnit.METERS;
            break;
        case 7:
            dxfDistanceUnit = DxfDistanceUnit.KILOMETERS;
            break;
        case 8:
            dxfDistanceUnit = DxfDistanceUnit.MICROINCHES;
            break;
        case 9:
            dxfDistanceUnit = DxfDistanceUnit.MILS;
            break;
        case 10:
            dxfDistanceUnit = DxfDistanceUnit.YARDS;
            break;
        case 11:
            dxfDistanceUnit = DxfDistanceUnit.ANGSTROMS;
            break;
        case 12:
            dxfDistanceUnit = DxfDistanceUnit.NANOMETERS;
            break;
        case 13:
            dxfDistanceUnit = DxfDistanceUnit.MICRONS;
            break;
        case 14:
            dxfDistanceUnit = DxfDistanceUnit.DECIMETERS;
            break;
        case 15:
            dxfDistanceUnit = DxfDistanceUnit.DECAMETERS;
            break;
        case 16:
            dxfDistanceUnit = DxfDistanceUnit.HECTOMETERS;
            break;
        case 17:
            dxfDistanceUnit = DxfDistanceUnit.GIGAMETERS;
            break;
        case 18:
            dxfDistanceUnit = DxfDistanceUnit.ASTRONOMICAL_UNITS;
            break;
        case 19:
            dxfDistanceUnit = DxfDistanceUnit.LIGHT_YEARS;
            break;
        case 20:
            dxfDistanceUnit = DxfDistanceUnit.PARSECS;
            break;
        default:
            // NOTE: The remaining cases are all scientific units.
            break;
        }

        return dxfDistanceUnit;
    }

}// enum DxfDistanceUnit
