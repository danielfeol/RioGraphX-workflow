/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.riographx;

import static com.riographx.WorkFlow.ImageDir;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.spark.sql.Row;

/**
 *
 * @author Spock
 */
public class DrawGraphs implements java.io.Serializable{
    private static final long serialVersionUID = 6L;
    
    public void DrawGraphs(List<Row> graphList, int max_results) throws IOException{
    Row s1 = null;    
    String[] tempArray;
    String a =  null;
    
    for (int i = 0 ; i < max_results; i++){
        
        s1 = graphList.get(i);
        a = s1.toString();        
        tempArray = a.split(",");
       // tempArray[0] = tempArray[0].replace("[", "");
        tempArray[0] = tempArray[0].substring(1, tempArray[0].length());
        Graph6 g6 = new Graph6(tempArray[0],0);
        File tmpDir = new File(ImageDir + tempArray[0]+".png");
        boolean exists = tmpDir.exists();
        if (!exists){
            String dot = g6.printDotFormat();
            MutableGraph g;
            g = new Parser().read(dot);
            Graphviz.fromGraph(g).width(500).render(Format.PNG).toFile(new File(ImageDir + tempArray[0]+".png"));
        }
        }
    }
    
}

       