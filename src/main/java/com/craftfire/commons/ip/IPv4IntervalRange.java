/*
 * This file is part of CraftCommons.
 *
 * Copyright (c) 2011-2012, CraftFire <http://www.craftfire.com/>
 * CraftCommons is licensed under the GNU Lesser General Public License.
 *
 * CraftCommons is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CraftCommons is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.craftfire.commons.ip;

import com.craftfire.commons.classes.VersionRange;

public class IPv4IntervalRange implements IPRange {
    private final IPv4Address min, max;

    public IPv4IntervalRange(IPv4Address min, IPv4Address max) {
        this.min = min;
        this.max = max;
    }

    public IPv4Address getMin() {
        return this.min;
    }

    public IPv4Address getMax() {
        return this.max;
    }

    @Override
    public boolean isInRange(IPAddress address) {
        // TODO: Check if address is IPv4 and decide what to do if it's not.
        return new VersionRange(this.min.toString(), this.max.toString()).inVersionRange(address.toIPv4().toString());
    }

    @Override
    public String toString() {
        return "[" + getMin() + " - " + getMax() + "]";
    }
}
