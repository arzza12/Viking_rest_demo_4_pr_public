package ru.mephi.vikingdemo.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import ru.mephi.vikingdemo.model.BeardStyle;
import ru.mephi.vikingdemo.model.EquipmentItem;
import ru.mephi.vikingdemo.model.HairColor;

import java.util.List;


@Schema(description = "Запрос на создание викинга")
public record VikingCreateRequest(
        @Schema(description = "Имя викинга", example = "Ragnar", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
        @Schema(description = "Возраст", example = "35", requiredMode = Schema.RequiredMode.REQUIRED)
        int age,
        @Schema(description = "Рост в сантиметрах", example = "180", requiredMode = Schema.RequiredMode.REQUIRED)
        int heightCm,
        @Schema(description = "Цвет волос", example = "Blond", requiredMode = Schema.RequiredMode.REQUIRED)
        HairColor hairColor,
        @Schema(description = "Стиль бороды", example = "LONG", requiredMode = Schema.RequiredMode.REQUIRED)
        BeardStyle beardStyle,
        @ArraySchema(
                schema = @Schema(implementation = EquipmentItem.class),
                arraySchema = @Schema(description = "Список снаряжения (может быть пустым)")
        )
        List<EquipmentItem> equipment  //пустой список []
) {
}