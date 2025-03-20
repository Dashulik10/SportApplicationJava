package org.example.sportapp.dataBase;

import org.example.sportapp.models.Athlete;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
* Реализация объектно-реляционного доступа к базе данных
* */

public class AthleteDAO {

    public void addAthlete(Athlete athlete) throws SQLException {

        String sql = "INSERT INTO sportsmen (second_name, first_name, patronymic, team_cast, position, title, sport_id, rank)" +
                "VALUES (?, ?, ?, ?::team_cast_enum, ?, ?, ?, ?::rank_enum)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, athlete.getSecond_name());
            statement.setString(2, athlete.getFirst_name());
            statement.setString(3, athlete.getPatronymic());
            statement.setString(4, athlete.getTeam_cast());
            statement.setString(5, athlete.getPosition());
            statement.setInt(6, athlete.getTitle());
            statement.setLong(7, athlete.getSport());
            statement.setString(8, athlete.getRank());

            statement.executeUpdate(); // выполняется запрос
        }
        catch (SQLException exception){
            System.err.println("Ошибка при добавлении! " + exception.getMessage());
        }
    }

    public boolean deleteAthlete (String second_name, String first_name,
                                  String team_cast, Long sport, String rank) throws SQLException {

        StringBuilder sql = new StringBuilder("DELETE FROM sportsmen WHERE ");

        List<Object> parameters = new ArrayList<>();

        if(second_name != null) {
            sql.append("second_name = ? AND ");
            parameters.add(second_name);
        }

        if(first_name != null) {
            sql.append("first_name = ? AND ");
            parameters.add(first_name);
        }

        if(sport != null) {
            sql.append("sport_id = ? AND ");
            parameters.add(sport);
        }

        if (team_cast != null) {
            sql.append("team_cast = ?::team_cast_enum AND ");
            parameters.add(team_cast);
        }

        if(rank != null) {
            sql.append("rank = ?::rank_enum AND ");
            parameters.add(rank);
        }

        if(parameters.isEmpty()) {
            throw new IllegalArgumentException
                    ("Необходимо указать хотя бы одно поле для удаления.");
        }

        sql.setLength(sql.length() - 4);

        try (Connection connection = DatabaseConfig.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }

            int rowDeleted = statement.executeUpdate();
            return rowDeleted > 0;
        }


    }

    public List<Athlete> searchAthletes(String second_name, String first_name, String team_cast,
                                        Long sport, String rank) throws SQLException {

        List<Object> parameters = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT s.id, s.second_name, s.first_name, s.patronymic, s.team_cast, " +
                        "s.position, s.title, s.sport_id, sp.name AS sport_name, s.rank " +
                        "FROM sportsmen s " +
                        "JOIN sports sp ON s.sport_id = sp.id " +
                        "WHERE "
        );

        boolean hasConditions = false; // Для отслеживания добавляемых условий

        if (second_name != null) {
            sql.append("s.second_name = ? AND ");
            parameters.add(second_name);
            hasConditions = true;
        }

        if (first_name != null) {
            sql.append("s.first_name = ? AND ");
            parameters.add(first_name);
            hasConditions = true;
        }

        if (team_cast != null) {
            sql.append("s.team_cast = ?::team_cast_enum AND ");
            parameters.add(team_cast);
            hasConditions = true;
        }

        if (sport != null) {
            sql.append("s.sport_id = ? AND ");
            parameters.add(sport);
            hasConditions = true;
        }

        if (rank != null) {
            sql.append("s.rank = ?::rank_enum AND ");
            parameters.add(rank);
            hasConditions = true;
        }

        if (!hasConditions) {
            sql.setLength(sql.length() - 6); // Убираем "WHERE "
        } else {
            sql.setLength(sql.length() - 4); // Убираем "AND "
        }

        List<Athlete> athletes = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Athlete athlete = new Athlete(
                            resultSet.getLong("id"),
                            resultSet.getString("second_name"),
                            resultSet.getString("first_name"),
                            resultSet.getString("patronymic"),
                            resultSet.getString("team_cast"),
                            resultSet.getString("position"),
                            resultSet.getInt("title"),
                            resultSet.getLong("sport_id"),
                            resultSet.getString("rank")
                    );
                    athletes.add(athlete);
                }
            }
        }

        return athletes;
    }

    //загрузка всех спортсменов в базу, не циклом (для загрузчика ксмл)
    public void addMultipleAthletes(List<Athlete> athletes) throws SQLException {
        String sql = "INSERT INTO sportsmen (second_name, first_name, patronymic, team_cast, position, title, sport_id, rank) " +
                "VALUES (?, ?, ?, ?::team_cast_enum, ?, ?, ?, ?::rank_enum)";

        try (Connection connection = DatabaseConfig.getConnection();
             //Спецом чтобы подготовить и потом можно было замещать значениями, от sql-инъекций
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (Athlete athlete : athletes) {
                statement.setString(1, athlete.getSecond_name());
                statement.setString(2, athlete.getFirst_name());
                statement.setString(3, athlete.getPatronymic());
                statement.setString(4, athlete.getTeam_cast());
                statement.setString(5, athlete.getPosition());
                statement.setInt(6, athlete.getTitle());
                statement.setLong(7, athlete.getSport());
                statement.setString(8, athlete.getRank());

                // Добавляем текущий запрос в пакет
                statement.addBatch();
            }

            // Выполняем все запросы в одном пакете
            // Этот батч загружает в пакет запросов, но не отправляет их немедленно в бд
            // возвращается массив, где каждое число - количество строк, изменённых на сервере
            int[] result = statement.executeBatch();
            System.out.println("Вставлено атлетов: " + result.length);
        } catch (SQLException exception) {
            System.err.println("Ошибка при массовом добавлении атлетов: " + exception.getMessage());
            throw exception;
        }
    }


    public List<String> getAllSports() throws SQLException {
        List<String> sports = new ArrayList<>();
        String sql = "SELECT name FROM sports";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                sports.add(resultSet.getString("name")); // Добавляем название спорта в список
            }
        }

        return sports;
    }

    public Long getSportIdByName(String sportName) throws SQLException {
        String sql = "SELECT id FROM sports WHERE name = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, sportName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong("id");
                }
            }
        }

        throw new SQLException("Вид спорта не найден: " + sportName);
    }


    public String getSportNameById(Long sportId) throws SQLException {
        String sql = "SELECT name FROM sports WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, sportId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("name"); // Возвращаем название вида спорта
                }
            }
        }

        throw new SQLException("Вид спорта с ID " + sportId + " не найден.");
    }

    public Map<String, Map<String, List<Athlete>>> getAthleteTree() throws SQLException {
        String sql = "SELECT s.id, s.second_name, s.first_name, s.patronymic, s.team_cast, " +
                "s.position, s.title, s.rank, sp.name AS sport_name " +
                "FROM sportsmen s " +
                "JOIN sports sp ON s.sport_id = sp.id " +
                "ORDER BY sp.name, s.team_cast, s.second_name";

        // чтобы сохранять порядок добавления видов спорта и составов при построении дерева
        Map<String, Map<String, List<Athlete>>> tree = new LinkedHashMap<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String sportName = resultSet.getString("sport_name");
                String teamCast = resultSet.getString("team_cast");

                // Создаём категории спорта, если их нет
                tree.putIfAbsent(sportName, new LinkedHashMap<>());
                // Создаём категории составов внутри спорта, если их нет
                tree.get(sportName).putIfAbsent(teamCast, new ArrayList<>());

                // Добавляем спортсмена в соответствующую группу
                Athlete athlete = new Athlete(
                        resultSet.getLong("id"),
                        resultSet.getString("second_name"),
                        resultSet.getString("first_name"),
                        resultSet.getString("patronymic"),
                        teamCast,
                        resultSet.getString("position"),
                        resultSet.getInt("title"),
                        null, // Здесь id спорта не нужен для дерева
                        resultSet.getString("rank")
                );

                tree.get(sportName).get(teamCast).add(athlete);
            }
        }

        return tree;
    }


}


