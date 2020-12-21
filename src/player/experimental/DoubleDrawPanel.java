package player.experimental;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

public class DoubleDrawPanel extends JPanel implements MouseMotionListener, MouseListener {

	public DoubleDrawPanel() {
		this.addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent e) {
				// EMPTY
			}
			
			@Override
			public void componentResized(ComponentEvent e) {				
				int w = e.getComponent().getWidth();
				int h = e.getComponent().getHeight();
				convertWaveToNewSize(w, h);
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
				// EMPTY
			}
			
			@Override
			public void componentHidden(ComponentEvent e) {
				// EMPTY
			}
		});
		
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		setTimeInMs(1000);
	}
	
	private double[] wave = new double[0];
	private Point oldMouseLoc;
	private int oldHeight = -1;
	private boolean isInit = true;
	private double timeInMs;
	private String timeInMsString;
	public void setTimeInMs(double timeInMs) {
		this.timeInMs = timeInMs;
		timeInMsString = Double.toString(timeInMs);
	}
	
	
	private void convertWaveToNewSize(int w, int h) {
		double[] oldWave = wave;
		wave = new double[w];
		if(isInit) {
			for(int i = 0; i < wave.length; i++) {
				wave[i] = 0.5;
			}
		}
		else {
			convertWaveToNewSize(oldWave, wave);
		}
		
		oldHeight = h;
		repaint();
	}
	
	
	// Modified code from getOneSecondWave, because in java I can't unfortunately write generic code for
	// primitive type arrays
	public static void convertWaveToNewSize(double[] oldWave, double[] newWave) {
		double samplesPerPixel = newWave.length / (double)(oldWave.length - 1);
		double modulo;
		if((int)samplesPerPixel == 0) {
			modulo = samplesPerPixel;
		}
		else {
			modulo = samplesPerPixel % (int)samplesPerPixel;
		}
		double currentSamplesPerPixel = samplesPerPixel;
		double currSample;
		double nextSample = oldWave[0];
		for(int i = 0, outputIndex = 0; i < oldWave.length - 1; i++, currentSamplesPerPixel += modulo) {
			currSample = nextSample;
			nextSample = oldWave[i + 1];
			
			double jump = (nextSample - currSample) / currentSamplesPerPixel;
			double val = currSample;
			for(int j = 0; j < (int)currentSamplesPerPixel; j++, outputIndex++, val += jump) {
				newWave[outputIndex] = val;
				System.out.println("OUTPUT_INDEX:\t" + outputIndex + "\t" + newWave[outputIndex]);
			}
			
			if(currentSamplesPerPixel >= ((int)samplesPerPixel + 1)) {
				currentSamplesPerPixel--;
			}
		}
	}


	
	public double[] getOneSecondWave(int sampleRate) {
		return getOneSecondWave(wave, sampleRate);
	}
	
	public static double[] getOneSecondWave(double[] wave, int sampleRate) {
		return getNPeriods(wave, sampleRate, 1, 1000);
	}
	

	
	/**
	 * @param periodTime is in milliseconds
	 * @return
	 */
	public double[] getNPeriods(int sampleRate, int periodCount, double periodTime) {
		return getNPeriods(wave, sampleRate, periodCount, periodTime);
	}

	/**
	 * @param periodTime is in milliseconds
	 * @return
	 */
	public static double[] getNPeriods(double[] wave, int sampleRate, int periodCount, double periodTime) {
		int len = (int)((periodTime / 1000) * sampleRate);
		double[] arr = new double[len];
		double samplesPerPixel = arr.length / (double)(wave.length - 1);
		fillArrWithValues(arr, wave, samplesPerPixel);
		return arr;
	}
	
	
	
	public void fillArrWithValues(double[] arr, double samplesPerPixel) {
		fillArrWithValues(arr, wave, samplesPerPixel);
	}
	
	public static void fillArrWithValues(double[] arr, double[] wave, double samplesPerPixel) {
		double modulo;
		if((int)samplesPerPixel == 0) {
			modulo = samplesPerPixel;
		}
		else {
			modulo = samplesPerPixel % (int)samplesPerPixel;
		}
		double currentSamplesPerPixel = samplesPerPixel;
		double currSample;
		double nextSample = 1 - 2*wave[0];	// 1 in wave == -1, 0 in wave == 1, 0.5 in wave == 0 
											// 0.75 == -0.5, 0.25 == 0.5 ... so it is 1 - 2*wave 
		for(int i = 0, outputIndex = 0; i < wave.length - 1; i++, currentSamplesPerPixel += modulo) {
			currSample = nextSample;
			nextSample = 1 - 2*wave[i + 1];
			
			double jump = (nextSample - currSample) / currentSamplesPerPixel;
			double val = currSample;
			for(int j = 0; j < (int)currentSamplesPerPixel; j++, outputIndex++, val += jump) {
				arr[outputIndex] = val;
			}
			
			if(currentSamplesPerPixel >= ((int)samplesPerPixel + 1)) {
				currentSamplesPerPixel--;
			}
		}
	}
	
	

	
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		int w, h;
		w = this.getWidth();
		h = this.getHeight();
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, w, h);

		g.setColor(Color.black);
		g.drawLine(0, h / 2, w, h / 2);
		
		
		g.setColor(Color.BLUE);
		for(int x = 0, y; x < wave.length; x++) {
			y = (int)(wave[x] * h); 
			//g.drawRect(x, y, 1, 1);		// TODO: NOVY TODO
			g.drawLine(x, h / 2, x, y);
			System.out.println("double draw panel painting: " + x + ":\t" + wave[x]);
		}
		
		
		
		g.setColor(Color.BLACK);
		final int TIME_LABEL_COUNT = 4;
		double timeJump = timeInMs / TIME_LABEL_COUNT;
		double currTime = 0;
		for(int i = 0, x = 0; i < TIME_LABEL_COUNT; i++, x += w / TIME_LABEL_COUNT, currTime += timeJump) {
			g.drawLine(x, 0, x, h);
			g.drawString(Double.toString(currTime), x, h / 2);
		}

		g.drawString(timeInMsString, w - g.getFontMetrics().stringWidth(timeInMsString), h / 2);
	}


	@Override
	public void mouseDragged(MouseEvent e) {
		isInit = false;
		Point p = e.getPoint();
		setPixelsBetweenCurrentMouseLocAndOld(p);
		this.repaint();
		oldMouseLoc = p;
	}
	
	private void setPixelsBetweenCurrentMouseLocAndOld(Point currMouseLoc) {
		int w = this.getWidth();
		int h = this.getHeight();
        double jump;
        
        currMouseLoc.y = Math.max(0, currMouseLoc.y);
        currMouseLoc.y = Math.min(h - 1, currMouseLoc.y);
    	currMouseLoc.x = Math.min(w-1, currMouseLoc.x);
    	currMouseLoc.x = Math.max(0, currMouseLoc.x);      
        if(oldMouseLoc == null) {
            oldMouseLoc = currMouseLoc;
        }
        
        
        double y = oldMouseLoc.y;
        if(currMouseLoc.x <= oldMouseLoc.x) {
        	jump = (currMouseLoc.y - oldMouseLoc.y) / (double)(oldMouseLoc.x - currMouseLoc.x);
            for (int i = oldMouseLoc.x; i >= currMouseLoc.x; i--, y += jump) {
            	wave[i] = y / h;
            }	
        }
        else if(currMouseLoc.x > oldMouseLoc.x) {
        	jump = (currMouseLoc.y - oldMouseLoc.y) / (double)(currMouseLoc.x - oldMouseLoc.x);
            for (int i = oldMouseLoc.x; i <= currMouseLoc.x; i++, y += jump) {
            	wave[i] = y / h;
            }        	
        }
		//wave[currMouseLoc.x] = currMouseLoc.y / (double)h;		// TODO: NOVY TODO
	}


	@Override
	public void mouseMoved(MouseEvent e) {
		// EMPTY
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		mouseDragged(e);
		oldMouseLoc = null;
	}


	@Override
	public void mouseEntered(MouseEvent e) {
		// EMPTY
	}


	@Override
	public void mouseExited(MouseEvent e) {
		// EMPTY
	}


	@Override
	public void mousePressed(MouseEvent e) {
		// EMPTY
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		oldMouseLoc = null;
	}
}