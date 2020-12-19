package player.wave;

import util.Aggregation;
import util.audio.AudioUtilities;
import util.swing.JLabelWithLineInMid;
import test.ProgramTest;
import util.swing.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class VerticalReferencesPanel extends JPanel {
    private JComponent[] components;
    private int valuesLongestWidth;
    private double pixelJump;
    private int labelCount;
    private int minLineLen = 5;

    private boolean isDouble;           // TODO: Poresit kdyz isDouble = false;

    private double minValue;
    public double getMinValue() {
        return minValue;
    }
    public void setMinValue(double minValue) {
        this.minValue = minValue;
        setMidValue();
    }

    private double maxValue;
    public double getMaxValue() {
        return maxValue;
    }
    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
        setMidValue();
    }


    private double midValue;
    public double getMidValue() {
        return midValue;
    }
    private void setMidValue() {
        midValue = Aggregation.performAggregation(minValue, maxValue, Aggregation.AVG);
    }

    public VerticalReferencesPanel(double minValue, double maxValue) {
        setMinValue(minValue);
        setMaxValue(maxValue);
        isDouble = true;
        FontMetrics fm = this.getFontMetrics(this.getFont());

        String widestDoubleVal = getStringDouble(-1.00);    // TODO: Melo by se menit podle toho jestli chci zobrazovat doubly nebo inty
        valuesLongestWidth = fm.stringWidth(widestDoubleVal);
        widestDoubleVal = getStringDouble(minValue);
        valuesLongestWidth = Math.max(valuesLongestWidth, fm.stringWidth(widestDoubleVal));
        widestDoubleVal = getStringDouble(maxValue);
        valuesLongestWidth = Math.max(valuesLongestWidth, fm.stringWidth(widestDoubleVal));

        components = null;

        JPanel thisPanel = this;
        ComponentListener resizeListener = new ComponentAdapter(){
            @Override
            public void componentResized(ComponentEvent e) {
                int height = e.getComponent().getHeight();
                int spaceSizeBetweenLabelsInPixels = 50;
                int halfHeight = height / 2;
                labelCount = halfHeight / spaceSizeBetweenLabelsInPixels;
                if(isDouble) {
                    if(labelCount > 100) {
                        labelCount = 100;
                    }
                }
                pixelJump = halfHeight / (double) (labelCount + 1);     // +1 because there will be max value + the other labels
                thisPanel.repaint();
            }
        };
        this.addComponentListener(resizeListener);



//        GridBagLayout layout = new GridBagLayout();
//        this.setLayout(layout);
//

        BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        this.setLayout(layout);





// TODO: Vymazat
//        int waveHeight = 300;
//        double maxVal = 1;
//        double minVal = -1;
//
//
//        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//        int spaceSizeBetweenLabelsInPixels = 50;
//        int halfHeight = waveHeight / 2;
//        int labelCount = halfHeight / spaceSizeBetweenLabelsInPixels;
//        pixelJump = halfHeight / (double) (labelCount + 1);     // +1 because there will be max value + the other labels
//        double valRange = maxVal - minVal;
//        double halfValRange = valRange / 2;
//        double halfValJump = halfValRange / (labelCount + 1);
//        System.out.println(labelCount + "\t" + waveHeight + "\t" + pixelJump);
//
//
//        double val = maxVal;
//        components = new JLabel[3 + 2 * labelCount];            // +3 because maxVal, 0, minVal
//        String valString;
//
//        valuesLongestWidth = 0;
//
//
//
//
//        int currGridY = 0;
//        GridBagConstraints constraints;
//        constraints = new GridBagConstraints();
//        constraints.fill = GridBagConstraints.BOTH;
//        constraints.gridx = 0;
//        constraints.gridy = currGridY;
//        constraints.weightx = 0;
//        constraints.weighty = 0.1;
//        for (int i = 0; i < components.length - 1; val -= halfValJump, i++, currGridY++, constraints.gridy = currGridY) {
//            valString = String.audioFormat("%.2f", val);
//            components[i] = new JLabel(valString);
//            this.add(components[i], constraints);
//
//
//            int w = components[i].getWidth();
//            if(w > valuesLongestWidth) {
//                valuesLongestWidth = w;
//            }
//        }
//        valString = String.audioFormat("%.2f", minVal);
//        components[components.length - 1] = new JLabel(valString);
//        JLabel l = components[components.length - 1];
//        this.add(l, constraints);
//        int w = l.getWidth();
//        if(w > valuesLongestWidth) {
//            valuesLongestWidth = w;
//        }
    }

    public void addComponentsToPane(Container c) {
        if(components == null) {
            components = new JComponent[3];

            BoxLayout layout = new BoxLayout(c, BoxLayout.Y_AXIS);
            c.setLayout(layout);

            JLabel label = new JLabel(Integer.toString(-1));
            c.add(label);

            JPanel p = new JPanel();
            layout = new BoxLayout(p, BoxLayout.Y_AXIS);
            p.setLayout(layout);

            c.add(p);

            int TOP = 4;

            for (int i = 0; i < TOP; i++) {
                JLabel l = new JLabel(Integer.toString(i));
                p.add(l);
            }

            components[0] = label;
            components[1] = p;

            label = new JLabel(Integer.toString(TOP));
            c.add(label);

            components[2] = label;

            c.revalidate();
            c.repaint();
        }
    }


//    // TODO: Just for debugging to show where should be the lines
//    @Override
//    protected void paintChildren(Graphics g){
//        super.paintChildren(g);
//        drawSamplesValueRangeDouble(g);
//
//        double pixel = 0;
//        double halfPixelJump = pixelJump / 2;
//        double nextPixel = halfPixelJump;
//        int i = 0;
//        while(pixel < this.getHeight()) {
//            if(i % 2 == 0) {
//                g.setColor(Color.CYAN);
//            //    g.drawRect(0, (int)pixel, this.getWidth() / 2, (int)nextPixel);
//            }
//            else {
//                //g.setColor(Color.green);
//            }
//            g.drawRect(0, (int)pixel, this.getWidth() / 2, (int)nextPixel);
//
//            //g.drawLine(0, (int)pixel, this.getWidth() / 2, (int)nextPixel);
//
//            pixel = nextPixel;
//            nextPixel += halfPixelJump;
//            i++;
//        }
//    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

//        addComponentsToPane(this);
        if(isDouble) {
            drawSamplesValueRangeDouble(g);
        }
        else {
            drawSamplesValueRangeInt(g, 24);
        }

//        GridBagLayout gbl = (GridBagLayout) this.getLayout();
//        drawGridBagLayout(gbl, g);


//        for(int i = 0; i < components.length; i++) {
//            g.setColor(Color.green);
//            System.out.println(i);
//            int x = components[i].getX();
//            int y = components[i].getY();
//            g.drawLine(0, y, this.getWidth(), y);
//
//
//            g.setColor(Color.red);
//            int h = components[i].getHeight();
//            y += h;
//            g.drawLine(0, y, this.getWidth(), y);
//        }
    }

    private void drawGridBagLayout(GridBagLayout gbl, Graphics g) {
        ProgramTest.printNTimes("-------------------------------------------------", 5);

        int[][] dims = gbl.getLayoutDimensions();
        g.setColor(Color.BLUE);
        int x = 0;
        System.out.println("X");
        for (int add : dims[0])
        {
            x += add;
            g.drawLine(x, 0, x, getHeight());
            System.out.println(x);
        }

        int y = 0;
        System.out.println("Y");
        for (int add : dims[1])
        {
            y += add;
            g.drawLine(0, y, getWidth(), y);
            System.out.println(y);
        }
    }


    private void drawSamplesValueRangeDouble(Graphics g) {
        drawSamplesValueRangeDouble(g, minValue, maxValue, midValue);
    }

    private void drawSamplesValueRangeInt(Graphics g, int sampleSizeInBits) {
        int valMax = AudioUtilities.getMaxAbsoluteValueSigned(sampleSizeInBits);
        int valMin = -valMax;
        valMax--;
        drawSamplesValueRangeInt(g, valMin, valMax, 0);
    }


    // midVal is usually 0
    private void drawSamplesValueRangeInt(Graphics g, int minVal, int maxVal, int midVal) {
        int waveHeight = this.getHeight();
        int waveStartX = 30;
        int waveStartY = 0;
        int waveEndY = waveHeight;

        int valRange;
        int valJump;

        valRange = Math.abs(maxVal - midVal);
        valJump = valRange / (labelCount + 1);
        System.out.println(labelCount + "\t" + waveHeight + "\t" + pixelJump);

        Color color = Color.black;
        g.setColor(color);
        int x = waveStartX;
//        double valRange = maxVal - minVal;
//        double halfValRange = valRange / 2;
//        double halfValJump = halfValRange / labelCount;
//        System.out.println(halfValJump + "\t" + halfValRange + "\t" + Math.ceil(labelCount / (double)2));
////        double valJump = valueRange / labelCount;   // For each space we perform 1 jump, so labelCount currently represents number of spaces
//        labelCount++;       // To add the minimum value, now it represents real label count
//        String valString;
        int startXForLine = x;

// TODO: DEBUG        int DEBUG = (int)(waveStartY + (1 + 0.633) * waveHeight / 2.0);        // TODO: !!!
// TODO: DEBUG        g.drawLine(0, DEBUG, this.getWidth(), DEBUG);


        int val = maxVal;
        double y = waveStartY;
        // +1 because maxVal, 0, minVal


        int textHeight = g.getFontMetrics().getHeight();
        drawFirstValueInt(x, (int) (y), maxVal, color, g, textHeight);
        y += pixelJump;
        val -= valJump;
        for (int i = 0; i < labelCount; y += pixelJump, val -= valJump, i++) {
            drawInternalValueInt(x, (int) y, val, color, g, textHeight);
        }
        drawInternalValueInt(x, (int) y, midVal, color, g, textHeight);


        valRange = Math.abs(minVal - midVal);
        valJump = valRange / (labelCount + 1);

        y += pixelJump;
        val = midVal - valJump;
        for (int i = 0; i < labelCount; y += pixelJump, val -= valJump, i++) {
            drawInternalValueInt(x, (int) y, val, color, g, textHeight);
        }


        drawLastValueInt(x, (int) (y), minVal, color, g, textHeight);
    }



    // midVal is usually 0
    private void drawSamplesValueRangeDouble(Graphics g, double minVal, double maxVal, double midVal) {
        int waveHeight = this.getHeight();
        int waveStartX = 30;
        int waveStartY = 0;
        int waveEndY = waveHeight;

        double valRange;
        double valJump;

        valRange = Math.abs(maxVal - midVal);
        valJump = valRange / (labelCount + 1);
// TODO: DEBUG
//        System.out.println(labelCount + "\t" + waveHeight + "\t" + pixelJump);
// TODO: DEBUG

        Color color = Color.black;
        g.setColor(color);
        int x = waveStartX;
//        double valRange = maxVal - minVal;
//        double halfValRange = valRange / 2;
//        double halfValJump = halfValRange / labelCount;
//        System.out.println(halfValJump + "\t" + halfValRange + "\t" + Math.ceil(labelCount / (double)2));
////        double valJump = valueRange / labelCount;   // For each space we perform 1 jump, so labelCount currently represents number of spaces
//        labelCount++;       // To add the minimum value, now it represents real label count
//        String valString;
        int startXForLine = x;

// TODO: DEBUG        int DEBUG = (int)(waveStartY + (1 + 0.633) * waveHeight / 2.0);        // TODO: !!!
// TODO: DEBUG        g.drawLine(0, DEBUG, this.getWidth(), DEBUG);


        double val = maxVal;
        double y = waveStartY;
        // +1 because maxVal, 0, minVal


        int textHeight = g.getFontMetrics().getHeight();
        drawFirstValueDouble(x, (int) (y), maxVal, color, g, textHeight);
        y += pixelJump;
        val -= valJump;
        for (int i = 0; i < labelCount; y += pixelJump, val -= valJump, i++) {
            drawInternalValueDouble(x, (int) y, val, color, g, textHeight);
        }
        drawInternalValueDouble(x, (int) y, midVal, color, g, textHeight);


        valRange = Math.abs(minVal - midVal);
        valJump = valRange / (labelCount + 1);

        y += pixelJump;
        val = midVal - valJump;
        for (int i = 0; i < labelCount; y += pixelJump, val -= valJump, i++) {
            drawInternalValueDouble(x, (int) y, val, color, g, textHeight);
        }

        drawLastValueDouble(x, getHeight() - 1, minVal, color, g, textHeight);
    }







    private GridBagConstraints createGBC(int gridx, int gridy, int gridwidth, int gridheight,
                         int fill, double weightx, double weighty) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.fill = fill;
        gbc.weightx = weightx;
        gbc.weighty = weighty;

        return gbc;
    }


    private int setLabel(int index, double val, int currGridY, Container container) {
        String valString = String.format("%.2f", val);
        JLabel label = new JLabel(valString);
        label = new JLabelWithLineInMid(10, valString);
//        components[index] = label;

        GridBagConstraints constraints;
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 2 * currGridY;
        constraints.weightx = 0;
        constraints.weighty = 1.0;
        //constraints.weighty = 1.0 / (components.length - 1);
        //constraints.weighty = 0.1;
//        constraints.gridheight = 2;



    //    constraints = createGBC(0, currGridY, 1, 1,
    //        GridBagConstraints.BOTH, 1, 1);




//        if(index == 0) {
//            constraints.anchor = GridBagConstraints.NORTH;
//            //constraints.fill = GridBagConstraints.NONE;
//            constraints.weighty /= 2;
////            constraints.weighty = 0;
////            constraints.gridheight /= 2;
//        }
//        else if(index == components.length - 1) {
//            constraints.anchor = GridBagConstraints.SOUTH;
//            //constraints.fill = GridBagConstraints.NONE;
//            constraints.weighty /= 2;
//            //constraints.weighty = 0;
////            constraints.gridheight /= 2;
////            constraints.gridy--;
//        }
//        else {
//   //         constraints.anchor = GridBagConstraints.CENTER;
//            constraints.gridy--;
//        }

//        constraints.fill = GridBagConstraints.NONE;
        //constraints.gridy = GridBagConstraints.RELATIVE;

    //    constraints.insets = new Insets(0, 0, 0, 10);

    //    container.add(label, constraints);

//        double currY = index * pixelJump;
//        int h = (int)pixelJump;
//        if((int)currY != (int)(currY - pixelJump) + h) {
//            h++;
//        }
//        //label.setSize(this.getPreferredSize().visibleWidth, h);
//
//        Dimension d = new Dimension(this.getPreferredSize().visibleWidth, h);
//        label.setPreferredSize(d);
////        d = new Dimension(this.getPreferredSize().visibleWidth-1, h);
////        label.setMinimumSize(d);
////        d = new Dimension(this.getPreferredSize().visibleWidth+1, h);
////        label.setMaximumSize(d);

        //label.setSize(this.getPreferredSize().visibleWidth, (int)pixelJump);
        container.add(label);
        int w = label.getWidth();
        if(w > valuesLongestWidth) {
            valuesLongestWidth = w;
        }

//        if(index == 0) {
//            constraints.gridy = 1;
//            currGridY = constraints.gridy;
//            constraints.weighty = 0.05;
////            int gridY = constraints.gridy;
////            constraints = new GridBagConstraints();
////            constraints.ipady = label.getHeight();
////            constraints.gridy = gridY-1;
//            this.add(new JPanel(), constraints);
//        }

        return currGridY + 1;
    }


    private void drawFirstValueInt(int x, int y, int valToDraw, Color color, Graphics g, int textHeight) {
        int shiftForStringY = textHeight - textHeight / 4;
        drawValueInt(x, y, valToDraw, color, g, shiftForStringY);
    }
    private void drawInternalValueInt(int x, int y, int valToDraw, Color color, Graphics g, int textHeight) {
        int shiftForStringY = textHeight / 4;
        drawValueInt(x, y, valToDraw, color, g, shiftForStringY);
    }
    private void drawLastValueInt(int x, int y, int valToDraw, Color color, Graphics g, int textHeight) {
        int shiftForStringY = -textHeight / 4;
        drawValueInt(x, y, valToDraw, color, g, shiftForStringY);
    }
    private void drawValueInt(int x, int y, int valToDraw, Color color, Graphics g, int shiftForStringY) {
        String valString = getStringInt(valToDraw);
        drawValue(valString, x, y, color, g, shiftForStringY);
    }


    private void drawFirstValueDouble(int x, int y, double valToDraw, Color color, Graphics g, int textHeight) {
        int shiftForStringY = textHeight - textHeight / 4;
        drawValueDouble(x, y, valToDraw, color, g, shiftForStringY);
    }
    private void drawInternalValueDouble(int x, int y, double valToDraw, Color color, Graphics g, int textHeight) {
        int shiftForStringY = textHeight / 4;
        drawValueDouble(x, y, valToDraw, color, g, shiftForStringY);
    }
    private void drawLastValueDouble(int x, int y, double valToDraw, Color color, Graphics g, int textHeight) {
        int shiftForStringY = -textHeight / 4;
        drawValueDouble(x, y, valToDraw, color, g, shiftForStringY);
    }
    private void drawValueDouble(int x, int y, double valToDraw, Color color, Graphics g, int shiftForStringY) {
        String valString = getStringDouble(valToDraw);
        drawValue(valString, x, y, color, g, shiftForStringY);
    }



    private void drawValue(String valString, int x, int y, Color color, Graphics g, int shiftForStringY) {
        int startX = SwingUtils.drawStringWithSpace(g, color, valString, 0, this.getWidth(), y + shiftForStringY);
        int w = g.getFontMetrics().stringWidth(valString);
        if(w > valuesLongestWidth) {
// TODO: DEBUG
//            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//            int w2 = this.getFontMetrics(this.getFont()).stringWidth(valString);
//            System.out.println(w + "\t" + valString + "\t" + g.getFont().getSize() + "\t" + g.getFont().getName() +
//                "\t" + this.getFont().getFontName() + "\t" + w2 +
//                "\t" + this.getFont().getPSName() + "\t" + this.getFont().getFamily() + "\t" + this.getFont().toString());
// TODO: DEBUG
            valuesLongestWidth = w;
        }

        startX += w;
        // TODO: Correct version:
        g.drawLine(startX, y, this.getWidth(), y);
        // TODO: Debug version:
        //g.drawLine(0, y, this.getWidth(), y);
    }


    public static String getStringDouble(double valToDraw) {
        String valString = String.format("%.2f", valToDraw);
        return valString;
    }

    public static String getStringInt(int valToDraw) {
        String valString = Integer.toString(valToDraw);
        return valString;
    }




    private void drawFirstValue(int lineLen, int y, JLabel label, Graphics g, int textHeight) {
        int shiftForStringY = textHeight - textHeight / 4;
        drawValue(lineLen, y, label, g, shiftForStringY);
    }

    private void drawInternalValue(int lineLen, int y, JLabel label, Graphics g, int shiftForStringY) {
        drawValue(lineLen, y, label, g, shiftForStringY);
    }

    private void drawLastValue(int lineLen, int y, JLabel label, Graphics g, int textHeight) {
        int shiftForStringY = -textHeight / 4;
        drawValue(lineLen, y, label, g, shiftForStringY);
    }


    private void drawValue(int lineLen, int y, JLabel label, Graphics g, int shiftForStringY) {
        y += shiftForStringY;
        int x = valuesLongestWidth - label.getWidth();
//        label.setLocation(x, y);
//        g.drawLine(valuesLongestWidth, y, valuesLongestWidth + minLineLen, y);
// TODO: DEBUG        System.out.println(label.getText() + "\t" + valuesLongestWidth + "\t" + y + "\t" + label.getWidth());
        // TODO:
        //ByteWave.drawStringWithSpace(g, color, valString, 0, startXForLine, y + shiftForStringY);
    }


    @Override
    public Dimension getPreferredSize() {
// TODO: DEBUG
//        System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
//        FontMetrics fm = this.getFontMetrics(this.getFont());
//        int w = fm.stringWidth(Double.toString(-1.00));
//        System.out.println((valuesLongestWidth + minLineLen) + "\t" + super.getPreferredSize().height + "\t" +
//            valuesLongestWidth + "\t" + this.getFont().getSize() + "\t" + w + "\t" + this.getFont().getName() + "\t" + this.getFont().getFontName() +
//            "\t" + this.getFont().getPSName() + "\t" + this.getFont().getFamily() + "\t" + this.getFont().toString());
        return new Dimension(valuesLongestWidth + minLineLen, super.getPreferredSize().height);
    }



}
