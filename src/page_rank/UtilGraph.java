package page_rank;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UtilGraph {

    public static UndirectedSparseGraph<Integer, String> buildAuthorGraphAffiliation(Connection connection, Integer aff) throws SQLException {
        UndirectedSparseGraph<Integer, String> authorGraph = new UndirectedSparseGraph<>();
        String query = "SELECT author_id, coauthor_id FROM dblp.coauthors JOIN dblp.authors ON(author_id=id) WHERE affiliation = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, aff);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int authorId = resultSet.getInt("author_id");
                    int coAuthorId = resultSet.getInt("coauthor_id");

                    addVertexIfNotExists(authorGraph, authorId);
                    addVertexIfNotExists(authorGraph, coAuthorId);
                    addEdgeIfNotExists(authorGraph, authorId, coAuthorId);
                }
            }
        }

        return authorGraph;
    }

    public static UndirectedSparseGraph<Integer, String> buildAuthorGraph(Connection connection) throws SQLException {
        UndirectedSparseGraph<Integer, String> authorGraph = new UndirectedSparseGraph<>();
        String query = "SELECT author_id, coauthor_id FROM dblp.coauthors order by author_id limit 10000";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int authorId = resultSet.getInt("author_id");
                    int coAuthorId = resultSet.getInt("coauthor_id");

                    addVertexIfNotExists(authorGraph, authorId);
                    addVertexIfNotExists(authorGraph, coAuthorId);
                    addEdgeIfNotExists(authorGraph, authorId, coAuthorId);
                }
            }
        }

        return authorGraph;
    }

    private static void addVertexIfNotExists(UndirectedSparseGraph<Integer, String> graph, int vertex) {
        if (!graph.containsVertex(vertex)) {
            graph.addVertex(vertex);
        }
    }

    private static void addEdgeIfNotExists(UndirectedSparseGraph<Integer, String> graph, int source, int target) {
        String edgeName = "Edge_" + Math.min(source, target) + "_" + Math.max(source, target);

        if (!graph.containsEdge(edgeName)) {
            graph.addEdge(edgeName, source, target);
        }
    }


    public static Map<Integer, Double> calculatePageRank(UndirectedSparseGraph<Integer, String> graph, double dampingFactor, int iterations) {
        int numVertices = graph.getVertexCount();
        double teleportProbability = (1.0 - dampingFactor) / numVertices;

        // Initialiser les scores PageRank à 1/N
        Map<Integer, Double> pageRankScores = new HashMap<>();
        for (Integer vertex : graph.getVertices()) {
            pageRankScores.put(vertex, 1.0 / numVertices);
        }

        // Effectuer des itérations pour converger vers les scores finaux
        for (int iteration = 0; iteration < iterations; iteration++) {
            Map<Integer, Double> newPageRankScores = new HashMap<>();

            // Calculer les nouveaux scores pour chaque sommet
            for (Integer vertex : graph.getVertices()) {
                double newScore = teleportProbability;

                // Ajouter la contribution des voisins
                for (Integer neighbor : graph.getNeighbors(vertex)) {
                    int outDegree = graph.getNeighborCount(neighbor);
                    newScore += dampingFactor * (pageRankScores.get(neighbor) / outDegree);
                }

                newPageRankScores.put(vertex, newScore);
            }

            // Mettre à jour les scores PageRank pour la prochaine itération
            pageRankScores = newPageRankScores;
        }

        return pageRankScores;
    }
/*

    public static Map<Integer, Double> calculatePageRank(UndirectedSparseGraph<Integer, String> graph) {
        PageRank<Integer, String> pageRank = new PageRank<>(graph, 0.15); // 0.15 est le facteur d'amortissement (damping factor)
        pageRank.evaluate();

        Map<Integer, Double> pageRankScores = new HashMap<>();
        for (Integer vertex : graph.getVertices()) {
            double score = pageRank.getVertexScore(vertex);
            pageRankScores.put(vertex, score);
        }

        return pageRankScores;
    }
*/

    public static UndirectedSparseGraph<Integer, String> buildAuthorGraphWithWeight(Connection connection) throws SQLException {
        UndirectedSparseGraph<Integer, String> authorGraph = new UndirectedSparseGraph<>();

        // Récupérer les auteurs, les coauteurs et le nombre de publications associées depuis la base de données
        String query = "SELECT author_id, coauthor_id, COUNT(*) AS publication_count " +
                "FROM dblp.coauthors JOIN dblp.publicationauthors USING(author_id)" +
                "GROUP BY author_id, coauthor_id";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int authorId = resultSet.getInt("author_id");
                int coAuthorId = resultSet.getInt("coauthor_id");
                int publicationCount = resultSet.getInt("publication_count");

                // Ajouter un lien entre les auteurs avec le poids du nombre de publications
                addVertexIfNotExists(authorGraph, authorId);
                addVertexIfNotExists(authorGraph, coAuthorId);
                if (!authorGraph.containsEdge("Edge_" + authorId + "_" + coAuthorId) && !authorGraph.containsEdge("Edge_" + coAuthorId + "_" + authorId)) {
                    authorGraph.addEdge("Edge_" + authorId + "_" + coAuthorId + "_Count_" + publicationCount,
                            authorId, coAuthorId);
                }
            }
        }

        return authorGraph;
    }

    public static UndirectedSparseGraph<Integer, String> buildAuthorGraphAffiliationWithWeight(Connection connection, Integer aff) throws SQLException {
        UndirectedSparseGraph<Integer, String> authorGraph = new UndirectedSparseGraph<>();

        // Retrieve authors, coauthors, and publication counts from the database based on affiliation
        String query = "SELECT author_id, coauthor_id, COUNT(*) AS publication_count " +
                "FROM dblp.coauthors " +
                "JOIN dblp.publicationauthors USING(author_id) " +
                "JOIN dblp.authors ON(author_id=id) " +
                "WHERE affiliation = ? " +
                "GROUP BY author_id, coauthor_id";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, aff);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int authorId = resultSet.getInt("author_id");
                    int coAuthorId = resultSet.getInt("coauthor_id");
                    int publicationCount = resultSet.getInt("publication_count");

                    addVertexIfNotExists(authorGraph, authorId);
                    addVertexIfNotExists(authorGraph, coAuthorId);

                    // Add edge with weight based on the number of publications
                    if (!authorGraph.containsEdge("Edge_" + authorId + "_" + coAuthorId) &&
                            !authorGraph.containsEdge("Edge_" + coAuthorId + "_" + authorId)) {
                        authorGraph.addEdge("Edge_" + authorId + "_" + coAuthorId + "_Count_" + publicationCount,
                                authorId, coAuthorId);
                    }
                }
            }
        }

        return authorGraph;
    }

}
