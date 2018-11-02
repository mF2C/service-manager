/**
 * Categorizing module inside the Service Manager
 * Part of the mF2C Project: http://www.mf2c-project.eu/
 * <p>
 * This code is licensed under an Apache 2.0 license. Please, refer to the LICENSE.TXT file for more information
 *
 * @author Francisco Carpio - TUBS
 */
package sm.categorization.learning;

import org.deeplearning4j.clustering.cluster.ClusterSet;
import org.deeplearning4j.clustering.cluster.Point;
import org.deeplearning4j.clustering.kmeans.KMeansClustering;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import sm.elements.Service;

import java.util.List;

public class Clustering {

    private KMeansClustering kMeansClustering;

    public Clustering(int clusterCount, int maxIterationCount) {
        kMeansClustering = KMeansClustering.setup(clusterCount, maxIterationCount, "euclidean");
    }

    public float[][] generateInput(List<Service> services) {
        float[][] input = new float[services.size()][8 + getMaxLengthService(services)];
        for (int s = 0; s < services.size(); s++) {
            input[s][0] = services.get(s).getExecType().hashCode();
            input[s][1] = services.get(s).getExecPorts().length;
            input[s][2] = services.get(s).getAgentType().hashCode();
            if (services.get(s).getCpuArch() != null)
                input[s][3] = services.get(s).getCpuArch().hashCode();
            if (services.get(s).getOs() != null)
                input[s][4] = services.get(s).getOs().hashCode();
            if (services.get(s).getMemoryMin() != 0)
                input[s][5] = services.get(s).getMemoryMin();
            if (services.get(s).getStorageMin() != 0)
                input[s][6] = services.get(s).getStorageMin();
            if (services.get(s).getDisk() != 0)
                input[s][7] = services.get(s).getDisk();
        }
        return input;
    }

    private int getMaxLengthService(List<Service> services) {
        int maxLength = 0;
        for (Service s : services)
            if (s.getReqResource().length + s.getOptResource().length > maxLength)
                maxLength = s.getReqResource().length + s.getOptResource().length;
        return maxLength;
    }

    public List<Point> generatePoints(float[][] input) {
        INDArray serviceINDArray = Nd4j.create(input);
        return Point.toPoints(serviceINDArray);
    }

    public ClusterSet run(List<Point> points) {
        return kMeansClustering.applyTo(points);
    }
}
