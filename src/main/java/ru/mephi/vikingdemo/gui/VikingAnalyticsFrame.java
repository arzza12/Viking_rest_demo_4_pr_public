package ru.mephi.vikingdemo.gui;

import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.VikingAnalyticsService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class VikingAnalyticsFrame extends JFrame {

    private final VikingAnalyticsService analyticsService;

    private final JTextArea resultArea = new JTextArea(6, 40);
    private final JLabel resultLabel = new JLabel("Окно результатов аналитик");
    private final DefaultTableModel redBeardsTableModel =
            new DefaultTableModel(new String[]{"Имя", "Возраст"}, 0);

    public VikingAnalyticsFrame(VikingAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;

        setTitle("Viking Analytics");
        setSize(new Dimension(700, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // закрыть только это окно
        setLayout(new BorderLayout(10, 10));

        JLabel header = new JLabel("Аналитика викингов", SwingConstants.CENTER);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 16f));
        add(header, BorderLayout.NORTH);

        // все кнопки + результаты
        add(buildCenterPanel(), BorderLayout.CENTER);
    }

    // Собирает центральную панель с кнопками и областями результатов
    private JPanel buildCenterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        panel.add(buildSectionLabel("1. Случайный викинг ростом > 180 см"));
        JButton tallButton = new JButton("Найти случайного высокого викинга");
        tallButton.addActionListener(e -> onFindTallViking());
        panel.add(tallButton);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setBorder(BorderFactory.createTitledBorder("Результат"));
        panel.add(new JScrollPane(resultArea));

        panel.add(Box.createVerticalStrut(10));

        panel.add(buildSectionLabel("2. Викинги с легендарным снаряжением"));
        JButton legendaryButton = new JButton("Легендарное снаряжение");
        legendaryButton.addActionListener(e -> onFindLegendaryEquipped());
        panel.add(legendaryButton);

        panel.add(Box.createVerticalStrut(10));

        panel.add(buildSectionLabel("3. Рыжие викинги, отсортированные по возрасту"));
        JButton redBeardsButton = new JButton("Рыжие по возрасту");
        redBeardsButton.addActionListener(e -> onGetRedBeardsSortedByAge());
        panel.add(redBeardsButton);

        // Таблица для рыжих викингов
        JTable redBeardsTable = new JTable(redBeardsTableModel);
        redBeardsTable.setRowHeight(24);
        JScrollPane tableScroll = new JScrollPane(redBeardsTable);
        tableScroll.setPreferredSize(new Dimension(600, 120));
        tableScroll.setBorder(BorderFactory.createTitledBorder("Рыжие викинги"));
        panel.add(tableScroll);

        panel.add(Box.createVerticalStrut(10));

        panel.add(buildSectionLabel("4. Операции с ID"));
        JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton maxIdButton = new JButton("Макс. ID");
        maxIdButton.addActionListener(e -> onFindMaxId());
        idPanel.add(maxIdButton);

        JButton evenIdsButton = new JButton("Чётные ID");
        evenIdsButton.addActionListener(e -> onFindEvenIds());
        idPanel.add(evenIdsButton);

        panel.add(idPanel);

        // Метка для результатов ид операций
        resultLabel.setFont(resultLabel.getFont().deriveFont(13f));
        panel.add(resultLabel);

        return panel;
    }


    private void onFindTallViking() {
        List<Viking> all = analyticsService.getAllVikings();
        Optional<Viking> result = analyticsService.findRandomTallViking(all);

        if (result.isPresent()) {
            Viking v = result.get();
            resultArea.setText(
                    "Имя: " + v.name() + "\n" +
                            "Возраст: " + v.age() + "\n" +
                            "Рост: " + v.heightCm() + " см\n" +
                            "Цвет волос: " + v.hairColor() + "\n" +
                            "Борода: " + v.beardStyle()
            );
        } else {
            resultArea.setText("Викингов ростом > 180 см не найдено.");
        }
    }

    private void onFindLegendaryEquipped() {
        List<Viking> all = analyticsService.getAllVikings();
        List<Viking> result = analyticsService.findLegendaryEquipped(all);

        if (result.isEmpty()) {
            resultArea.setText("Викингов с легендарным снаряжением не найдено.");
        } else {
            // Собираем имена через Stream API — map + joining
            String names = result.stream()
                    .map(Viking::name)
                    .collect(java.util.stream.Collectors.joining(", "));
            resultArea.setText(
                    "Найдено викингов: " + result.size() + "\n" +
                            "Имена: " + names
            );
        }
    }

    private void onGetRedBeardsSortedByAge() {
        List<Viking> all = analyticsService.getAllVikings();
        List<Viking> result = analyticsService.getRedBeardsSortedByAge(all);

        // Очищаем таблицу перед заполнением
        redBeardsTableModel.setRowCount(0);

        if (result.isEmpty()) {
            resultArea.setText("Рыжих викингов не найдено.");
        } else {
            // Заполняем таблицу
            result.forEach(v ->
                    redBeardsTableModel.addRow(new Object[]{v.name(), v.age()})
            );
            resultArea.setText("Найдено рыжих викингов: " + result.size());
        }
    }

    private void onFindMaxId() {
        List<Viking> all = analyticsService.getAllVikings();
        OptionalInt result = analyticsService.findMaxId(all);

        if (result.isPresent()) {
            resultLabel.setText("Максимальный ID: " + result.getAsInt());
        } else {
            resultLabel.setText("Список викингов пуст.");
        }
    }

    private void onFindEvenIds() {
        List<Viking> all = analyticsService.getAllVikings();
        List<Integer> result = analyticsService.findEvenIds(all);

        if (result.isEmpty()) {
            resultLabel.setText("Чётных ID не найдено.");
        } else {
            String ids = result.stream()
                    .map(String::valueOf)
                    .collect(java.util.stream.Collectors.joining(", "));
            resultLabel.setText("Чётные ID: " + ids);
        }
    }

    private JLabel buildSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 12f));
        label.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));
        return label;
    }
}