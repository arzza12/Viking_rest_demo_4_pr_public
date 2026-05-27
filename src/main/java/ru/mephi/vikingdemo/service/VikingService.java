package ru.mephi.vikingdemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mephi.vikingdemo.dto.VikingCreateRequest;
import ru.mephi.vikingdemo.dto.VikingUpdateRequest;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.repository.VikingMapper;
import ru.mephi.vikingdemo.repository.VikingStorage;

import java.util.List;

@Service
public class VikingService {
    //каждый раз при изменении создается новая копия

    private final VikingFactory vikingFactory;
    private final VikingStorage vikingStorage;
    private final VikingMapper vikingMapper;

    @Autowired
    public VikingService(
            VikingFactory vikingFactory,
            VikingStorage vikingStorage,
            VikingMapper vikingMapper   // добавлен для toVikingFromCreateRequest()
    ) {
        this.vikingFactory = vikingFactory;
        this.vikingStorage = vikingStorage;
        this.vikingMapper = vikingMapper;
    }

    public List<Viking> findAll() {
        return vikingStorage.findAll();
    }

    public Viking createRandomViking() {
        Viking viking = vikingFactory.createRandomViking();
        return vikingStorage.save(viking);
    }

    /**
     * Создаёт викинга с конкретными параметрами из запроса
     * Маппер превращает DTO в Viking, storage сохраняет в БД и возвращает с id
     */
    public Viking addViking(VikingCreateRequest request) {
        // Превращаем DTO запроса в модель Viking (id = null — БД присвоит сама)
        Viking viking = vikingMapper.toVikingFromCreateRequest(request);
        // Сохраняем и получаем обратно Viking уже с id из БД
        return vikingStorage.save(viking);
    }

    /**
     * Частично обновляет викинга: только те поля, что не null в запросе.
     * Возвращает обновлённый Viking для ответа Клиенту и обновления GUI.
     */
    public Viking update(int id, VikingUpdateRequest request) {
        return vikingStorage.update(id, request);
    }

    public void deleteById(int id) {
        vikingStorage.deleteById(id);
    }
}