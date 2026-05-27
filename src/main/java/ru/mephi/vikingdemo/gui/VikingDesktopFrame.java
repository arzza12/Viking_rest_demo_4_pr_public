package ru.mephi.vikingdemo.gui;

import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.VikingService;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

public class VikingDesktopFrame extends JFrame {

    private final VikingService vikingService;
    private final VikingTableModel tableModel = new VikingTableModel();

    public VikingDesktopFrame(VikingService vikingService) {
        this.vikingService = vikingService;

        setTitle("Viking Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(1100, 420)); // добавилась колонка ID
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel header = new JLabel("Viking Demo", SwingConstants.CENTER);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 18f));
        add(header, BorderLayout.NORTH);

        JTable vikingTable = new JTable(tableModel);
        vikingTable.setRowHeight(28);
        add(new JScrollPane(vikingTable), BorderLayout.CENTER);

        JButton createButton = new JButton("Create random viking");
        createButton.addActionListener(event -> onCreateViking());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(createButton);
        add(bottomPanel, BorderLayout.SOUTH);

        onInit();
    }

    private void onCreateViking() {
        Viking viking = vikingService.createRandomViking();
        tableModel.addViking(viking);
    }

    public void addNewViking(Viking viking) {
        tableModel.addViking(viking);
    }

    /**
     * Удаляет строку с викингом из таблицы по его id.
     * Вызывается из VikingListener когда DELETE /api/vikings/{id} выполнен успешно.
     * SwingUtilities.invokeLater обновление GUI всегда должно быть в EDT потоке,а REST-запрос приходит из другого потока (HTTP thread pool).
     */
    public void removeViking(int id) {
        SwingUtilities.invokeLater(() -> {
            int rowIndex = tableModel.findRowById(id);
            if (rowIndex == -1) {
                // Викинг не найден в таблице, тогда ниче не происходит
                System.out.println("removeViking: строка с id=" + id + " не найдена в таблице");
                return;
            }
            tableModel.removeViking(rowIndex);
        });
    }

    /**
     * Обновляет строку викинга в таблице.
     * Вызывается из VikingListener когда PATCH /api/vikings/{id} выполнен успешно.
     */
    public void updateViking(Viking viking) {
        SwingUtilities.invokeLater(() -> {
            if (viking.id() == null) {
                System.out.println("updateViking: viking.id() == null, обновление невозможно");
                return;
            }
            int rowIndex = tableModel.findRowById(viking.id());
            if (rowIndex == -1) {
                System.out.println("updateViking: строка с id=" + viking.id() + " не найдена в таблице");
                return;
            }
            tableModel.updateViking(rowIndex, viking);
        });
    }

    private void onInit() {
        List<Viking> all = vikingService.findAll();
        if (!all.isEmpty()) {
            for (Viking viking : all) {
                tableModel.addViking(viking);
            }
        }
    }
}