package ru.mephi.vikingdemo.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import ru.mephi.vikingdemo.model.BeardStyle;
import ru.mephi.vikingdemo.model.EquipmentItem;
import ru.mephi.vikingdemo.model.HairColor;

import java.util.List;


@Schema(description = "Запрос на частичное обновление викинга")
public class VikingUpdateRequest {

    @Schema(description = "Новое имя (null — не менять)", example = "Bjorn")
    private String name;

    @Schema(description = "Новый возраст (null — не менять)", example = "40")
    private Integer age;

    @Schema(description = "Новый рост в сантиметрах (null — не менять)", example = "190")
    private Integer heightCm;

    @Schema(description = "Новый цвет волос (null — не менять)", example = "Black")
    private HairColor hairColor;

    @Schema(description = "Новый стиль бороды (null — не менять)", example = "BRAIDED")
    private BeardStyle beardStyle;

    @ArraySchema(
            schema = @Schema(implementation = EquipmentItem.class),
            arraySchema = @Schema(description = "Новый список снаряжения (null — не менять)")
    )
    private List<EquipmentItem> equipment;

    // Конструктор без аргументов нужен Jackson для десериализации JSON
    public VikingUpdateRequest() {}

    public String getName() { return name; }
    public Integer getAge() { return age; }
    public Integer getHeightCm() { return heightCm; }
    public HairColor getHairColor() { return hairColor; }
    public BeardStyle getBeardStyle() { return beardStyle; }
    public List<EquipmentItem> getEquipment() { return equipment; }

    public void setName(String name) { this.name = name; }
    public void setAge(Integer age) { this.age = age; }
    public void setHeightCm(Integer heightCm) { this.heightCm = heightCm; }
    public void setHairColor(HairColor hairColor) { this.hairColor = hairColor; }
    public void setBeardStyle(BeardStyle beardStyle) { this.beardStyle = beardStyle; }
    public void setEquipment(List<EquipmentItem> equipment) { this.equipment = equipment; }
}