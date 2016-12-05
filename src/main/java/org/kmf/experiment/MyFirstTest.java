package org.kmf.experiment;

import org.mwg.*;
import org.mwg.core.scheduler.TrampolineScheduler;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.algorithm.profiling.GaussianSlotNode;
import org.mwg.structure.StructurePlugin;
import org.mwg.structure.distance.GeoDistance;

/**
 * Created by bogdan.toader on 19/09/16.
 */
public class MyFirstTest {
    public static void main(String[] arg){

        final Graph graph = new GraphBuilder()
                .withMemorySize(1000)
                .withPlugin(new MLPlugin())
                .withPlugin(new StructurePlugin())
                .withScheduler(new TrampolineScheduler())
                .withStorage(new LevelDBStorage("/Users/bogdan.toader/Documents/leveldb"))
                .build();

        graph.connect(new Callback<Boolean>() {
            public void on(Boolean result) {


                Importer csvImporter = new Importer(graph);

                csvImporter.importCsv();



                Node root = graph.newNode(0,0);
                root.set("name", Type.STRING,"root");
                root.set("temperature", Type.DOUBLE, 15.0);
                graph.index(0, 0, "ROOT NODE", new Callback<NodeIndex>() {
                    @Override
                    public void on(NodeIndex result) {
                        result.addToIndex(root,"name");
                    }
                });

                final Node smartwatch =graph.newNode(0,0);
                smartwatch.set("latitude", Type.DOUBLE, 35.6);
                smartwatch.set("longitude", Type.DOUBLE, 6.5);
                smartwatch.set("name", Type.STRING," smartwatch bogdan");


                final Node smartwatch2 =graph.newNode(0,0);
                smartwatch2.set("latitude", Type.DOUBLE, 31.2);
                smartwatch2.set("longitude", Type.DOUBLE, 5.5);
                smartwatch2.set("name", Type.STRING," smartwatch assaad");


                smartwatch2.travelInTime(1000, new Callback<Node>() {
                    public void on(Node result) {
                        result.set("latitude", Type.DOUBLE, 44.0);
                        result.set("longitude", Type.DOUBLE, 45.0);
                    }
                });


                //GaussianSlotNode profiler = (GaussianSlotNode) graph.newTypedNode(0,0,GaussianSlotNode.NAME);



                double[] point1=new double[2];
                double[] point2= new double[2];

                point1[0]=(Double) smartwatch.get("latitude");
                point1[1]=(Double) smartwatch.get("longitude");

                point2[0]=(Double) smartwatch2.get("latitude");
                point2[1]=(Double) smartwatch2.get("longitude");
                System.out.println("distance between p1 and p2 "+ GeoDistance.instance().measure(point1,point2)+ " in meters");

                smartwatch2.travelInTime(1001, new Callback<Node>() {
                    public void on(Node result) {
                        double[] point1=new double[2];
                        double[] point2= new double[2];

                        point1[0]=(Double) smartwatch.get("latitude");
                        point1[1]=(Double) smartwatch.get("longitude");

                        point2[0]=(Double) result.get("latitude");
                        point2[1]=(Double) result.get("longitude");
                        System.out.println("distance between p1 and p2 "+ GeoDistance.instance().measure(point1,point2)+ " in meters");

                    }
                });



                root.addToRelation("watches",smartwatch);
                root.addToRelation("watches",smartwatch2);


                WSServer ws=new WSServer(graph,5678);
                ws.start();
            }
        });




    }
}
