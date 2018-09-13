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
        float[][] input = new float[services.size()][17];
        for (int s = 0; s < services.size(); s++) {
            input[s][0] = services.get(s).getExecType().hashCode();
            input[s][1] = services.get(s).getExecPorts().length;
            input[s][2] = services.get(s).getCategory().getCpu().hashCode();
            input[s][3] = services.get(s).getCategory().getMemory().hashCode();
            input[s][4] = services.get(s).getCategory().getStorage().hashCode();
            input[s][5] = services.get(s).getCategory().getDisk().hashCode();
            if (services.get(s).getCategory().isTemperature())
                input[s][6] = 1;
            if (services.get(s).getCategory().isInclinometer())
                input[s][7] = 1;
            if (services.get(s).getCategory().isJammer())
                input[s][8] = 1;
            if (services.get(s).getCategory().isLocation())
                input[s][9] = 1;
            if (services.get(s).getCategory().isBatteryLevel())
                input[s][10] = 1;
            if (services.get(s).getCategory().isDoorSensor())
                input[s][11] = 1;
            if (services.get(s).getCategory().isPumpSensor())
                input[s][12] = 1;
            if (services.get(s).getCategory().isAccelerometer())
                input[s][13] = 1;
            if (services.get(s).getCategory().isHumidity())
                input[s][14] = 1;
            if (services.get(s).getCategory().isAirPressure())
                input[s][15] = 1;
            if (services.get(s).getCategory().isIrMotion())
                input[s][16] = 1;
        }
        return input;
    }

    public List<Point> generatePoints(float[][] input) {
        INDArray serviceINDArray = Nd4j.create(input);
        return Point.toPoints(serviceINDArray);
    }

    public ClusterSet run(List<Point> points) {
        return kMeansClustering.applyTo(points);
    }
}
