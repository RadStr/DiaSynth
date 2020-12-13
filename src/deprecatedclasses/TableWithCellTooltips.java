package deprecatedclasses;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// TODO: RML
@Deprecated
public class TableWithCellTooltips extends JTable {
    public TableWithCellTooltips(DefaultTableModel tableModel, String... columnToolTips) {
        super(tableModel);
        lastRowIndex = -1;
        lastToolTip = new StringBuilder(HTML_START_TAG);
        this.columnToolTips = columnToolTips;

        this.addMouseListener(new MouseAdapter() {
            // user moved mouse out of table, so we expect that he won't be working with the table for a while
            // so we shrink the size of tooltip
            @Override
            public void mouseExited(MouseEvent e) {
                lastRowIndex = -1;
                resetLastToolTip();
                lastToolTip.trimToSize();
            }
        });
    }

    private int lastRowIndex;
    private StringBuilder lastToolTip;
    private final String HTML_NEW_LINE_TAG = "<br>";
    private final String HTML_START_TAG = "<html>";
    private final String HTML_END_TAG = "<html>";

    private String[] columnToolTips;
    public void setColumnToolTips(String[] newTooltips) {
        columnToolTips = newTooltips;
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        java.awt.Point p = e.getPoint();
        int rowIndex = rowAtPoint(p);


// Shows only the cell
//        String tip = null;
//        int colIndex = columnAtPoint(p);
//        try {
//            tip = getValueAt(rowIndex, colIndex).toString();
//        } catch (RuntimeException e1) {
//            //catch null pointer exception if mouse is over an empty line
//        }
//        return tip;

// Shows whole line
        if(rowIndex == -1) {
            lastRowIndex = -1;
            return "";
        }
        else if(rowIndex != lastRowIndex) {
            lastRowIndex = rowIndex;
            // Reuse the old object for performance (it may cause memory leak - having larger buffer than it is necessary)
            // But since the text will be short, it doesn't matter
            resetLastToolTip();
            int colCount = getColumnCount();
            String previousSeparator = "";
            for(int i = 0; i < colCount; i++, previousSeparator = HTML_NEW_LINE_TAG) {
                lastToolTip.append(previousSeparator);     // new line
                lastToolTip.append(getValueAt(rowIndex, i));
            }
            lastToolTip.append(HTML_END_TAG);
        }

        return lastToolTip.toString();
    }

    //Implement table header tool tips.
    @Override
    protected JTableHeader createDefaultTableHeader() {
        return new JTableHeaderWithToolTips(columnModel);
    }

    private class JTableHeaderWithToolTips extends JTableHeader {
        public JTableHeaderWithToolTips(TableColumnModel columnModel) {
            super(columnModel);
        }

        @Override
        public String getToolTipText(MouseEvent e) {
            java.awt.Point p = e.getPoint();
            int index = columnModel.getColumnIndexAtX(p.x);
            int realIndex = columnModel.getColumn(index).getModelIndex();
            return columnToolTips[realIndex];
        }
    }

    private void resetLastToolTip() {
        lastToolTip.setLength(HTML_START_TAG.length());
    }
}
// TODO: RML