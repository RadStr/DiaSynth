package deprecatedclasses;

import javax.swing.table.DefaultTableModel;

// TODO: RML
@Deprecated
public class TableModelUneditableNamesEditableValues extends DefaultTableModel {
    private static final long serialVersionUID = 1L;

    public TableModelUneditableNamesEditableValues(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
        //TODO: https://docs.oracle.com/javase/tutorial/uiswing/components/table.html#celltooltip
//        this.addTableModelListener(new TableModelListener() {
//            @Override
//            public void tableChanged(TableModelEvent e) {
//
//                int row = e.getFirstRow();
//                int column = e.getColumn();
//                TableModel model = (TableModel)e.getSource();
//                String columnName = model.getColumnName(column);
//                Object data = model.getValueAt(row, column);
//            }
//        });
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if(column == 0) {
            return false;
        }
        else {
            return true;
        }
    }
}
// TODO: RML