/*
 * Copyright (C) 2024 Miguel González García
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.miguerubsk.ic2reactoralg.simulator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

/** Regression tests for behavior lost when the simulator was ported to generic components. */
class ComponentBehaviorTest {

    private static final int URANIUM_ROD = 1;
    private static final int MOX_ROD = 4;
    private static final int NEUTRON_REFLECTOR = 7;
    private static final int COMPONENT_HEAT_VENT = 12;
    private static final int RSH_CONDENSATOR = 24;

    private static ReactorComponent place(Reactor reactor, int row, int col, int id) {
        ReactorComponent component = ComponentFactory.createComponent(id);
        reactor.setComponentAt(row, col, component);
        component.clearCurrentHeat();
        component.clearDamage();
        return component;
    }

    @Test
    void componentHeatVentDoesNotAcceptHeatFromFuelRods() {
        Reactor reactor = new Reactor();
        ReactorComponent rod = place(reactor, 2, 4, URANIUM_ROD);
        ReactorComponent vent = place(reactor, 2, 5, COMPONENT_HEAT_VENT);

        rod.generateHeat();

        assertFalse(vent.isHeatAcceptor());
        assertFalse(vent.isBroken());
        assertEquals(0.0, vent.getCurrentHeat());
    }

    @Test
    void reflectorOnlyWearsDownWhileTheAdjacentRodIsWorking() {
        Reactor reactor = new Reactor();
        ReactorComponent rod = place(reactor, 2, 4, MOX_ROD);
        ReactorComponent reflector = place(reactor, 2, 5, NEUTRON_REFLECTOR);

        reflector.generateHeat();
        assertEquals(1.0, reflector.getCurrentDamage());

        rod.applyDamage(rod.getMaxDamage()); // the rod is now broken
        reflector.generateHeat();
        assertEquals(1.0, reflector.getCurrentDamage());
    }

    @Test
    void condensatorNeverAcceptsMoreThanItsCapacity() {
        Reactor reactor = new Reactor();
        ReactorComponent condensator = place(reactor, 0, 0, RSH_CONDENSATOR);

        condensator.adjustCurrentHeat(19_000);
        double rejected = condensator.adjustCurrentHeat(2_000);

        assertEquals(20_000, condensator.getCurrentHeat());
        assertEquals(1_000, rejected);
    }
}
