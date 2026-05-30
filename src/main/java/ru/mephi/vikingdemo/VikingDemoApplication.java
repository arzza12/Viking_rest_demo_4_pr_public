package ru.mephi.vikingdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.mephi.vikingdemo.controller.VikingListener;
import ru.mephi.vikingdemo.gui.VikingDesktopFrame;
import ru.mephi.vikingdemo.service.VikingAnalyticsService;
import ru.mephi.vikingdemo.service.VikingService;

import javax.swing.SwingUtilities;

@SpringBootApplication
public class VikingDemoApplication {

    public static void main(String[] args) {
        System.out.println(java.awt.GraphicsEnvironment.isHeadless());

        SpringApplication app = new SpringApplication(VikingDemoApplication.class);
        app.setHeadless(false);

        ConfigurableApplicationContext context = app.run(args);

        VikingService vikingService = context.getBean(VikingService.class);
        VikingListener vikingListener = context.getBean(VikingListener.class);
        VikingAnalyticsService analyticsService =
                context.getBean(VikingAnalyticsService.class); // ← ДОБАВЛЕНО

        SwingUtilities.invokeLater(() -> {
            VikingDesktopFrame frame =
                    new VikingDesktopFrame(vikingService, analyticsService);
            vikingListener.setGui(frame);
            frame.setVisible(true);
        });
    }
}