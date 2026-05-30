package ru.mephi.vikingdemo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.mephi.vikingdemo.dto.VikingCreateRequest;
import ru.mephi.vikingdemo.dto.VikingUpdateRequest;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.VikingService;

import java.util.List;

@RestController
@RequestMapping("/api/vikings")
@Tag(name = "Vikings", description = "Операции с викингами")
public class VikingController {

    private final VikingService vikingService;
    private final VikingListener vikingListener;

    public VikingController(VikingService vikingService, VikingListener vikingListener) {
        this.vikingService = vikingService;
        this.vikingListener = vikingListener;
    }

    @GetMapping
    @Operation(summary = "Получить список всех викингов", operationId = "getAllVikings")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список успешно получен")
    })
    public List<Viking> getAllVikings() {
        System.out.println("GET /api/vikings called");
        return vikingService.findAll();
    }

    @GetMapping("/test")
    @Operation(summary = "Получить список тестовых викингов", operationId = "getTest")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список успешно получен")
    })
    public List<String> test() {
        System.out.println("GET /api/vikings/test called");
        return List.of("Ragnar", "Bjorn");
    }

    @PostMapping("/post")
    @Operation(summary = "Создать викинга со случайными параметрами", operationId = "post")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Викинг успешно создан")
    })
    public void addRandomViking() {
        System.out.println("POST /api/vikings/post called");
        vikingListener.testAdd();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать викинга с заданными параметрами", operationId = "addViking")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Викинг успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректное тело запроса")
    })
    public Viking addViking(@RequestBody VikingCreateRequest request) {
        System.out.println("POST /api/vikings called, name=" + request.name());
        Viking created = vikingService.addViking(request);
        vikingListener.onVikingAdded(created);
        return created;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить викинга по id", operationId = "deleteViking")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Викинг успешно удалён"),
            @ApiResponse(responseCode = "404", description = "Викинг с таким id не найден")
    })
    public void deleteViking(
            @Parameter(description = "ID викинга", example = "1")
            @PathVariable int id
    ) {
        System.out.println("DELETE /api/vikings/" + id + " called");
        vikingService.deleteById(id);
        vikingListener.onVikingDeleted(id);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Частично обновить параметры викинга", operationId = "updateViking")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Викинг успешно обновлён"),
            @ApiResponse(responseCode = "404", description = "Викинг с таким id не найден"),
            @ApiResponse(responseCode = "400", description = "Некорректное тело запроса")
    })
    public Viking updateViking(
            @Parameter(description = "ID викинга", example = "1")
            @PathVariable int id,
            @RequestBody VikingUpdateRequest request
    ) {
        System.out.println("PATCH /api/vikings/" + id + " called");
        Viking updated = vikingService.update(id, request);
        vikingListener.onVikingUpdated(updated);
        return updated;
    }


    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Сгенерировать N случайных викингов",
            operationId = "generateVikings",
            description = "Генерирует и сохраняет в БД указанное количество случайных викингов"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Викинги успешно созданы"),
            @ApiResponse(responseCode = "400", description = "Некорректный параметр count")
    })
    public List<Viking> generateVikings(
            @Parameter(description = "Количество викингов для генерации", example = "5")
            @RequestParam(defaultValue = "1") int count
    ) {
        System.out.println("POST /api/vikings/generate?count=" + count + " called");
        List<Viking> generated = vikingService.generateAndSaveVikings(count);
        // каждый сгенерированный викинг добавляется в таблицу
        generated.forEach(vikingListener::onVikingAdded);
        return generated;
    }
}