//package Clustering;
//
//import java.util.Collection;
//
//public class kMeansDistantSilhouette
//{
//
//    private static kMeansDistantSilhouette SINGLETON = null;
//
//    public static kMeansDistantSilhouette getInstance()
//    {
//        if(SINGLETON == null)
//        {
//            SINGLETON = new kMeansDistantSilhouette();
//        }
//        return SINGLETON;
//    }
//
//    private kMeansDistantSilhouette()
//    {
//
//    }
//
//    public DistantDataCluster[] doBestKMeans(int minK, int maxK,Collection<DistantDatum> distantData)
//    {
//        DistantDataCluster[] bestClusters = null;
//        double bestSilhouette = -2;
//        for(int k = minK; k <= maxK; k++)
//        {
//            kMeansDistant kMD = new kMeansDistant(k, distantData);
//            DistantDataCluster[] clusters = kMD.calculateClusters();
//            double silhouette = calculateSilhouette(clusters);
//            if(silhouette > bestSilhouette)
//            {
//                bestSilhouette = silhouette;
//                bestClusters = clusters;
//            }
//        }
//        return bestClusters;
//    }
//
//    private doubleAQ calculateSilhouette(DistantDataCluster[] clusters)
//    {
//        double similarity = calculateSimilarity(clusters);
//        double dissimilarity = calculateDissimilarity(clusters);
//        if(similarity > 0 || dissimilarity > 0)
//        {
//            return (similarity - dissimilarity) / Math.max(similarity, dissimilarity);
//        } else
//        {
//            return -1;
//        }
//    }
//
////    private double calculateSimilarity(DistantDataCluster[] clusters)
////    {
////
////    }
////
////    private double calculateIndividualSimilarity(DistantDatum dataPoint, DistantDataCluster cluster)
////    {
////
////    }
////
////    private double calculateDissimilarity(DistantDataCluster[] clusters)
////    {
////
////    }
////
////    private double calculateSingleDissimilarity(DistantDatum dataPoint, DistantDataCluster memberCluster, DistantDataCluster[] clusters)
////    {
////
////    }
//
//}
