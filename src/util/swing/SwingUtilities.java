package util.swing;

import main.DiasynthTabbedPanel;

import javax.swing.*;
import java.awt.*;

public class SwingUtilities {
    private SwingUtilities() {}      // Allow only static access


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Draw methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // https://stackoverflow.com/questions/19386951/how-to-draw-a-circle-with-given-x-and-y-coordinates-as-the-middle-spot-of-the-ci
    public static void drawCenteredCircle(Graphics g, int x, int y, int r) {
        x = x - r;
        y = y - r;
        int d = 2 * r;
        g.fillOval(x, y, d, d);
    }


    public static int drawStringWithSpace(Graphics g, Color color, String s, int currX, int binWidth, int y) {
        FontMetrics fontMetrics = g.getFontMetrics();
        g.setColor(color);
        int textLen = fontMetrics.stringWidth(s);
        int textStart = (binWidth - textLen) / 2;
        int stringStartX = currX + textStart;
        g.drawString(s, stringStartX, y);

        return stringStartX;

// TODO: Vymazat, jen DEBUG testovani neceho
//        int x = stringStartX + textLen / 2;
//        g.drawLine(x, 0, x, 400);
//
//        g.setColor(Color.red);
//        x = currX + binWidth / 2;
//        g.drawLine(x, 0, x, 400);
    }


    public static void drawStringWithDefinedMidLoc(Graphics g, Color color, String s, int mid, int y) {
        FontMetrics fontMetrics = g.getFontMetrics();
        g.setColor(color);
        int textLen = fontMetrics.stringWidth(s) - 1;       // -1 because it pushes more to the middle
        int stringStartX = mid - textLen / 2;
        g.drawString(s, stringStartX, y);
    }

    public static void drawLabelWithDefinedMidLoc(Graphics g, JLabel label, int mid, int y) {
        int textLen = label.getWidth() - 1;       // -1 because it pushes more to the middle
        int labetStartX = mid - textLen / 2;
        label.setLocation(labetStartX, y);
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Draw methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Set label location methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void setLabelLocWithSpace(JLabel label, int startX, int binWidth, int y) {
        int textLen = label.getWidth();
        int textStart = (binWidth - textLen) / 2;
        int labetStartX = startX + textStart;
        label.setLocation(labetStartX, y);
    }

    public static void setLabelLocWithSpace(JLabel label, int startX, int startY, int binWidth, int binHeight) {
        FontMetrics fm = label.getFontMetrics(label.getFont());
        int textLen =  fm.stringWidth(label.getText());
        int textStartX = (binWidth - textLen) / 2;
        int x = startX + textStartX;

        int textHeight = fm.getHeight();
        int textStartY = (binHeight - textHeight) / 2;
        int y = startY + textStartY;

        label.setLocation(x, y);
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Set label location methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Find font methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void findBiggestFontToFitSize(JLabel label, int maxWidth, int maxHeight) {
        Font oldFont = label.getFont();
        int currFontSize = oldFont.getSize();
        FontMetrics fm = label.getFontMetrics(oldFont);

        Font newFont = new Font(oldFont.getName(), oldFont.getStyle(), currFontSize);
        int textWidth = fm.stringWidth(label.getText());
        if(textWidth < maxWidth && textWidth >= 0) {
            while(currFontSize < DiasynthTabbedPanel.MAX_LABEL_FONT_SIZE) {
                // TODO: DEBUG
                //ProgramTest.debugPrint("Font:", newFont, "w and h", maxWidth, maxHeight);
                // TODO: DEBUG
                currFontSize++;
                newFont = new Font(oldFont.getName(), oldFont.getStyle(), currFontSize);
                fm = label.getFontMetrics(newFont);
                textWidth = fm.stringWidth(label.getText());
                if(textWidth > maxWidth) {
                    currFontSize = Math.max(1, currFontSize - 1);
                    newFont = new Font(oldFont.getName(), oldFont.getStyle(), currFontSize);
                    fm = label.getFontMetrics(newFont);
                    label.setFont(newFont);
                    if(fm.getHeight() > maxHeight) {
                        findBiggestFontToFitMaxHeight(label, maxHeight);
                    }
                    return;
                }
                else if(textWidth < 0) {
                    break;
                }
            }

            // If we get here then the maximum label size was reached
            newFont = new Font(oldFont.getName(), oldFont.getStyle(), DiasynthTabbedPanel.MAX_LABEL_FONT_SIZE);
            label.setFont(newFont);
            findBiggestFontToFitMaxHeight(label, maxHeight);
        }
        else if (textWidth > maxWidth) {
            while(currFontSize > 1) {
                // TODO: DEBUG
                //ProgramTest.debugPrint("Font:", newFont, "w and h", maxWidth, maxHeight);
                // TODO: DEBUG
                currFontSize--;
                newFont = new Font(oldFont.getName(), oldFont.getStyle(), currFontSize);
                fm = label.getFontMetrics(newFont);
                if(fm.stringWidth(label.getText()) < maxWidth) {
                    break;
                }
            }

            label.setFont(newFont);
            if(fm.getHeight() > maxHeight) {
                findBiggestFontToFitMaxHeight(label, maxHeight);
            }
            return;
        }
    }

    public static void findBiggestFontToFitMaxHeight(JLabel label, int maxHeight) {
        Font newFont = label.getFont();
        int currFontSize = newFont.getSize();
        FontMetrics fm = label.getFontMetrics(newFont);
        while(fm.getHeight() >= maxHeight) {
            currFontSize--;
            newFont = new Font(newFont.getName(), newFont.getStyle(), currFontSize);
            fm = label.getFontMetrics(newFont);
        }

        label.setFont(newFont);
    }


    // On My system the result is java.awt.Font[family=Dialog,name=Dialog,style=bold,size=26822]
    public static int findMaxFontSize(JLabel label) {
        Font oldFont = label.getFont();
        int currFontSize = 0;
        FontMetrics fm ;
        Font newFont;
        while(true) {
            currFontSize++;
            newFont = new Font(oldFont.getName(), oldFont.getStyle(), currFontSize);
            fm = label.getFontMetrics(newFont);
            // TODO: DEBUG
            //ProgramTest.debugPrint("Font:", newFont, "stringWidth", fm.stringWidth(label.getText()));
            // TODO: DEBUG
            if(fm.stringWidth(label.getText()) < 0) {
                return currFontSize - 1;
            }
        }
    }

    public static int findMaxFontSize(int startFontSize, Graphics g, String[] texts,
                                      int maxWidth, int maxHeight, int checkNthIndexes) {
        int fontSize = startFontSize;
        FontMetrics fontMetrics;
        for(int i = 0; fontSize > 0; fontSize--) {
            g.setFont(new Font("Serif", Font.BOLD, fontSize));
            fontMetrics = g.getFontMetrics();
            for(; i < texts.length; i++) {
                if(i % checkNthIndexes == 0) {
                    if (fontMetrics.stringWidth(texts[i]) > maxWidth || fontMetrics.getHeight() > maxHeight) {
                        break;
                    }
                }
            }

            if(i >= texts.length) {
                break;
            }
        }

        return fontSize;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Find font methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Set font methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void setFontSize(JLabel label, int oldWidth, int newWidth) {
        float ratio = newWidth / (float)oldWidth;
        // TODO: DEBUG
        //ProgramTest.debugPrint("Old font:", label.getFont());
        // TODO: DEBUG
        Font oldFont = label.getFont();
        Font newFont = oldFont.deriveFont(ratio * oldFont.getSize2D());
        label.setFont(newFont);
        // TODO: DEBUG
        //ProgramTest.debugPrint("New font:", label.getFont());
        // TODO: DEBUG
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Set font methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Find max width for set of characters methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static int calculateMaxWidth(char startChar, char endChar, FontMetrics fm) {
        int maxWidth = -1;
        char c = startChar;
        while(c <= endChar) {
            maxWidth = Math.max(maxWidth, fm.charWidth(c));
            c++;
        }

        return maxWidth;
    }

    public static int calculateMaxWidthDigit(FontMetrics fm) {
        return calculateMaxWidth('0', '9', fm);
    }

    public static int calculateMaxWidthAlphabetLowerCase(FontMetrics fm) {
        return calculateMaxWidth('a', 'z', fm);
    }

    public static int calculateMaxWidthAlphabetUpperCase(FontMetrics fm) {
        return calculateMaxWidth('A', 'Z', fm);
    }

    public static int calculateMaxWidthAlfanum(FontMetrics fm) {
        int maxWidth = -1;
        maxWidth = Math.max(maxWidth, calculateMaxWidthDigit(fm));
        maxWidth = Math.max(maxWidth, calculateMaxWidthAlphabetLowerCase(fm));
        maxWidth = Math.max(maxWidth, calculateMaxWidthAlphabetUpperCase(fm));
        return maxWidth;
    }

    public static Dimension calculateMaximizedFrameSize() {
        JFrame f = new JFrame();
        f.setExtendedState(f.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        f.setVisible(true);
        Dimension size = f.getSize();
        f.setVisible(false);
        f.dispose();
        return size;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Find max width for set of characters methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
