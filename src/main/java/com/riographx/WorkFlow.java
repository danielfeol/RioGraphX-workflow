package com.riographx;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.riographx.dao.SubmitDAO;
import com.riographx.entities.Email;
import com.riographx.entities.Submit;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.api.java.UDF1;
import static org.apache.spark.sql.functions.callUDF;
import static org.apache.spark.sql.functions.asc;
import static org.apache.spark.sql.functions.desc;
import parsii.eval.Expression;
import parsii.eval.Parser;
import parsii.eval.Scope;
import parsii.tokenizer.ParseException;
import java.util.concurrent.TimeUnit;

public class WorkFlow implements VoidFunction {
    // Variables needed for the template
    private Graph6 g,gc;                // graph    
    Dataset<Row> top;
    private static int order;
    private static int counter;
    List<Row> graphList;
    public static final String ReportDir = "/opt/spark-data/reports/";
    public static final String DataDir = "/opt/spark-data/";
    public static final String ImageDir = "/opt/spark-data/images/";
    
    
    public Dataset<Row> run(String inputFileName, 
            int id_submit,
            String latex, 
            int max_results,
            int min_order,
            int max_order,
            int min_degree,
            int max_degree,            
            boolean lookformax,
            boolean triangle_free,
            boolean connected,
            boolean bipartite,
            SparkSession spark) throws IOException, DocumentException {
        
        long startTime = System.currentTimeMillis();               // Take a note of starting time
        counter = 0;                                               // Initialise counter
        
        String latex1;
        String latex_original = latex;
        
        /*########Load Dataset##################*/ 
        Dataset<Row> graphs;
        if (max_order == 11){
            graphs = spark.read().format("csv").load(DataDir + "11/graphseleven.csv").toDF("grafo","ordem","grauminimo","graumaximo","trianglefree","conexo","bipartite");
        }else{
            graphs = spark.read().format("csv").load(inputFileName).toDF("grafo","ordem","grauminimo","graumaximo","trianglefree","conexo","bipartite");
            //graphs = spark.read().format("csv").load("gs://riographx.appspot.com/graphs.csv").toDF("grafo","ordem","grauminimo","graumaximo","trianglefree","conexo","bipartite");
        }        
                    
        graphs = graphs.filter("ordem <= " + max_order)
                .filter("ordem >= " + min_order)
                .filter("grauminimo >= " + min_degree)
                .filter("graumaximo <= " + max_degree);
        
        if(triangle_free){
            graphs = graphs.filter("trianglefree == 1");
        }
        if(connected){
            graphs = graphs.filter("conexo == 1");
        }
        if(bipartite){
            graphs = graphs.filter("bipartite == 1");
        }        
        
        latex = latex.replace("\\", "");
        latex = latex.replace("{", "");
        latex = latex.replace("}", "");
        latex = latex.replace(" ", "");
        latex = latex.replace("overline", "bar");        
        latex1 = latex;
        latex = latex.replace("(", "");
        latex = latex.replace(")", "");
        String[] s = latex.split("[+-/*]");
        
        /*#####opt_function########*/ 
        spark.udf().register("eigenValue", new UDF1<String, Double>() {
            private static final long serialVersionUID = -5372447039252716846L;
            
            @Override
            public Double call(String grafo) throws ParseException {
            String[] tempValue;
            double eigen;   
            g = new Graph6(grafo,0);
            gc = new Graph6(grafo,1);// create a graph out of its g6 code
            Scope scope = new Scope();
            double[] x = {};

            for (String a : s){
                if (a.contains("bar")){
                   if(a.contains("lambda")){
                       tempValue = a.split("_");
                       if (tempValue[1].contains("n")){
                       eigen = gc.Aspectrum()[0];    
                       }else{
                       eigen = gc.Aspectrum()[gc.Aspectrum().length-Integer.parseInt(tempValue[1])];}
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;
                       continue;
                   }
                   if(a.contains("mu")){
                       tempValue = a.split("_");
                       if (tempValue[1].contains("n")){
                       eigen = gc.Lspectrum()[0];    
                       }else{
                       eigen = gc.Lspectrum()[gc.Lspectrum().length-Integer.parseInt(tempValue[1])];}
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;               
                       continue;
                   }
                   if(a.contains("q")){
                       tempValue = a.split("_");
                       if (tempValue[1].contains("n")){
                       eigen = gc.Qspectrum()[0];    
                       }else{
                       eigen = gc.Qspectrum()[gc.Qspectrum().length-Integer.parseInt(tempValue[1])];}
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;    
                       continue;
                   }
                    if(a.contains("alpha")){
                       tempValue = a.split("_");
                       if (tempValue[1].contains("n")){
                       eigen = gc.SQDspectrum()[0];    
                       }else{
                       eigen = gc.SQDspectrum()[gc.SQDspectrum().length-Integer.parseInt(tempValue[1])];}
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;               
                       continue;
                   }
                    if(a.contains("beta")){
                       tempValue = a.split("_");
                       if (tempValue[1].contains("n")){
                       eigen = gc.Zspectrum()[0];    
                       }else{
                       eigen = gc.Zspectrum()[gc.Zspectrum().length-Integer.parseInt(tempValue[1])];}
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;               
                       continue;
                   }                    
                    if(a.contains("gamma")){
                       tempValue = a.split("_");
                       if (tempValue[1].contains("n")){
                       eigen = gc.QDspectrum()[0];    
                       }else{
                       eigen = gc.QDspectrum()[gc.QDspectrum().length-Integer.parseInt(tempValue[1])];}
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;               
                       continue;
                   }
                    if(a.contains("rho")){
                       tempValue = a.split("_");
                       if (tempValue[1].contains("n")){
                       eigen = gc.Dspectrum()[0];    
                       }else{
                       eigen = gc.Dspectrum()[gc.Dspectrum().length-Integer.parseInt(tempValue[1])];}
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;               
                       continue;
                   }
                   if(a.contains("ABC_GG")){
                       eigen = gc.ZGGindex();
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;               
                       continue;
                   }
                   if(a.contains("ABC")){
                       eigen = gc.Zindex();
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;               
                       continue;
                   }                   
                   if(a.contains("chi")){
                       eigen = gc.chromaticNumber();
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;               
                       continue;
                   }
                   if(a.contains("omega")){
                       eigen = gc.cliqueNumber();
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;               
                       continue;
                   }                   
                   if(a.contains("SLE_D")){
                       eigen = gc.SQDenergy();
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;               
                       continue;
                   }
                   if(a.contains("LE_D")){
                       eigen = gc.QDenergy();
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;               
                       continue;
                   }
                   if(a.contains("E_D")){
                       eigen = gc.Denergy();
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;               
                       continue;
                   }                   
                   if(a.contains("SLE")){
                       eigen = gc.Qenergy();
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;               
                       continue;
                   }
                   if(a.contains("LE")){
                       eigen = gc.Lenergy();
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;               
                       continue;
                   }
                   if(a.contains("E")){
                       eigen = gc.Aenergy();
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;               
                       continue;
                   }                   
                   if(a.contains("d_")){
                       tempValue = a.split("_");
                       eigen = gc.degrees()[gc.degrees().length-Integer.parseInt(tempValue[1])];
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;     
                   }                   
                }else{
                   if(a.contains("lambda")){
                       tempValue = a.split("_");
                       if (tempValue[1].contains("n")){
                       eigen = g.Aspectrum()[0];    
                       }else{
                       eigen = g.Aspectrum()[g.Aspectrum().length-Integer.parseInt(tempValue[1])];}
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;                
                       continue;
                   }
                   if(a.contains("mu")){
                       tempValue = a.split("_");
                       if (tempValue[1].contains("n")){
                       eigen = g.Lspectrum()[0];    
                       }else{
                       eigen = g.Lspectrum()[g.Lspectrum().length-Integer.parseInt(tempValue[1])];}
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;     
                       continue;
                   }
                   if(a.contains("q")){
                       tempValue = a.split("_");
                       if (tempValue[1].contains("n")){
                       eigen = g.Qspectrum()[0];    
                       }else{
                       eigen = g.Qspectrum()[g.Qspectrum().length-Integer.parseInt(tempValue[1])];}
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;     
                       continue;
                   }
                    if(a.contains("alpha")){
                       tempValue = a.split("_");
                       if (tempValue[1].contains("n")){
                       eigen = g.SQDspectrum()[0];    
                       }else{
                       eigen = g.SQDspectrum()[g.SQDspectrum().length-Integer.parseInt(tempValue[1])];}
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;         
                       continue;
                   }
                    if(a.contains("beta")){
                       tempValue = a.split("_");
                       if (tempValue[1].contains("n")){
                       eigen = g.Zspectrum()[0];    
                       }else{
                       eigen = g.Zspectrum()[g.Zspectrum().length-Integer.parseInt(tempValue[1])];}
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;               
                       continue;
                   }                     
                    if(a.contains("gamma")){
                       tempValue = a.split("_");
                       if (tempValue[1].contains("n")){
                       eigen = g.QDspectrum()[0];    
                       }else{
                       eigen = g.QDspectrum()[g.QDspectrum().length-Integer.parseInt(tempValue[1])];}
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;               
                       continue;
                   }
                    if(a.contains("rho")){
                       tempValue = a.split("_");
                       if (tempValue[1].contains("n")){
                       eigen = g.Dspectrum()[0];    
                       }else{
                       eigen = g.Dspectrum()[g.Dspectrum().length-Integer.parseInt(tempValue[1])];}
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;               
                       continue;
                   }
                   if(a.contains("ABC_GG")){
                       eigen = g.ZGGindex();
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;               
                       continue;
                   }
                   if(a.contains("ABC")){
                       eigen = g.Zindex();
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;               
                       continue;
                   }                    
                   if(a.contains("chi")){
                       eigen = g.chromaticNumber();
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;    
                       continue;
                   }
                   if(a.contains("omega")){
                       eigen = g.cliqueNumber();
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;               
                       continue;
                   }                   
                   if(a.contains("SLE_D")){
                       eigen = g.SQDenergy();
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;               
                       continue;
                   }
                   if(a.contains("LE_D")){
                       eigen = g.QDenergy();
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;               
                       continue;
                   }
                   if(a.contains("E_D")){
                       eigen = g.Denergy();
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;               
                       continue;
                   }                   
                   if(a.contains("SLE")){
                       eigen = g.Qenergy();
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;               
                       continue;
                   }
                   if(a.contains("LE")){
                       eigen = g.Lenergy();
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;               
                       continue;
                   }
                   if(a.contains("E")){
                       eigen = g.Aenergy();
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;               
                       continue;
                   } 
                   if(a.contains("d_")){
                       tempValue = a.split("_");
                       if (tempValue[1].contains("n")){
                       eigen = g.degrees()[0];    
                       }else{
                       eigen = g.degrees()[g.degrees().length-Integer.parseInt(tempValue[1])];}
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;      
                       continue;
                   }
                   if(a.contains("n")){
                       eigen = g.n();
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;      
                       continue;
                   }
                    if(a.contains("m")){
                       eigen = g.m();
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = eigen;
                   }else{
                       x = Arrays.copyOf(x, x.length + 1);
                       x[x.length -1 ] = Double.parseDouble(a);
                    }
                }
            }
            for (int i = 0; i < x.length; i++){
                scope.create(s[i]).setValue(x[i]);

            }
            DecimalFormat df = new DecimalFormat("#.####");
            Expression expr = Parser.parse(latex1, scope); 
            return Double.parseDouble(df.format(expr.evaluate()));
        }
	}, DataTypes.DoubleType);
        
        /*#####Execution of opt_function########*/ 
        graphs = graphs.withColumn(latex_original, callUDF("eigenValue",graphs.col("grafo")));
        
        /*#####Drop NULL results########*/ 
        graphs = graphs.na().drop();
        
        /*#####Sort results#####################*/ 
        if (lookformax){
            graphs = graphs.orderBy(desc(latex_original));
            if (max_order < 11){
                graphs = graphs.cache();
            }
        }
        else{
            graphs = graphs.orderBy(asc(latex_original));
            if (max_order < 11){
                graphs = graphs.cache();
            }
        }

        /*#####Create PDF file######*/ 
        Document document = new Document();
        PDF.PDF(document, id_submit);
        document.open();        
        try {
            PDF.addTitlePage(document,id_submit,
                    latex_original,
                    lookformax,
                    triangle_free,
                    connected,
                    bipartite);
        } catch (DocumentException ex) {
            Logger.getLogger(WorkFlow.class.getName()).log(Level.SEVERE, null, ex);
        }        
        /*#####K-best graphs for each order######*/ 
        if (lookformax){
            graphList = graphs.select("*").filter("ordem == " + min_order).limit(max_results).collectAsList();
        }
        else{
            graphList = graphs.select("*").filter("ordem == " + min_order).limit(max_results).collectAsList();
        }
        
        /*#####Dataframe to List and write in PDF file ##################*/ 
        order = min_order + 1;
        makeReport(graphList,document,id_submit,latex_original, max_results);        
                
        while (order <= max_order){
        if (lookformax){
            graphList = graphs.select("*").filter("ordem == " + order).limit(max_results).collectAsList();
        }
        else{
            graphList = graphs.select("*").filter("ordem == " + order).limit(max_results).collectAsList();
        }

        makeReport(graphList,document,id_submit,latex_original,max_results);
        order++;
        } 
        
        /*#####Close PDF file######*/ 
        document.close();
       
        /*#####Copy report to Google Bucket ########*/ 
        MoveToBucket.uploadObject("google-project_id", "google-bucket" ,id_submit + ".pdf", ReportDir + id_submit + ".pdf");
        
        
    
        long totalTime = System.currentTimeMillis() - startTime;        // Report elapsed time
        System.out.println("Time elapsed: " + 
            (totalTime / 60000) + " min, " + ((double) (totalTime % 60000) / 1000) + " sec");
        
        
        return null;
    }
    /*############## Generate reports###############*/        
    public void makeReport(List<Row> graphList, Document document, int id_submit, String latex_original, int max_results)
            throws IOException, DocumentException{
        /*############## Draw Graphs###############*/        
        DrawGraphs draw = new DrawGraphs();
        draw.DrawGraphs(graphList,max_results);
        /*############## Make PDFs ################*/    
        PDF.addContent(document,graphList,id_submit,
        latex_original, 
        max_results);

    }

    public static void main(String[] args) throws IOException, NumberFormatException, MessagingException, DocumentException, InterruptedException {
        int i = 0;        
        final SubmitDAO dao = new SubmitDAO();
        while (i == 0){
        try{
            final Submit sub = dao.getSingle();
            System.out.println("Submissão nº: " + sub.getId_submit());
            dao.setProcessing(sub);
            SparkConf sparkConf = new SparkConf();
            sparkConf.setAppName("Portal RioGraphX v3.0");
            sparkConf.setMaster("spark://spark-master:7077");
            //sparkConf.setMaster("local[28]");            
            sparkConf.set("spark.cores.max", "");
            sparkConf.set("spark.executor.memory", "");
            sparkConf.set("spark.executor.userClassPathFirst","true");
            sparkConf.set("spark.driver.userClassPathFirst","true");
            sparkConf.set("spark.driver.memory", "");            
            try(JavaSparkContext context = new JavaSparkContext(sparkConf)) {            
                context.setLogLevel("ERROR");
                SparkSession spark = new SparkSession( context.sc() );
                try{
                    new WorkFlow().run(DataDir + "graphs.csv",
                            sub.getId_submit(),
                            sub.getFunction(),
                            sub.getMaxresults(),
                            sub.getMin_order(),
                            sub.getMax_order(),
                            sub.getMin_degree(),
                            sub.getMax_degree(),
                            sub.isMin_max(),
                            sub.isTriang_free(),
                            sub.isConnected(),
                            sub.isBipartite(),
                            spark);
                    dao.setProcessed(sub);
                    dao.setFinished(sub);
                    String message = "Dear " + sub.getUsername() + ",<br><br>"
                            + "Your submission number " + sub.getId_submit() +
                            " taken at RioGraphX System is ready to download.<br><br>"
                            + "This submission has the following parameters:<br><br>"
                            + "Objective function: " + sub.getFunction() + "<br>"
                            + "Minimum order: " + sub.getMin_order() + "<br>"
                            + "Maximum order: " + sub.getMax_order() + "<br>"
                            + "Minimum degree: " + sub.getMin_degree() + "<br>"
                            + "Maximum degree: " + sub.getMax_degree() + "<br>"
                            + "Only Tringle free? " + sub.isTriang_free() + "<br>"
                            + "Only Connected? " + sub.isConnected() + "<br>"
                            + "Only Bipartite? " + sub.isBipartite() + "<br><br>"
                            + "Best regards and thank you for using our tool<br>"
                            + "RiographX ​​Team";
                    Email.sendAsHtml(sub.getEmail(), "[RioGraphx] - Your results are ready." ,message);
                }catch(Exception e){
                    System.out.println("An error has occurred.");
                    dao.setProcessed(sub);
                    dao.setError(sub);}
                spark.stop();
            }        }catch(Exception e){
        }        
        }

    }

    @Override
    public void call(Object t) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}