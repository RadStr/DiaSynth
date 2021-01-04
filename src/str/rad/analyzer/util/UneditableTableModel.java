package str.rad.analyzer.util;

import javax.swing.table.DefaultTableModel;

public class UneditableTableModel extends DefaultTableModel {
    private static final long serialVersionUID = 1L;

    public UneditableTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
