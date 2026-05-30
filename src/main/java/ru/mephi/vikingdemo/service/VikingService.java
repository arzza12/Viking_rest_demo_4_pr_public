package ru.mephi.vikingdemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.vikingdemo.dto.VikingCreateRequest;
import ru.mephi.vikingdemo.dto.VikingUpdateRequest;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.repository.VikingMapper;
import ru.mephi.vikingdemo.repository.VikingStorage;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class VikingService {

    private final VikingFactory vikingFactory;
    private final VikingStorage vikingStorage;
    private final VikingMapper vikingMapper;

    @Autowired
    public VikingService(
            VikingFactory vikingFactory,
            VikingStorage vikingStorage,
            VikingMapper vikingMapper
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

    public Viking addViking(VikingCreateRequest request) {
        Viking viking = vikingMapper.toVikingFromCreateRequest(request);
        return vikingStorage.save(viking);
    }

    public Viking update(int id, VikingUpdateRequest request) {
        return vikingStorage.update(id, request);
    }

    public void deleteById(int id) {
        vikingStorage.deleteById(id);
    }

    //Генерирует и сохраняет в БД count случайных викингов.

    @Transactional
    public List<Viking> generateAndSaveVikings(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> vikingFactory.createRandomViking()) // генерация случайного викинга
                .map(vikingStorage::save)                          // сохраняем в БД, получаем с id
                .collect(Collectors.toList());                     // собираем результат
    }
}