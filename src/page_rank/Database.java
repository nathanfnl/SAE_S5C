package page_rank;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Database {

    public static void insertPageRankIntoDatabase(Connection connection, Map<Integer, Double> pageRankScores, String table) throws SQLException {
        String query = null;
        if(table.equals("page_ranks")) {
            query = "INSERT INTO dblp.page_ranks_test (author_id, page_rank) VALUES (?, ?)";
        }
        else if(table.equals("page_ranks_weight")){
            query = "INSERT INTO dblp.page_ranks_weight (author_id, page_rank) VALUES (?, ?)";
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (Map.Entry<Integer, Double> entry : pageRankScores.entrySet()) {
                int authorId = entry.getKey();
                double pageRank = entry.getValue();

                preparedStatement.setInt(1, authorId);
                preparedStatement.setDouble(2, pageRank);

                preparedStatement.addBatch();
            }

            // Exécutez le lot d'insertions
            preparedStatement.executeBatch();
        }
    }

    public static void insertPageRankAffiliationIntoDatabase(Connection connection, Map<Integer, Double> pageRankScores, int affiliationId, String table) throws SQLException {
        String query = null;
        if(table.equals("page_ranks_aff")) {
            query = "INSERT INTO dblp.page_ranks_aff (author_id, page_rank, affiliation_id) VALUES (?, ?, ?)";
        }
        else if(table.equals("page_ranks_aff_weight")){
            query = "INSERT INTO dblp.page_ranks_aff_weight (author_id, page_rank, affiliation_id) VALUES (?, ?, ?)";
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (Map.Entry<Integer, Double> entry : pageRankScores.entrySet()) {
                int authorId = entry.getKey();
                double pageRank = entry.getValue();

                preparedStatement.setInt(1, authorId);
                preparedStatement.setDouble(2, pageRank);
                preparedStatement.setInt(3, affiliationId);

                preparedStatement.addBatch();
            }

            // Exécutez le lot d'insertions
            try {
                preparedStatement.executeBatch();
            } catch (BatchUpdateException e) {
                e.printStackTrace();
                SQLException nextException = e.getNextException();
                if (nextException != null) {
                    nextException.printStackTrace();
                }
            }
        }
    }

    public static List<Integer> getAffiliations(Connection connection) throws SQLException {
        List<Integer> affiliations = new ArrayList<>();

        String query = "SELECT idaff FROM dblp.affiliation limit 5";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Integer affiliation = resultSet.getInt("idaff");
                affiliations.add(affiliation);
            }
        }

        return affiliations;
    }

    public static Connection getConnection() {

        String url = "jdbc:postgresql://database-etudiants.iut.univ-paris8.fr/";
        String utilisateur = "";
        String motDePasse = "";

        Connection connection = null;

        // Connexion à la base de données
        try {
            connection = DriverManager.getConnection(url, utilisateur, motDePasse);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }


}
