package ru.mephi.vikingdemo.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.mephi.vikingdemo.dto.VikingUpdateRequest;
import ru.mephi.vikingdemo.model.BeardStyle;
import ru.mephi.vikingdemo.model.HairColor;
import ru.mephi.vikingdemo.model.VikingEntity;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class VikingRepository {

    private final JdbcTemplate jdbcTemplate;

    // RowMapper преобразует строку ResultSet в объект VikingEntity
    private final RowMapper<VikingEntity> vikingRowMapper = (rs, rowNum) ->
            new VikingEntity(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("age"),
                    rs.getInt("height_cm"),
                    HairColor.valueOf(rs.getString("hair_color")),
                    BeardStyle.valueOf(rs.getString("beard_style")),
                    rs.getString("description")
            );

    public VikingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<VikingEntity> findAll() {
        String sql = """
                select id, name, age, height_cm, hair_color, beard_style, description
                from vikings
                order by id
                """;
        return jdbcTemplate.query(sql, vikingRowMapper);
    }

    public Optional<VikingEntity> findById(int id) {
        String sql = """
                select id, name, age, height_cm, hair_color, beard_style, description
                from vikings
                where id = ?
                """;
        List<VikingEntity> result = jdbcTemplate.query(sql, vikingRowMapper, id);
        return result.stream().findFirst();
    }

    public Integer save(VikingEntity viking) {
        String sql = """
                insert into vikings(name, age, height_cm, hair_color, beard_style, description)
                values (?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, viking.name());
            ps.setInt(2, viking.age());
            ps.setInt(3, viking.heightCm());
            ps.setString(4, viking.hairColor().name());
            ps.setString(5, viking.beardStyle().name());
            ps.setString(6, viking.description());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Не удалось получить id созданного викинга");
        }
        return key.intValue();
    }

    /**
     * Частичное обновление викинга, обновляются только не-null поля запроса, добавляем SETчасть только для переданных полей.
     */
    public void update(int id, VikingUpdateRequest request) {
        // Списки для динамического построения SQL
        List<String> setParts = new ArrayList<>();
        List<Object> params = new ArrayList<>();   // значения для PreparedStatement

        // Добавляем в запрос только те поля, которые не null
        if (request.getName() != null) {
            setParts.add("name = ?");
            params.add(request.getName());
        }
        if (request.getAge() != null) {
            setParts.add("age = ?");
            params.add(request.getAge());
        }
        if (request.getHeightCm() != null) {
            setParts.add("height_cm = ?");
            params.add(request.getHeightCm());
        }
        if (request.getHairColor() != null) {
            setParts.add("hair_color = ?");
            params.add(request.getHairColor().name()); // enum → строка для БД
        }
        if (request.getBeardStyle() != null) {
            setParts.add("beard_style = ?");
            params.add(request.getBeardStyle().name()); // enum → строка для БД
        }

        // Если все поля null, ниче не обновляем
        if (setParts.isEmpty()) {
            return;
        }

        // Собираем финальный SQL
        String sql = "update vikings set " + String.join(", ", setParts) + " where id = ?";
        params.add(id); // id всегда последний параметр (соответствует WHERE id = ?)

        jdbcTemplate.update(sql, params.toArray());
    }

    public void deleteById(int id) {
        String sql = "delete from vikings where id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void deleteAll() {
        jdbcTemplate.update("delete from vikings");
    }
}