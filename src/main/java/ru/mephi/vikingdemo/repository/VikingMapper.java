package ru.mephi.vikingdemo.repository;

import org.springframework.stereotype.Component;
import ru.mephi.vikingdemo.dto.VikingCreateRequest;
import ru.mephi.vikingdemo.model.EquipmentItem;
import ru.mephi.vikingdemo.model.EquipmentItemEntity;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.model.VikingEntity;

import java.util.List;

@Component
public class VikingMapper {

    // Viking в VikingEntity (для сохранения в БД)
    // id = null, потому что БД сгенерирует его сама
    public VikingEntity toVikingEntity(Viking viking) {
        return new VikingEntity(
                null,
                viking.name(),
                viking.age(),
                viking.heightCm(),
                viking.hairColor(),
                viking.beardStyle(),
                ""
        );
    }

    // EquipmentItem + vikingId → EquipmentItemEntity (для сохранения в БД)
    public EquipmentItemEntity toEquipmentItemEntity(Integer vikingId, EquipmentItem item) {
        return new EquipmentItemEntity(
                null,
                vikingId,
                item.name(),
                item.quality()
        );
    }

    // EquipmentItemEntity в EquipmentItem (для API-ответа)
    public EquipmentItem toEquipmentItem(EquipmentItemEntity entity) {
        return new EquipmentItem(
                entity.name(),
                entity.quality()
        );
    }

    // VikingEntity + список снаряжения (для API-ответа и GUI)
    // добавлен entity.id() первым аргументом (после добавления id в record Viking)
    public Viking toViking(VikingEntity entity, List<EquipmentItemEntity> equipmentEntities) {
        List<EquipmentItem> equipment = equipmentEntities.stream()
                .map(this::toEquipmentItem)
                .toList();

        return new Viking(
                entity.id(),      // было пропущено, теперь передаём id из БД
                entity.name(),
                entity.age(),
                entity.heightCm(),
                entity.hairColor(),
                entity.beardStyle(),
                equipment
        );
    }

    // VikingCreateRequest Viking (для передачи в storage.save())
    public Viking toVikingFromCreateRequest(VikingCreateRequest request) {
        return new Viking(
                null,                    // id присвоит БД после сохранения
                request.name(),
                request.age(),
                request.heightCm(),
                request.hairColor(),
                request.beardStyle(),
                request.equipment() != null ? request.equipment() : List.of()
        );
    }
}