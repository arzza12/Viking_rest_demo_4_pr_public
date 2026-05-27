
package ru.mephi.vikingdemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.mephi.vikingdemo.gui.VikingDesktopFrame;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.VikingService;
/**
 *
 * @author test2023
 */

/**
 * После каждой операции обновляет таблицу Swing.
 */
@Component
public class VikingListener {

    private final VikingService service;
    private VikingDesktopFrame gui;

    @Autowired
    public VikingListener(VikingService service) {
        this.service = service;
    }

    public void setGui(VikingDesktopFrame gui) {
        this.gui = gui;
    }

    // Создаёт случайного викинга и добавляет его в таблицу GUI
    void testAdd() {
        gui.addNewViking(service.createRandomViking());
    }

    /**
     * Вызывается контроллером после успешного POST /api/vikings.
     * Добавляет нового викинга в таблицу GUI.
     */
    public void onVikingAdded(Viking viking) {
        if (gui == null) {
            System.out.println("onVikingAdded: GUI ещё не инициализирован");
            return;
        }
        gui.addNewViking(viking);
    }

    /**
     * Вызывается контроллером после успешного DELETE /api/vikings/{id}.
     * Удаляет строку с викингом из таблицы GUI.
     */
    public void onVikingDeleted(int id) {
        if (gui == null) {
            System.out.println("onVikingDeleted: GUI ещё не инициализирован");
            return;
        }
        gui.removeViking(id);
    }

    /**
     * Вызывается контроллером после успешного PATCH /api/vikings/{id}.
     * Обновляет строку викинга в таблице GUI.
     */
    public void onVikingUpdated(Viking viking) {
        if (gui == null) {
            System.out.println("onVikingUpdated: GUI ещё не инициализирован");
            return;
        }
        gui.updateViking(viking);
    }
}