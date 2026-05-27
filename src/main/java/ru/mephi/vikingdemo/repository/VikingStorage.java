package ru.mephi.vikingdemo.repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.vikingdemo.dto.VikingUpdateRequest;
import ru.mephi.vikingdemo.model.EquipmentItem;
import ru.mephi.vikingdemo.model.EquipmentItemEntity;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.model.VikingEntity;

@Repository
public class VikingStorage {

    private final VikingRepository vikingRepository;
    private final EquipmentItemRepository equipmentItemRepository;
    private final VikingMapper vikingMapper;

    public VikingStorage(
            VikingRepository vikingRepository,
            EquipmentItemRepository equipmentItemRepository,
            VikingMapper vikingMapper
    ) {
        this.vikingRepository = vikingRepository;
        this.equipmentItemRepository = equipmentItemRepository;
        this.vikingMapper = vikingMapper;
    }

    /**
     * Сохраняет нового викинга и его снаряжение.
     * @Transactional если что-то упадёт, обе операции откатятся вместе.
     * Теперь возвращает Viking с id из БД (не тот, что пришёл на вход).
     */
    @Transactional
    public Viking save(Viking viking) {
        // Сохраняем викинга, получаем сгенерированный БД id
        Integer vikingId = vikingRepository.save(
                vikingMapper.toVikingEntity(viking)
        );

        // Сохраняем каждый предмет снаряжения с привязкой к id викинга
        for (EquipmentItem item : viking.equipment()) {
            equipmentItemRepository.save(
                    vikingMapper.toEquipmentItemEntity(vikingId, item)
            );
        }

        // Возвращаем Viking с id из БД, он нужен GUI и REST-ответу
        return findById(vikingId);
    }

    public List<Viking> findAll() {
        List<VikingEntity> vikingEntities = vikingRepository.findAll();
        List<EquipmentItemEntity> equipmentEntities = equipmentItemRepository.findAll();

        // Группируем снаряжение по id викинга для быстрого доступа
        Map<Integer, List<EquipmentItemEntity>> equipmentByVikingId = equipmentEntities.stream()
                .collect(Collectors.groupingBy(EquipmentItemEntity::vikingId));

        return vikingEntities.stream()
                .map(vikingEntity -> vikingMapper.toViking(
                        vikingEntity,
                        equipmentByVikingId.getOrDefault(vikingEntity.id(), List.of())
                ))
                .toList();
    }

    /**
     * Находит викинга по id вместе со снаряжением, выдает исключение если не найден ( защита от несуществующих id)
     */
    public Viking findById(int id) {
        VikingEntity entity = vikingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Викинг с id=" + id + " не найден"
                ));

        List<EquipmentItemEntity> equipment =
                equipmentItemRepository.findByVikingId(id);

        return vikingMapper.toViking(entity, equipment);
    }

    /**
     * Частичное обновление викинга.
     * @Transactional обновление викинга и снаряжения должны быть неделимыми.
     * Если equipment в запросе не null, тогда полностью заменяем снаряжение.
     */
    @Transactional
    public Viking update(int id, VikingUpdateRequest request) {
        // Обновляем поля самого викинга (только ненулевые поля)
        vikingRepository.update(id, request);

        // Если в запросе передан список снаряжения, то заменяем полностью
        if (request.getEquipment() != null) {
            // Удаляем старое снаряжение
            equipmentItemRepository.deleteByVikingId(id);

            // Сохраняем новое снаряжение
            for (EquipmentItem item : request.getEquipment()) {
                equipmentItemRepository.save(
                        vikingMapper.toEquipmentItemEntity(id, item)
                );
            }
        }

        // Возвращаем актуальное состояние из БД
        return findById(id);
    }

    /**
     * Удаляет викинга по id. Снаряжение удалится автоматически через ON DELETE CASCADE в schema.sql.
     */
    @Transactional
    public void deleteById(int id) {
        vikingRepository.deleteById(id);
    }
}