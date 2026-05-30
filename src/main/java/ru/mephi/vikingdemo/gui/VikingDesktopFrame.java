package ru.mephi.vikingdemo.gui;

import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.VikingAnalyticsService;
import ru.mephi.vikingdemo.service.VikingService;

import javax.swing.Box;
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
    private final VikingAnalyticsService analyticsService;
    private final VikingTableModel tableModel = new VikingTableModel();

    public VikingDesktopFrame(VikingService vikingService,
                              VikingAnalyticsService analyticsService) {
        this.vikingService = vikingService;
        this.analyticsService = analyticsService;

        setTitle("Viking Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(1100, 420));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel header = new JLabel("Viking Demo", SwingConstants.CENTER);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 18f));
        add(header, BorderLayout.NORTH);

        JTable vikingTable = new JTable(tableModel);
        vikingTable.setRowHeight(28);
        add(new JScrollPane(vikingTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();

        JButton createButton = new JButton("Create random viking");
        createButton.addActionListener(event -> onCreateViking());
        bottomPanel.add(createButton);

        JButton analyticsButton = new JButton("Аналитика");
        analyticsButton.addActionListener(event -> onOpenAnalytics());
        bottomPanel.add(analyticsButton);

        JButton generateButton = new JButton("Сгенерировать N викингов");
        generateButton.addActionListener(event -> onGenerateVikings());
        bottomPanel.add(generateButton);

        add(bottomPanel, BorderLayout.SOUTH);

        onInit();
    }

    private void onCreateViking() {
        Viking viking = vikingService.createRandomViking();
        tableModel.addViking(viking);
    }

    private void onOpenAnalytics() {
        VikingAnalyticsFrame analyticsFrame =
                new VikingAnalyticsFrame(analyticsService);
        analyticsFrame.setVisible(true);
    }


    private void onGenerateVikings() {
        String input = JOptionPane.showInputDialog(
                this,
                "Введите количество викингов для генерации:",
                "Генерация викингов",
                JOptionPane.QUESTION_MESSAGE
        );

        // Пользователь нажал Отмена
        if (input == null) return;

        try {
            int count = Integer.parseInt(input.trim());

            if (count <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Число должно быть больше 0",
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<Viking> generated = vikingService.generateAndSaveVikings(count);

            // Добавляем каждого в таблицу гуи
            generated.forEach(tableModel::addViking);

            JOptionPane.showMessageDialog(this,
                    "Успешно создано викингов: " + generated.size(),
                    "Готово",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Пожалуйста, введите целое число",
                    "Ошибка ввода",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void addNewViking(Viking viking) {
        tableModel.addViking(viking);
    }

    public void removeViking(int id) {
        SwingUtilities.invokeLater(() -> {
            int rowIndex = tableModel.findRowById(id);
            if (rowIndex == -1) {
                System.out.println("removeViking: строка с id=" + id + " не найдена");
                return;
            }
            tableModel.removeViking(rowIndex);
        });
    }

    public void updateViking(Viking viking) {
        SwingUtilities.invokeLater(() -> {
            if (viking.id() == null) {
                System.out.println("updateViking: viking.id() == null");
                return;
            }
            int rowIndex = tableModel.findRowById(viking.id());
            if (rowIndex == -1) {
                System.out.println("updateViking: строка с id=" + viking.id() + " не найдена");
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