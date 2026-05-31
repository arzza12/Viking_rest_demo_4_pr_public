package ru.mephi.vikingdemo.service;

import org.springframework.stereotype.Service;
import ru.mephi.vikingdemo.model.BeardStyle;
import ru.mephi.vikingdemo.model.HairColor;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.repository.VikingStorage;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;

@Service
public class VikingAnalyticsService {

    private final VikingStorage vikingStorage;

    public VikingAnalyticsService(VikingStorage vikingStorage) {
        this.vikingStorage = vikingStorage;
    }

    //подсчет объема

    public long countOlderThan(List<Viking> vikings, int minAge) {
        return vikings.stream()
                .filter(v -> v.age() > minAge)
                .count();
    }

    public long countYoungerThan(List<Viking> vikings, int maxAge) {
        return vikings.stream()
                .filter(v -> v.age() < maxAge)
                .count();
    }

    // в опр возрасте
    public long countInAgeRange(List<Viking> vikings, int from, int to) {
        return vikings.stream()
                .filter(v -> v.age() >= from && v.age() <= to)
                .count();
    }


     //ВНЕ возрастного диапазона

    public long countOutsideAgeRange(List<Viking> vikings, int from, int to) {
        return vikings.stream()
                .filter(v -> v.age() < from || v.age() > to)
                .count();
    }


    //Считает викингов с заданным стилем бороды И цветом волос одновременно.
    public long countByBeardAndHair(List<Viking> vikings, BeardStyle beard, HairColor hair) {
        return vikings.stream()
                .filter(v -> v.beardStyle() == beard && v.hairColor() == hair)
                .count();
    }

//1 или 2 топора (одновременно)
    public long countWithAxes(List<Viking> vikings, int axeCount) {
        return vikings.stream()
                .filter(v -> {
                    long axes = v.equipment().stream()
                            .filter(item -> item.name().toLowerCase().contains("axe"))
                            .count();
                    return axes == 1 || axes == 2;
                })
                .count();
    }

    // методы выборки для отображения

    // возвращает викинга больше 180
    public Optional<Viking> findRandomTallViking(List<Viking> vikings) {
        // нужные викинги в новый список
        List<Viking> tallVikings = vikings.stream()
                .filter(v -> v.heightCm() > 180)
                .collect(Collectors.toList());
        java.util.Collections.shuffle(tallVikings);
        return tallVikings.stream().findAny();
    }

    // возвращает викингов с легендаркой
    public List<Viking> findLegendaryEquipped(List<Viking> vikings) {
        return vikings.stream()
                .filter(v -> v.equipment().stream()
                        .anyMatch(item -> "Legendary".equals(item.quality()))
                )
                .collect(Collectors.toList());
    }

    // возвращает отсортированных рыжих + чтоб была борода у  викингов по возрастанию
    public List<Viking> getRedBeardsSortedByAge(List<Viking> vikings) {
        return vikings.stream()
                .filter(v -> v.hairColor() == HairColor.Red
                        && v.beardStyle() != BeardStyle.CLEAN_SHAVEN)
                .sorted(Comparator.comparingInt(Viking::age))
                .collect(Collectors.toList());
    }

    // Операции с массивом ID

    // находит максимальный айди всех викингов

    public OptionalInt findMaxId(List<Viking> vikings) {
        // Объявляем массив явно как локальную переменную
        Integer[] ids = vikings.stream()
                .map(Viking::id)
                .toArray(Integer[]::new);

        return Arrays.stream(ids)
                .mapToInt(Integer::intValue)
                .max();
    }

    //Возвращает список чётных айди викингов

    public List<Integer> findEvenIds(List<Viking> vikings) {
        // Объявляем массив как локал переменную
        int[] ids = vikings.stream()
                .mapToInt(Viking::id)
                .toArray();

        return Arrays.stream(ids)
                .filter(id -> id % 2 == 0)
                .boxed()
                .collect(Collectors.toList());
    }

    //Загружает актуальный список викингов из бд
    public List<Viking> getAllVikings() {
        return vikingStorage.findAll();
    }
}