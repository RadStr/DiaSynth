package RocnikovyProjektIFace.DecibelDetectorPackage;

import Rocnikovy_Projekt.ProgramTest;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;

// I choose to use doubles instead of ints (for jumps) since it is just a few instructions
// And the painting won't be called that often.
public class DecibelDetector extends JPanel {
	public DecibelDetector(GetValuesIFace valueGetter) {
		this.setLayout(null);
		
		this.valueGetter = valueGetter;
		decibelReferences = new JLabel[START_REFERENCE_COUNT];
		addReferenceLabels();
		
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Component c = e.getComponent();
				
				int w = c.getWidth();
				recalculateDecibelReferences(w);
				if(decibelReferences == null || decibelReferences.length <= 0) {
				    revalidate();
				    repaint();
				    return;
                }
				int h = c.getHeight();
				int decibelReferenceY = h / 2 - decibelReferences[0].getHeight() / 2;
				double xJump = w / (double)(decibelReferences.length - 1);
				double x = 0;
				
				for(int i = 0; i < decibelReferences.length - 1; i++, x += xJump) {
					decibelReferences[i].setLocation((int)x, decibelReferenceY);
					System.out.println(decibelReferences[i]);
				}
				// Last one is 0 - it needs to be on the left else it is not visible
				JLabel last = decibelReferences[decibelReferences.length - 1]; 
				last.setLocation(((int)x) - last.getWidth(), decibelReferenceY);
				
				
				revalidate();
				repaint();
			}
		});

		setToolTipText("Contains the amplitude in decibels (dB) for each channel");
	}
	
	private GetValuesIFace valueGetter;
	private double[] decibels = new double[0];
	private int[] decibelsWidths = new int[0];
	
	private static final int MIN_DECIBEL = -60;
	private static final int START_REFERENCE_COUNT = 11;
	
	private double referenceJump = 6;
	private JLabel[] decibelReferences;
	
	private void recalculateDecibelReferences(int newWidth) {
		int maxLabelWidth = decibelReferences[0].getPreferredSize().width;
		for(JLabel label : decibelReferences) {
			this.remove(label);
		}
		
		int newReferenceCount = newWidth / (2*maxLabelWidth);	
		double floor;
		do {
			referenceJump = MIN_DECIBEL / (double)(newReferenceCount - 1);
			referenceJump = -referenceJump;
			floor = Math.floor(referenceJump);
			newReferenceCount--;
		} while(referenceJump != floor && referenceJump != floor + 0.5);	// If the jump is integer or integer + 0.5
		newReferenceCount++;
		
		decibelReferences = new JLabel[newReferenceCount];
		addReferenceLabels();
	}
	
	private void addReferenceLabels() {
		double reference = MIN_DECIBEL;
		for(int i = 0; i < decibelReferences.length; i++, reference += referenceJump) {
			if(i == decibelReferences.length - 1) {
				decibelReferences[i] = new JLabel("0.0");
			}
			else {
				decibelReferences[i] = new JLabel(String.format("%.1f", reference));
			}
			decibelReferences[i].setSize(decibelReferences[i].getPreferredSize());
			this.add(decibelReferences[i]);
		}
	}


	private boolean shouldFindNewDecibels = true;
	public void setShouldFindNewDecibels() {
	    shouldFindNewDecibels = true;
    }
    private int oldWidth;

	public boolean isDrawingEnabled = true;
	public void setIsDrawingEnabled(boolean enable) {
		isDrawingEnabled = enable;
		for(int i = 0; i < decibelReferences.length; i++) {
			decibelReferences[i].setVisible(enable);
		}
	}



	@Override
	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(!isDrawingEnabled) {
        	return;
		}

        int w = this.getWidth();
        int h = this.getHeight();

        // Because else there may be decibelsWidths for old width so there is for a brief moment incorrectly filled rectangle -
        // and it is very visible and unpleasant to see (filled rectangle appears and disappears few milliseconds after)
        if(oldWidth != w) {
            setShouldFindNewDecibels();
            oldWidth = w;
        }

        double yJump = (h - 1) / (double) decibels.length;
        double currY = 0;
        double nextY = yJump;

        if (shouldFindNewDecibels) {
            shouldFindNewDecibels = false;

            double[] amplitudes = valueGetter.getValues();
            if (amplitudes.length != decibels.length) {
                decibels = new double[amplitudes.length];
                decibelsWidths = new int[decibels.length];
            }


            for (int i = 0; i < decibels.length; i++, currY = nextY, nextY += yJump) {
                decibels[i] = 20 * Math.log10(Math.abs(amplitudes[i]) / 1);     // Math.abs because log is defined only for >0 numbers
                int rectangleHeight = ((int)nextY - (int)currY);
                g.setColor(Color.blue);
                decibelsWidths[i] = (int) (w * (decibels[i] / MIN_DECIBEL));      // if amplitudes == 0 then decibels == -infinity then result of this is infinity
                decibelsWidths[i] = Math.min(decibelsWidths[i], w);
                g.fillRect(0, (int) currY, w - decibelsWidths[i], rectangleHeight);
                g.setColor(Color.black);
                g.drawRect(0, (int) currY, w, rectangleHeight);
            }
        }
        else {
            for (int i = 0; i < decibels.length; i++, currY = nextY, nextY += yJump) {
                int rectangleHeight = ((int)nextY - (int)currY);

                g.setColor(Color.blue);
                g.fillRect(0, (int) currY, w - decibelsWidths[i], rectangleHeight);
                g.setColor(Color.black);
                g.drawRect(0, (int) currY, w, rectangleHeight);
            }
        }


        g.setColor(Color.black);
        double xJump = w / (double) (decibelReferences.length - 1);
        double x = 0;
        for (int i = 0; i < decibelReferences.length; i++, x += xJump) {
            g.drawLine((int) x, 0, (int) x, h);
        }
    }
}
