package Simulator;

/**
 * Represents a 60k Coolant Cell.
 * @author Brian McCloud
 */
public class CoolantCell60k extends ReactorComponent {
    
    /**
     * The filename for the image to show for the component.
     */
    private static final String IMAGEFILENAME = "reactorCoolantSix.png";    
    
    public static final MaterialsList MATERIALS = new MaterialsList(2, CoolantCell30k.MATERIALS, "Iron", 6, "Tin");
    
    /**
     * Creates a new instance.
     */
    public CoolantCell60k() {
        setImage(TextureFactory.getImage(IMAGEFILENAME));
        setMaxHeat(60000);
        automationThreshold = 54000;
    }
    
    /**
     * Gets the name of the component.
     * @return the name of this component.
     */
    @Override
    public String toString() {
        String result = "60k Coolant Cell";
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
