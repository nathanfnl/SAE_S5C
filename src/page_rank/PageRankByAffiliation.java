package page_rank;

import edu.uci.ics.jung.graph.UndirectedSparseGraph;

import java.sql.*;
import java.util.List;
import java.util.Map;

import static page_rank.Database.*;
import static page_rank.UtilGraph.*;

public class PageRankByAffiliation {

    public static void main(String[] args) {
        try {
            // Connexion à la base de données
            Connection connection = getConnection();

            // Récupérer la liste des affiliations
            List<Integer> affiliations = getAffiliations(connection);

            for(Integer aff: affiliations) {

                // Création des graphes
                UndirectedSparseGraph<Integer, String> authorGraph = buildAuthorGraphAffiliation(connection, aff);
                UndirectedSparseGraph<Integer, String> authorGraphWithWeight = buildAuthorGraphAffiliationWithWeight(connection, aff);

                // Calculer le PageRank du graphe
                //Map<Integer, Double> pageRankScores = calculatePageRank(authorGraph, 0.15, 5);
                //Map<Integer, Double> pageRankScoresWithWeight = calculatePageRank(authorGraphWithWeight, 0.15, 5);

                // Afficher les scores page_rank.PageRank
                //System.out.println("PageRank Scores: " + pageRankScores);
                //System.out.println("PageRankWithWeight Scores: " + pageRankScoresWithWeight);

                // Insérer les scores PageRank dans la base de données
                //insertPageRankAffiliationIntoDatabase(connection, pageRankScores, aff, "page_ranks_aff");
                //insertPageRankAffiliationIntoDatabase(connection, pageRankScoresWithWeight, aff, "page_ranks_aff_weight");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }









}
