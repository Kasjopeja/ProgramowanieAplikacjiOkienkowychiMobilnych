package com.example.lab4;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;

public class GenericTableModel<T> extends AbstractTableModel {

    private List<T> data = new ArrayList<>();
    private Class<T> type;
    private List<String> excludedColumns = new ArrayList<>(); // Lista wykluczonych kolumn

    public GenericTableModel(Class<T> type) {
        this.type = type;
    }

    public void setExcludedColumns(List<String> excludedColumns) {
        this.excludedColumns = excludedColumns;
    }

    public void setData(List<T> data) {
        this.data = data;
        fireTableDataChanged();
    }

    public void addRow(T row) {
        data.add(row);
        fireTableRowsInserted(data.size() - 1, data.size() - 1);
    }

    public void removeRow(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < data.size()) {
            data.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }

    public T getRow(int rowIndex) {
        return data.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return (int) java.util.Arrays.stream(type.getDeclaredFields())
                .filter(field -> !excludedColumns.contains(field.getName()))
                .count();
    }

    @Override
    public String getColumnName(int columnIndex) {
        Field[] fields = type.getDeclaredFields();
        int adjustedIndex = 0;
        for (Field field : fields) {
            if (!excludedColumns.contains(field.getName())) {
                if (adjustedIndex == columnIndex) {
                    return field.getName();
                }
                adjustedIndex++;
            }
        }
        return null;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        T row = data.get(rowIndex);
        Field[] fields = type.getDeclaredFields();
        int adjustedIndex = 0;
        for (Field field : fields) {
            if (!excludedColumns.contains(field.getName())) {
                if (adjustedIndex == columnIndex) {
                    field.setAccessible(true);
                    try {
                        return field.get(row);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                adjustedIndex++;
            }
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }
}
