package org.kmf.experiment;

import org.mwg.*;
import org.mwg.struct.Relation;
import org.mwg.task.ActionFunction;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;

import static org.mwg.core.task.Actions.*;

/**
 * Created by bogdan.toader on 05/12/16.
 */
public class Example {
    public final static String USERNAME = "username";
    public final static String PRODUCTNAME = "productname";
    public final static String USER_PRODUCT_REL = "buy";
    public final static String INDEX = "allusers";

    public static void main(String[] arg) {
        final Graph g = new GraphBuilder().withMemorySize(1000000).build();
        g.connect(connectionResult -> {
            final Node user1 = g.newNode(0, 0);
            final Node user2 = g.newNode(0, 0);
            final Node user3 = g.newNode(0, 0);

            user1.set(USERNAME, Type.STRING, "assaad");
            user2.set(USERNAME, Type.STRING, "bogdan");
            user3.set(USERNAME, Type.STRING, "mioara");


            final Node product1 = g.newNode(0, 0);
            final Node product2 = g.newNode(0, 0);
            final Node product3 = g.newNode(0, 0);

            product1.set(PRODUCTNAME, Type.STRING, "water");
            product2.set(PRODUCTNAME, Type.STRING, "tea");
            product3.set(PRODUCTNAME, Type.STRING, "coffee");

            user1.addToRelation(USER_PRODUCT_REL,product1);
            user1.addToRelation(USER_PRODUCT_REL,product2);



            user1.travelInTime(10, new Callback<Node>() {
                @Override
                public void on(Node result) {
                    Relation relx = (Relation) result.getOrCreate(USER_PRODUCT_REL, Type.RELATION);
                    relx.clear();
                    relx.add(product3.id());

                }
            });


            user2.addToRelation(USER_PRODUCT_REL,product3);
            user2.addToRelation(USER_PRODUCT_REL,product2);


            user3.addToRelation(USER_PRODUCT_REL,product1);
            user3.addToRelation(USER_PRODUCT_REL,product2);
            user3.addToRelation(USER_PRODUCT_REL,product3);



            g.index(0, 0, INDEX, new Callback<NodeIndex>() {
                @Override
                public void on(NodeIndex result) {
                    result.addToIndex(user1);
                    result.addToIndex(user2);
                    result.addToIndex(user3);
                }
            });

            ///----  here let's imagine we closed the database

            Task navigation = newTask()
                    .then(setTime("0"))
                    .then(readGlobalIndexAll(INDEX)) //here we get the list of all users
                    .forEach(newTask()
                            .then(defineAsVar("user")) //we saved the user in a var called user
                            .then(attribute(USERNAME)) //we get the name of the user
                            .then(defineAsVar("username")) //we save it to username var ->"bogdan"
                            .then(readVar("user"))
                            .then(traverse(USER_PRODUCT_REL))
                            .forEach(newTask()
                                    .then(defineAsVar("product"))
                                    .then(attribute(PRODUCTNAME))
                                    .then(defineAsVar("productname"))
                                    .thenDo(new ActionFunction() {
                                        @Override
                                        public void eval(TaskContext context) {
                                            System.out.println("User: " + context.variable("username").get(0) + " buy: " + context.variable("productname").get(0));
                                            context.continueTask();
                                        }
                                    })
                            )
                            .then(println("--"))

                    );

            navigation.execute(g,null);


        });

    }
}
