package ru.mephi.vikingdemo.gui;

import ru.mephi.vikingdemo.model.EquipmentItem;
import ru.mephi.vikingdemo.model.Viking;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VikingTableModel extends AbstractTableModel {

    // добавлена колонка ID первой (для поиска строки при удалении/обновлении)

    private final String[] columns = {"ID", "Name", "Age", "Height (cm)", "Hair color", "Beard style", "Equipment"};
    private final List<Viking> data = new ArrayList<>();

    public void addViking(Viking viking) {
        int row = data.size();
        data.add(viking);
        fireTableRowsInserted(row, row);
    }


    public void removeViking(int rowIndex) {
        data.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }


    public void updateViking(int rowIndex, Viking viking) {
        data.set(rowIndex, viking);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public int findRowById(int id) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).id() != null && data.get(i).id() == id) {
                return i;
            }
        }
        return -1; // не найден
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Viking viking = data.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> viking.id();           // добавлена колонка ID
            case 1 -> viking.name();
            case 2 -> viking.age();
            case 3 -> viking.heightCm();
            case 4 -> viking.hairColor();
            case 5 -> viking.beardStyle();
            case 6 -> formatEquipment(viking.equipment());
            default -> "";
        };
    }

    private String formatEquipment(List<EquipmentItem> equipment) {
        return equipment.stream()
                .map(item -> item.name() + " [" + item.quality() + "]")
                .collect(Collectors.joining(", "));
    }
}