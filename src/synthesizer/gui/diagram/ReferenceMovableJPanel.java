package synthesizer.gui.diagram;

import java.awt.*;

public class ReferenceMovableJPanel extends MovableJPanelBase {
    public ReferenceMovableJPanel(int absoluteX, int absoluteY, int w, int h, DiagramPanel diagramPanel) {
        super(absoluteX, absoluteY, w, h, diagramPanel);
        location = new IntPairWithInternalDoublesWithoutMinAndMax(absoluteX, absoluteY);
    }

    public ReferenceMovableJPanel(int absoluteX, int absoluteY, DiagramPanel diagramPanel) {
        super(absoluteX, absoluteY, diagramPanel);
        location = new IntPairWithInternalDoublesWithoutMinAndMax(absoluteX, absoluteY);
    }

    private IntPairWithInternalDoublesWithoutMinAndMax location;


    @Override
    public void setLocation(Point point) {
        super.setLocation(point);
        location.set(point.x, point.y);
    }
    @Override
    public void setLocation(int x, int y) {
        super.setLocation(x, y);
        location.set(x, y);
    }

    @Override
    public void updateLocation(int updateXVal, int updateYVal) {
        location.add(updateXVal, updateYVal);
        super.setLocation(location.getFirst(), location.getSecond());
    }

    @Override
    public void updateX(int update) {
        location.addFirst(update);
        super.setLocation(location.getFirst(), location.getSecond());
    }

    @Override
    public void updateY(int update) {
        location.addSecond(update);
        super.setLocation(location.getFirst(), location.getSecond());
    }

    public void updateLocation(Dimension oldVisibleSize, IntPairWithInternalDoublesWithMinAndMax newVisibleSize, Point screenMidPoint) {
        int newWidth = newVisibleSize.getFirst();
        updateLocationBasedOnMidPoint(location, screenMidPoint, oldVisibleSize.width, newWidth);
        super.setLocation(location.getFirst(), location.getSecond());
    }

    private void updateLocationBasedOnMidPoint(IntPairWithInternalDoublesWithoutMinAndMax locationToUpdate, Point screenMidPoint, int oldWidth, int newWidth) {
        double distanceFromMidPointX = locationToUpdate.getFirstDouble() - screenMidPoint.x;
        double distanceFromMidPointY = locationToUpdate.getSecondDouble() - screenMidPoint.y;
        if(newWidth > oldWidth) {       // zooming
            locationToUpdate.add(distanceFromMidPointX, distanceFromMidPointY);
        }
        else {      // unzooming
            locationToUpdate.subtract(distanceFromMidPointX / 2.0, distanceFromMidPointY / 2.0);
        }
    }

    @Override
    public void updateSize(Dimension newSize) {
        super.updateSize(newSize);
        diagramPanel.sizeChangeCallback();
    }
}
