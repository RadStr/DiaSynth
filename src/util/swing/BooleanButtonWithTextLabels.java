package util.swing;

public class BooleanButtonWithTextLabels extends BooleanButton {
    private String textIfTrue;
    private String textIfFalse;

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     */
    public BooleanButtonWithTextLabels(boolean bool, String textIfTrue, String textIfFalse) {
        this.boolVar = bool;
        this.textIfFalse = textIfFalse;
        this.textIfTrue = textIfTrue;
        setButtonVisuals();
    }

    @Override
    protected void setButtonVisuals() {
        if(boolVar) {
            this.setText(textIfTrue);
        }
        else {
            this.setText(textIfFalse);
        }
    }
}
