package Simulator;

/**
 * Represents some containment reactor plating.
 * @author Brian McCloud
 */
public class ContainmentReactorPlating extends ReactorComponent {
    
    /**
     * The filename for the image to show for the component.
     */
    private static final String IMAGEFILENAME = "reactorPlatingExplosive.png";    
    
    public static final MaterialsList MATERIALS = new MaterialsList(ReactorPlating.MATERIALS, 2, "Advanced Alloy");
    
    /**
     * Creates a new instance.
     */
    public ContainmentReactorPlating() {
        setImage(TextureFactory.getImage(IMAGEFILENAME));
    }
    
    /**
     * Gets the name of the component.
     * @return the name of this component.
     */
    @Override
    public String toString() {
        return "Containment Reactor Plating";
    }
    
    @Override
    public void addToReactor() {
        getParent().adjustMaxHeat(500);
    }

    @Override
    public void removeFromReactor() {
        getParent().adjustMaxHeat(-500);
    }
    
    @Override
    public MaterialsList getMaterials() {
        return MATERIALS;
    }
    
}
