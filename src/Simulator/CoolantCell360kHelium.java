package Simulator;

/**
 * Represents a 360k Helium Coolant Cell.
 * @author Brian McCloud
 */
public class CoolantCell360kHelium extends ReactorComponent {
    
    /**
     * The filename for the image to show for the component.
     */
    private static final String IMAGEFILENAME = "gt.360k_Helium_Coolantcell.png";    
    
    public static final MaterialsList MATERIALS = new MaterialsList(2, CoolantCell180kHelium.MATERIALS, 6, "Tin", 9, "Copper");
    
    /**
     * Creates a new instance.
     */
    public CoolantCell360kHelium() {
        setImage(TextureFactory.getImage(IMAGEFILENAME));
        setMaxHeat(360000);
        automationThreshold = 350000;
    }
    
    /**
     * Gets the name of the component.
     * @return the name of this component.
     */
    @Override
    public String toString() {
        String result = "360k He Coolant Cell";
        if (getInitialHeat() > 0) {
            result += String.format(" (initial heat: %,d)", (int)getInitialHeat());
        }
        return result;
    }

    @Override
    public boolean isHeatAcceptor() {
        return !isBroken();
    }
    
    @Override
    public MaterialsList getMaterials() {
        return MATERIALS;
    }
    
    @Override
    public double adjustCurrentHeat(double heat) {
        currentCellCooling += heat;
        bestCellCooling = Math.max(currentCellCooling, bestCellCooling);
        return super.adjustCurrentHeat(heat);
    }
    
}
