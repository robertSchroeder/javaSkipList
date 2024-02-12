import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.Iterator;

public class App {
    public static void main(String[] args) throws Exception 
    {
        
        SkipListSet<Double> skipListSet=new SkipListSet<>();
        ArrayList<Double> collection1= new ArrayList<>();
        collection1.add(66.1);
        collection1.add(20.22);
        collection1.add(200.0);
        collection1.add(21.9);

       skipListSet.addAll(collection1);

       


       //System.out.println(skipListSet.isEmpty());
        
        // Double[] array=new Double[skipListSet.size()];

        // array=skipListSet.toArray(array);

        // for(Double item:array)System.out.println(item);



    //     System.out.println("\n"+"\n"+"Second SL that adds from a collection:"+"\n");

    //     TreeSet<String> collection=new TreeSet<>();

    //     collection.add("Baby");
    //     collection.add("Dog");
    //     collection.add("World");
    //     collection.add("Man");

    //     // ArrayList<String> collection2=new ArrayList<>();

    //     // collection2.add("Man");
    //     // collection2.add("Cat");
    //     // collection2.add("Planet");

    //     SkipListSet<String> skipListSet2=new SkipListSet<>();

    //    skipListSet2.addAll(collection);

       

   



      
        // skipListSet2.addAll(collection2);

        //Object[] objArray= skipListSet2.toArray();

        //for(Object item:objArray)System.out.println(item);


        // String[] array=new String[10];

        // array=skipListSet2.toArray(array);

        // for(String item:array)System.out.println(item);

        


    


    }
}
