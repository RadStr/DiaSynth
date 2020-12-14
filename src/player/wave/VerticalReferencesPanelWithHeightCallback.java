package player.wave;

import java.awt.*;

public class VerticalReferencesPanelWithHeightCallback extends VerticalReferencesPanel {
    public VerticalReferencesPanelWithHeightCallback(double minValue, double maxValue, HeightGetter heightGetter) {
        super(minValue, maxValue);
        this.heightGetter = heightGetter;
    }


    private HeightGetter heightGetter;
    private Dimension prefSize = new Dimension();

    @Override
    public Dimension getPreferredSize() {
        prefSize.width = super.getPreferredSize().width;
        prefSize.height = heightGetter.getHeight();
        return prefSize;
    }

    public int getPreferredWidth() {
        return super.getPreferredSize().width;
    }

    public static interface HeightGetter {
        int getHeight();
    }
}
