package page_rank;

import edu.uci.ics.jung.graph.UndirectedSparseGraph;

import java.sql.*;
import java.util.Map;

import static page_rank.Database.*;
import static page_rank.UtilGraph.*;

public class PageRank {

    public static void main(String[] args) {
        try {
            // Connexion à la base de données
            Connection connection = getConnection();

            // Création des graphes
            UndirectedSparseGraph<Integer, String> authorGraph = buildAuthorGraph(connection);
            //UndirectedSparseGraph<Integer, String> authorGraphWithWeight = buildAuthorGraphWithWeight(connection);
            System.out.println("Nombre de noeuds: " + authorGraph.getVertexCount());

            // Calculer le PageRank des graphes
            Map<Integer, Double> pageRankScores = calculatePageRank(authorGraph, 0.15, 100);
            //Map<Integer, Double> pageRankScoresWithWeight = calculatePageRank(authorGraphWithWeight, 0.15, 5);

            // Afficher les scores page_rank.PageRank
            System.out.println("PageRank Scores: " + pageRankScores);
            //System.out.println("PageRankWithWeight Scores: " + pageRankScoresWithWeight);

            // Insérer les scores PageRank dans la base de données
            insertPageRankIntoDatabase(connection, pageRankScores, "page_ranks");
            //insertPageRankIntoDatabase(connection, pageRankScoresWithWeight, "page_ranks_weight");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
