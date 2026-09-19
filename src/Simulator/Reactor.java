package Simulator;

import javax.swing.JOptionPane;

/**
 * Represents an IndustrialCraft2 Nuclear Reactor.
 *
 * Ported to use the data-driven (generic parameterized) component classes from
 * the upstream Ic2ExpReactorPlanner project, while preserving the historical
 * external API and code encoding (2 hex chars per grid cell, optional "(hNN)"
 * initial-heat suffix) that geneticAlg.codeHelper / geneticAlg.ReactorEntity depend on.
 *
 * @author Brian McCloud (original), ported for IC2-Reactor-Alg
 */
public class Reactor {

    private final ReactorComponent[][] grid = new ReactorComponent[6][9];

    private double currentEUoutput = 0.0;

    private double currentHeat = 0.0;

    private double maxHeat = 10000.0;

    private double ventedHeat = 0.0;

    private boolean fluid = false;

    public ReactorComponent getComponentAt(int row, int column) {
        if (row >= 0 && row < grid.length && column >= 0 && column < grid[row].length) {
            return grid[row][column];
        }
        return null;
    }

    public void setComponentAt(int row, int column, ReactorComponent component) {
        if (row >= 0 && row < grid.length && column >= 0 && column < grid[row].length) {
            if (grid[row][column] != null) {
                grid[row][column].removeFromReactor();
            }
            grid[row][column] = component;
            if (component != null) {
                component.setRow(row);
                component.setColumn(column);
                component.setParent(this);
                component.addToReactor();
            }
        }
    }

    public void clearGrid() {
        for (ReactorComponent[] gridRow : grid) {
            for (int col = 0; col < gridRow.length; col++) {
                if (gridRow[col] != null) {
                    gridRow[col].removeFromReactor();
                }
                gridRow[col] = null;
            }
        }
    }

    /**
     * @return the amount of EU output in the reactor tick just simulated.
     */
    public double getCurrentEUoutput() {
        return currentEUoutput;
    }

    /**
     * @return the current heat level of the reactor.
     */
    public double getCurrentHeat() {
        return currentHeat;
    }

    /**
     * @return the maximum heat of the reactor.
     */
    public double getMaxHeat() {
        return maxHeat;
    }

    /**
     * Adjust the maximum heat
     * @param adjustment the adjustment amount (negative values decrease the max heat).
     */
    public void adjustMaxHeat(double adjustment) {
        maxHeat += adjustment;
    }

    /**
     * Set the current heat of the reactor.  Mainly to be used for simulating a pre-heated reactor, or for resetting to 0 for a new simulation.
     * @param currentHeat the heat to set
     */
    public void setCurrentHeat(double currentHeat) {
        this.currentHeat = currentHeat;
    }

    /**
     * Adjusts the reactor's current heat by a specified amount
     * @param adjustment the adjustment amount.
     */
    public void adjustCurrentHeat(double adjustment) {
        currentHeat += adjustment;
        if (currentHeat < 0.0) {
            currentHeat = 0.0;
        }
    }

    /**
     * add some EU output.
     * @param amount the amount of EU to output over 1 reactor tick (20 game ticks).
     */
    public void addEUOutput(double amount) {
        currentEUoutput += amount;
    }

    /**
     * clears the EU output (presumably to start simulating a new reactor tick).
     */
    public void clearEUOutput() {
        currentEUoutput = 0.0;
    }

    /**
     * Gets a list of the materials needed to build the components.
     * @return a list of the materials needed to build the components.
     */
    public MaterialsList getMaterials() {
        MaterialsList result = new MaterialsList();
        for (int col = 0; col < grid[0].length; col++) {
            for (int row = 0; row < grid.length; row++) {
                if (grid[row][col] != null) {
                    result.add(grid[row][col].getMaterials());
                }
            }
        }
        return result;
    }

    /**
     * @return the amount of heat vented this reactor tick.
     */
    public double getVentedHeat() {
        return ventedHeat;
    }

    /**
     * Adds to the amount of heat vented this reactor tick, in case it is a new-style reactor with a pressure vessel and outputting heat to fluid instead of EU.
     * @param amount the amount to add.
     */
    public void ventHeat(double amount) {
        ventedHeat += amount;
    }

    /**
     * Clears the amount of vented heat, in case a new reactor tick is starting.
     */
    public void clearVentedHeat() {
        ventedHeat = 0;
    }

    /**
     * Get a code that represents the component set, which can be passed between forum users, etc.
     * @return a code representing some ids for the components and arrangement.  Passing the same code to setCode() should re-create an identical reactor setup, even if other changes have happened in the meantime.
     */
    public String getCode() {
        StringBuilder result = new StringBuilder(108);
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                final ReactorComponent component = grid[row][col];
                final int id = ComponentFactory.getID(component);
                result.append(String.format("%02X", id));
                if (component != null && component.getInitialHeat() > 0) {
                    result.append(String.format("(h%s)", Integer.toString((int) component.getInitialHeat(), 36)));
                }
            }
        }
        return result.toString();
    }

    /**
     * Sets a code to configure the entire grid all at once.  Expects the code to have originally been output by getCode().
     * @param code the code of the reactor setup to use.
     */
    public void setCode(final String code) {
        int pos = 0;
        int[][] ids = new int[grid.length][grid[0].length];
        char[][] paramTypes = new char[grid.length][grid[0].length];
        int[][] params = new int[grid.length][grid[0].length];
        if (code.length() >= 108 && code.matches("[0-9A-Za-z()]+")) {
            try {
                for (int row = 0; row < grid.length; row++) {
                    for (int col = 0; col < grid[row].length; col++) {
                        ids[row][col] = Integer.parseInt(code.substring(pos, pos + 2), 16);
                        pos += 2;
                        if (pos < code.length() && code.charAt(pos) == '(') {
                            paramTypes[row][col] = code.charAt(pos + 1);
                            int tempPos = pos + 2;
                            StringBuilder param = new StringBuilder(10);
                            while (code.charAt(tempPos) != ')') {
                                param.append(code.charAt(tempPos));
                                tempPos++;
                            }
                            params[row][col] = Integer.parseInt(param.toString(), 36);
                            pos = tempPos + 1;
                        }
                    }
                }
                for (int row = 0; row < grid.length; row++) {
                    for (int col = 0; col < grid[row].length; col++) {
                        final ReactorComponent component = ComponentFactory.createComponent(ids[row][col]);
                        if (paramTypes[row][col] == 'h') {
                            component.setInitialHeat(params[row][col]);
                        }
                        setComponentAt(row, col, component);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        } else {
            String tempCode = code;
            if (code.startsWith("http://www.talonfiremage.pwp.blueyonder.co.uk/v3/reactorplanner.html?")) {
                tempCode = code.replace("http://www.talonfiremage.pwp.blueyonder.co.uk/v3/reactorplanner.html?", "");
            }
            if (tempCode.matches("[0-9a-z]+")) {
                StringBuilder warnings = new StringBuilder(500);
                // Possibly a code from Talonius's old planner
                TaloniusDecoder decoder = new TaloniusDecoder(tempCode);
                // initial heat, ignored by new planner.
                decoder.readInt(10);
                // reactor grid
                for (int x = 8; x >= 0; x--) {
                    for (int y = 5; y >= 0; y--) {
                        int nextValue = decoder.readInt(7);

                        // items are no longer stackable in IC2 reactors, but stack sizes from the planner code still need to be handled
                        if (nextValue > 64) {
                            nextValue = decoder.readInt(7);
                        }

                        switch (nextValue) {
                            case 0:
                                setComponentAt(y, x, null);
                                break;
                            case 1:
                                setComponentAt(y, x, ComponentFactory.createComponent("fuelRodUranium"));
                                break;
                            case 2:
                                setComponentAt(y, x, ComponentFactory.createComponent("dualFuelRodUranium"));
                                break;
                            case 3:
                                setComponentAt(y, x, ComponentFactory.createComponent("quadFuelRodUranium"));
                                break;
                            case 4:
                                warnings.append(String.format("Obsolete component (depleted isotope cell) at row %d column %d removed.\n", y, x));
                                break;
                            case 5:
                                setComponentAt(y, x, ComponentFactory.createComponent("neutronReflector"));
                                break;
                            case 6:
                                setComponentAt(y, x, ComponentFactory.createComponent("thickNeutronReflector"));
                                break;
                            case 7:
                                setComponentAt(y, x, ComponentFactory.createComponent("heatVent"));
                                break;
                            case 8:
                                setComponentAt(y, x, ComponentFactory.createComponent("reactorHeatVent"));
                                break;
                            case 9:
                                setComponentAt(y, x, ComponentFactory.createComponent("overclockedHeatVent"));
                                break;
                            case 10:
                                setComponentAt(y, x, ComponentFactory.createComponent("advancedHeatVent"));
                                break;
                            case 11:
                                setComponentAt(y, x, ComponentFactory.createComponent("componentHeatVent"));
                                break;
                            case 12:
                                setComponentAt(y, x, ComponentFactory.createComponent("rshCondensator"));
                                break;
                            case 13:
                                setComponentAt(y, x, ComponentFactory.createComponent("lzhCondensator"));
                                break;
                            case 14:
                                setComponentAt(y, x, ComponentFactory.createComponent("heatExchanger"));
                                break;
                            case 15:
                                setComponentAt(y, x, ComponentFactory.createComponent("coreHeatExchanger"));
                                break;
                            case 16:
                                setComponentAt(y, x, ComponentFactory.createComponent("componentHeatExchanger"));
                                break;
                            case 17:
                                setComponentAt(y, x, ComponentFactory.createComponent("advancedHeatExchanger"));
                                break;
                            case 18:
                                setComponentAt(y, x, ComponentFactory.createComponent("reactorPlating"));
                                break;
                            case 19:
                                setComponentAt(y, x, ComponentFactory.createComponent("heatCapacityReactorPlating"));
                                break;
                            case 20:
                                setComponentAt(y, x, ComponentFactory.createComponent("containmentReactorPlating"));
                                break;
                            case 21:
                                setComponentAt(y, x, ComponentFactory.createComponent("coolantCell10k"));
                                break;
                            case 22:
                                setComponentAt(y, x, ComponentFactory.createComponent("coolantCell30k"));
                                break;
                            case 23:
                                setComponentAt(y, x, ComponentFactory.createComponent("coolantCell60k"));
                                break;
                            case 24:
                                warnings.append(String.format("Obsolete component (heating cell) at row %d column %d removed.\n", y, x));
                                break;
                            case 32:
                                setComponentAt(y, x, ComponentFactory.createComponent("fuelRodThorium"));
                                break;
                            case 33:
                                setComponentAt(y, x, ComponentFactory.createComponent("dualFuelRodThorium"));
                                break;
                            case 34:
                                setComponentAt(y, x, ComponentFactory.createComponent("quadFuelRodThorium"));
                                break;
                            case 35:
                                warnings.append(String.format("Obsolete component (plutonium cell) at row %d column %d removed.\n", y, x));
                                break;
                            case 36:
                                warnings.append(String.format("Obsolete component (dual plutonium cell) at row %d column %d removed.\n", y, x));
                                break;
                            case 37:
                                warnings.append(String.format("Obsolete component (quad plutonium cell) at row %d column %d removed.\n", y, x));
                                break;
                            case 38:
                                setComponentAt(y, x, ComponentFactory.createComponent("iridiumNeutronReflector"));
                                break;
                            case 39:
                                setComponentAt(y, x, ComponentFactory.createComponent("coolantCellHelium60k"));
                                break;
                            case 40:
                                setComponentAt(y, x, ComponentFactory.createComponent("coolantCellHelium180k"));
                                break;
                            case 41:
                                setComponentAt(y, x, ComponentFactory.createComponent("coolantCellHelium360k"));
                                break;
                            case 42:
                                setComponentAt(y, x, ComponentFactory.createComponent("coolantCellNak60k"));
                                break;
                            case 43:
                                setComponentAt(y, x, ComponentFactory.createComponent("coolantCellNak180k"));
                                break;
                            case 44:
                                setComponentAt(y, x, ComponentFactory.createComponent("coolantCellNak360k"));
                                break;
                            default:
                                warnings.append(String.format("Unrecognized component (id %d) at row %d column %d removed.\n", nextValue, y, x));
                                break;
                        }
                    }
                }
                if (warnings.length() > 0) {
                    warnings.setLength(warnings.length() - 1);  // to remove last newline character
                    JOptionPane.showMessageDialog(null, warnings, "Warning(s)", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }

    /**
     * Checks whether the reactor is to simulate a fluid-style reactor, rather than a direct EU-output reactor.
     * @return true if this was set to be a fluid-style reactor, false if this was set to be direct EU-output reactor.
     */
    public boolean isFluid() {
        return fluid;
    }

    /**
     * Sets whether the reactor is to simulate a fluid-style reactor, rather than a direct EU-output reactor.
     * @param fluid true if this is to be a fluid-style reactor, false if this is to be direct EU-output reactor.
     */
    public void setFluid(boolean fluid) {
        this.fluid = fluid;
    }

}
