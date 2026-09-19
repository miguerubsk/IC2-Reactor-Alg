package Simulator;

/**
 * Factory class to handle creating components by id or name.
 *
 * Ported from Ic2ExpReactorPlanner.ComponentFactory (data-driven design: generic
 * parameterized component classes instead of one Java class per item). The table
 * below preserves the same ids, order and names as the previous per-class table
 * in this repository, so genomes evolved under the old table remain meaningful.
 * See the class-level comment in each entry for any component dropped from, or
 * added relative to, the historical ~34-entry table.
 *
 * @author Brian McCloud (original design), ported for IC2-Reactor-Alg
 */
public class ComponentFactory {

    private ComponentFactory() {
        // do nothing, this class should not be instantiated.
    }

    // id 0 is reserved for "empty" (no component), matching the historical encoding used by
    // geneticAlg.codeHelper (2 hex chars per grid cell, 0x00 == empty).
    private static final ReactorComponent[] ITEMS = {
        null,
        new FuelRod(1, "fuelRodUranium", "Fuel Rod (Uranium)", TextureFactory.getImage("reactorUraniumSimple.png"), 20e3, 1, null, 100, 2, 1, false),
        new FuelRod(2, "dualFuelRodUranium", "Fuel Rod (Dual Uranium)", TextureFactory.getImage("reactorUraniumDual.png"), 20e3, 1, null, 200, 4, 2, false),
        new FuelRod(3, "quadFuelRodUranium", "Fuel Rod (Quad Uranium)", TextureFactory.getImage("reactorUraniumQuad.png"), 20e3, 1, null, 400, 8, 4, false),
        new FuelRod(4, "fuelRodMox", "Fuel Rod (MOX)", TextureFactory.getImage("reactorMOXSimple.png"), 10e3, 1, null, 100, 2, 1, true),
        new FuelRod(5, "dualFuelRodMox", "Fuel Rod (Dual MOX)", TextureFactory.getImage("reactorMOXDual.png"), 10e3, 1, null, 200, 4, 2, true),
        new FuelRod(6, "quadFuelRodMox", "Fuel Rod (Quad MOX)", TextureFactory.getImage("reactorMOXQuad.png"), 10e3, 1, null, 400, 8, 4, true),
        new Reflector(7, "neutronReflector", "Neutron Reflector", TextureFactory.getImage("reactorReflector.png"), 30e3, 1, null),
        new Reflector(8, "thickNeutronReflector", "Thick Neutron Reflector", TextureFactory.getImage("reactorReflectorThick.png"), 120e3, 1, null),
        new Vent(9, "heatVent", "Heat Vent", TextureFactory.getImage("reactorVent.png"), 1, 1000, null, 6, 0, 0),
        new Vent(10, "advancedHeatVent", "Advanced Heat Vent", TextureFactory.getImage("reactorVentDiamond.png"), 1, 1000, null, 12, 0, 0),
        new Vent(11, "reactorHeatVent", "Reactor Heat Vent", TextureFactory.getImage("reactorVentCore.png"), 1, 1000, null, 5, 5, 0),
        new Vent(12, "componentHeatVent", "Component Heat Vent", TextureFactory.getImage("reactorVentSpread.png"), 1, 1, null, 0, 0, 4),
        new Vent(13, "overclockedHeatVent", "Overclocked Heat Vent", TextureFactory.getImage("reactorVentGold.png"), 1, 1000, null, 20, 36, 0),
        new CoolantCell(14, "coolantCell10k", "10k Coolant Cell", TextureFactory.getImage("reactorCoolantSimple.png"), 1, 10e3, null),
        new CoolantCell(15, "coolantCell30k", "30k Coolant Cell", TextureFactory.getImage("reactorCoolantTriple.png"), 1, 30e3, null),
        new CoolantCell(16, "coolantCell60k", "60k Coolant Cell", TextureFactory.getImage("reactorCoolantSix.png"), 1, 60e3, null),
        new Exchanger(17, "heatExchanger", "Heat Exchanger", TextureFactory.getImage("reactorHeatSwitch.png"), 1, 2500, null, 12, 4),
        new Exchanger(18, "advancedHeatExchanger", "Advanced Heat Exchanger", TextureFactory.getImage("reactorHeatSwitchDiamond.png"), 1, 10e3, null, 24, 8),
        new Exchanger(19, "coreHeatExchanger", "Reactor Heat Exchanger", TextureFactory.getImage("reactorHeatSwitchCore.png"), 1, 5000, null, 0, 72),
        new Exchanger(20, "componentHeatExchanger", "Component Heat Exchanger", TextureFactory.getImage("reactorHeatSwitchSpread.png"), 1, 5000, null, 36, 0),
        new Plating(21, "reactorPlating", "Reactor Plating", TextureFactory.getImage("reactorPlating.png"), 1, 1, null, 1000, 0.9025),
        new Plating(22, "heatCapacityReactorPlating", "Heat-Capacity Reactor Plating", TextureFactory.getImage("reactorPlatingHeat.png"), 1, 1, null, 1700, 0.9801),
        new Plating(23, "containmentReactorPlating", "Containment Reactor Plating", TextureFactory.getImage("reactorPlatingExplosive.png"), 1, 1, null, 500, 0.81),
        new Condensator(24, "rshCondensator", "RSH-Condensator", TextureFactory.getImage("reactorCondensator.png"), 1, 20e3, null),
        new Condensator(25, "lzhCondensator", "LZH-Condensator", TextureFactory.getImage("reactorCondensatorLap.png"), 1, 100e3, null),
        new FuelRod(26, "fuelRodThorium", "Fuel Rod (Thorium)", TextureFactory.getImage("gt.Thoriumcell.png"), 50e3, 1, "GT5.08", 20, 0.5, 1, false),
        new FuelRod(27, "dualFuelRodThorium", "Fuel Rod (Dual Thorium)", TextureFactory.getImage("gt.Double_Thoriumcell.png"), 50e3, 1, "GT5.08", 40, 1, 2, false),
        new FuelRod(28, "quadFuelRodThorium", "Fuel Rod (Quad Thorium)", TextureFactory.getImage("gt.Quad_Thoriumcell.png"), 50e3, 1, "GT5.08", 80, 2, 4, false),
        new CoolantCell(29, "coolantCellHelium60k", "60k Helium Coolant Cell", TextureFactory.getImage("gt.60k_Helium_Coolantcell.png"), 1, 60e3, "GT5.08"),
        new CoolantCell(30, "coolantCellHelium180k", "180k Helium Coolant Cell", TextureFactory.getImage("gt.180k_Helium_Coolantcell.png"), 1, 180e3, "GT5.08"),
        new CoolantCell(31, "coolantCellHelium360k", "360k Helium Coolant Cell", TextureFactory.getImage("gt.360k_Helium_Coolantcell.png"), 1, 360e3, "GT5.08"),
        new CoolantCell(32, "coolantCellNak60k", "60k NaK Coolant Cell", TextureFactory.getImage("gt.60k_NaK_Coolantcell.png"), 1, 60e3, "GT5.08"),
        new CoolantCell(33, "coolantCellNak180k", "180k NaK Coolant Cell", TextureFactory.getImage("gt.180k_NaK_Coolantcell.png"), 1, 180e3, "GT5.08"),
        new CoolantCell(34, "coolantCellNak360k", "360k NaK Coolant Cell", TextureFactory.getImage("gt.360k_NaK_Coolantcell.png"), 1, 360e3, "GT5.08"),
        new Reflector(35, "iridiumNeutronReflector", "Iridium Neutron Reflector", TextureFactory.getImage("gt.neutronreflector.png"), 1, 1, null),
    };

    private static ReactorComponent copy(ReactorComponent source) {
        if (source != null) {
            Class<? extends ReactorComponent> aClass = source.getClass();
            if (aClass == Condensator.class) {
                return new Condensator((Condensator) source);
            } else if (aClass == CoolantCell.class) {
                return new CoolantCell((CoolantCell) source);
            } else if (aClass == Exchanger.class) {
                return new Exchanger((Exchanger) source);
            } else if (aClass == FuelRod.class) {
                return new FuelRod((FuelRod) source);
            } else if (aClass == Plating.class) {
                return new Plating((Plating) source);
            } else if (aClass == Reflector.class) {
                return new Reflector((Reflector) source);
            } else if (aClass == Vent.class) {
                return new Vent((Vent) source);
            }
        }
        return null;
    }

    /**
     * Gets a default instances of the specified component (such as for drawing button images)
     * @param id the id of the component.
     * @return the component with the specified id, or null if the id is out of range.
     */
    public static ReactorComponent getDefaultComponent(int id) {
        if (id >= 0 && id < ITEMS.length) {
            return ITEMS[id];
        }
        return null;
    }

    /**
     * Gets a default instances of the specified component (such as for drawing button images)
     * @param name the name of the component.
     * @return the component with the specified name, or null if the name is not found.
     */
    public static ReactorComponent getDefaultComponent(String name) {
        if (name != null) {
            for (ReactorComponent item : ITEMS) {
                if (item != null && item.baseName.equals(name)) {
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * Creates a new instance of the specified component.
     * @param id the id of the component to create.
     * @return a new instance of the specified component, or null if the id is out of range.
     */
    public static ReactorComponent createComponent(int id) {
        if (id >= 0 && id < ITEMS.length) {
            return copy(ITEMS[id]);
        }
        return null;
    }

    /**
     * Creates a new instance of the specified component.
     * @param name the name of the component to create.
     * @return a new instance of the specified component, or null if the name is not found.
     */
    public static ReactorComponent createComponent(String name) {
        return copy(getDefaultComponent(name));
    }

    /**
     * Gets the id of the component.
     * @param component the component to identify.
     * @return the id of the passed component, 0 if the component is null.
     */
    public static int getID(ReactorComponent component) {
        if (component != null) {
            return component.id;
        }
        return 0;
    }

    /**
     * Gets the (base, non-localized) name of the component.
     * @param component the component to identify.
     * @return the name of the passed component, "empty" for a null component.
     */
    public static String getName(ReactorComponent component) {
        if (component != null) {
            return component.baseName;
        }
        return "empty";
    }

    public static String getDisplayName(ReactorComponent component) {
        if (component != null) {
            return component.name;
        }
        return null;
    }

    /**
     * Get the number of defined components.
     * @return the number of defined components.
     */
    public static int getComponentCount() {
        return ITEMS.length;
    }
}
