package com.riographx;

import cern.colt.matrix.*;
import cern.colt.matrix.impl.*;
import cern.colt.matrix.linalg.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Graph6
{
    private int[] bits;         // Sequence of bits reconstructed from g6 code
    private int n;              // number of vertices (order)
    private int m;              // number of edges (size)
    private int[] degree;       // degree sequence
    private int[][] A;          // adjacency matrix
    private int color[];
    long nodes;//numberofdecisions
    long timeLimit;//milliseconds
    long cpuTime;//milliseconds
    int maxSize;//sizeofmaxclique
    int style;//usedtoflavoralgorithm
    int[] solution;//asitsays    
    /**
     * Constructor of a graph from g6 code
     */
    public Graph6(String s, Integer complement) {
        n=s.charAt(0)-63;       // number of vertices is obtained from the first character of g6 code
        int firsti=1;        
        if (s.charAt(0)>=126) {
            n=s.charAt(1)*4096 + s.charAt(2)*64 + s.charAt(3);
            firsti=4;
        }
        
        int bindex = 0;         // transform g6 code characters into bit sequence
        bits = new int[6*s.length()];
        for (int i=firsti; i<s.length(); i++) {
            int k = s.charAt(i)-63;
            for (int j=0; j<=5; j++) {
                bits[bindex+5-j] = k%2;
                k = k/2;
            }
            bindex += 6;
        }
        
        A = new int[n][n];        // initialize adjacency matrix, degree sequence and number of edges
        degree = new int[n];      // indexing always starts at 0  
        for (int i=0; i<n; i++)
            degree[i] = 0;
        m = 0;

        bindex = 0;               // processes bit sequence to fill up adjacency matrix, degree sequence and number of edges
        int bit_value;
        for (int j=1; j<n; j++)
            for (int i=0; i<j; i++) {
                if(complement == 1){
                    if (bits[bindex] == 1){
                        bit_value = 0;
                    }else{
                        bit_value = 1;
                    }
                    
                }else{
                    bit_value = bits[bindex];
                }
                A[i][j] = bit_value;
                A[j][i] = bit_value;

                degree[i] += bit_value;
                degree[j] += bit_value;
                
                m += bit_value;
                
                bindex++;
            }                    
    }
    /**
     *  Constructor of a graph from provided adjacency matrix
     *  May be used to create graph complement or results of other graph operations
     *  Assumption: A is a symmetric, (0,1)-matrix
     */
    public Graph6(int A[][]) {
        this.A = A;                 // adjacency matrix entries do not get copied, 
                                    // only pointer to the matrix gets copied
        n = A.length;               // number of vertices
        
        degree = new int[n];        // initializes degrees and the number of edges
        for (int i=0; i<n; i++)
            degree[i] = 0;
        m = 0;

        for (int i=0; i<n; i++)     // processes degrees and the number of edges
            for (int j=0; j<n; j++) 
                if (A[i][j]==1) {
                    degree[i]++;
                    degree[j]++;
                    m++;
                }
        m = m/2;                    // m was increased twice for each edge, so...
    }
    
    /** 
     * Methods returning values of numbers of vertices, edges, degrees and adjacency matrix
     */
    public int n() {
        return n;
    }

    public int m() {
        return m;
    }
    
    public int[] degrees() {
        int d[] = degree;
        Arrays.sort(d);
        return d;
    }

    public int[][] Amatrix() {
        return A;
    }

    /**
     * Laplacian matrix
     */
    private int[][] L;
    private boolean LExists = false;
    
    public int[][] Lmatrix() {
        if (LExists) 
            return L;
        
        LExists = true;        
        L = new int[n][n];

        for (int i=0; i<n; i++)     // off-diagonal entries are opposite of adjacency matrix entries
            for (int j=0; j<i; j++) {
                L[i][j] = -A[i][j];
                L[j][i] = -A[j][i];
            }
            
        for (int i=0; i<n; i++)     // diagonal entries are equal to corresponding degrees
            L[i][i] = degree[i];
        
        return L;
    }
    
    /** 
     * Signless Laplacian matrix
     */
    private int[][] Q;
    private boolean QExists = false;
    
    public int[][] Qmatrix() {
        if (QExists)
            return Q;
            
        QExists = true;
        Q = new int[n][n];
        
        for (int i=0; i<n; i++)     // off-diagonal entries are equal to adjacency matrix entries
            for (int j=0; j<i; j++) {
                Q[i][j] = A[i][j];
                Q[j][i] = A[j][i];
            }
            
        for (int i=0; i<n; i++)     // diagonal entries are equal to corresponding degrees
            Q[i][i] = degree[i];
            
        return Q;    
    }
    
    /** 
     * Distance matrix by Floyd-Warshall algorithm
     */
    private int[][] D;
    private boolean DExists = false;
    
    public int[][] Dmatrix() {
        if (DExists)
            return D;
            
        DExists = true;
        D = new int[n][n];
        
        for (int i=0; i<n; i++)          // initializes distance matrix
            for (int j=0; j<n; j++)
                if (i==j) D[i][j]=0;
                else if (A[i][j]==1) 
                          D[i][j]=1;
                     else D[i][j]=n;
                     
        for (int k=0; k<n; k++)          // the main loop of the Floyd-Warshall algorithm
            for (int i=0; i<n; i++)
                for (int j=0; j<n; j++)
                    if (D[i][j] > D[i][k] + D[k][j])
                        D[i][j] = D[i][k] + D[k][j];
                        
        return D;
    }
    /** 
     * Transmission Matrix
     *
     */
    private int[][] TrMatrix;
    private boolean TrMatrixExists = false;
    
    public int[][] TrMatrix(){
        if (TrMatrixExists)
            return TrMatrix;
            
        TrMatrixExists = true;
        TrMatrix = new int[n][n];
        int[][] D = Dmatrix();
        
        int[][] TrMatrix = new int[n][n];
        for (int i = 0; i < n; i++){
            for (int j = 0; j < n; j++){
                TrMatrix[i][j] = 0;
            }
        }       
        int contador;
        for (int i = 0; i < n; i++){
            contador = 0;
            for (int j = 0; j < n; j++){
                contador = contador + D[i][j];
            }
            TrMatrix[i][i] = contador;
        }

        return TrMatrix;
    }
    /** 
     * Signless Laplacian Distance Matrix
     *
     */
    private int[][] SQD;
    private boolean SQDExists = false;
    
    public int[][] SQDmatrix(){
        if (SQDExists)
            return SQD;
            
        SQDExists = true;
        SQD = new int[n][n];
        

        int[][] D = Dmatrix();
        int[][] Tr = TrMatrix();
        
        for (int i = 0; i < n; i++){
            for (int j = 0; j < n; j++){
                SQD[i][j] = D[i][j] + Tr[i][j];
            }
        } 
        return SQD;
    }
    /** 
     * Laplacian Distance Matrix
     *
     */
    private int[][] QD;
    private boolean QDExists = false;
    
    public int[][] QDmatrix(){
        if (QDExists)
            return QD;
            
        QDExists = true;
        QD = new int[n][n];
        

        int[][] D = Dmatrix();
        int[][] Tr = TrMatrix();
        
        for (int i = 0; i < n; i++){
            for (int j = 0; j < n; j++){
                QD[i][j] = Tr[i][j] - D[i][j];
            }
        } 
        return QD;
    } 
    /** 
     * Modularity matrix
     */
    private double[][] M;
    private boolean MExists = false;
    
    public double[][] Mmatrix() {
        if (MExists)
            return M;
            
        MExists = true;    
        for (int i=0; i<n; i++)
            for (int j=0; j<n; j++)
                M[i][j] = ((double) A[i][j]) - ((double) degree[i]*degree[j])/(2*m);

        return M;
    }
    /** 
    * ABC MATRIX
    */
    private double [][] Z;
    private boolean ZExists = false;  
    
    public double[][] Zmatrix() {
        if (ZExists) 
            return Z;
        
        ZExists = true; 
        Z = new double [n][n];
        
        for (int i=0; i<n; i++)     // off-diagonal entries are opposite of adjacency matrix entries
            for (int j=0; j<i; j++) 
                if (A[i][j]==1) {
                Z[i][j] = (Math.sqrt((double)degree[i]+degree[j]-2))/Math.sqrt((double)degree[i]*degree[j]);;
                Z[j][i] = (Math.sqrt((double)degree[i]+degree[j]-2))/Math.sqrt((double)degree[i]*degree[j]);;
            }
        return Z;
    }
    /**
     *  Auxiliary function to find eigenvalues of an integer matrix.
     *  "static" means it is a method of the class itself,
     *  so that it has to be called as Graph6.spectrum(matrix).
     *  Assumption: mat is a square matrix
     */
    public static double[] spectrum(int[][] mat) {
        int dim = mat.length;
        double[][] dmat = new double[dim][dim];
        for (int i=0; i<dim; i++)
            for (int j=0; j<dim; j++)
                dmat[i][j] = (double) mat[i][j];
            
        return Graph6.spectrum(dmat);
    }

    /** 
     * Auxiliary function to find eigenvalues of a double matrix.
     * "static" means it is a method of the class itself,
     * so that it has to be called as Graph6.spectrum(matrix).
     * Assumption: mat is a square matrix
     */
    public static double[] spectrum(double[][] dmat) {
        DoubleMatrix2D cernAdj = new DenseDoubleMatrix2D(dmat);
        EigenvalueDecomposition cernDecomposition = new EigenvalueDecomposition(cernAdj);
        DoubleMatrix1D adjSpectrum = cernDecomposition.getRealEigenvalues();
        double[] eigenvalues = adjSpectrum.toArray();
        
        return eigenvalues;
    }    
    
    /** 
     * Auxiliary function to find eigenvectors of an integer matrix.
     * Eigenvectors are returned in columns.
     * Assumption: mat is a square matrix
     */
    public static double[][] eigenvectors(int[][] mat) {
        int dim = mat.length;
        double[][] dmat = new double[dim][dim];
        for (int i=0; i<dim; i++)
            for (int j=0; j<dim; j++)
                dmat[i][j] = (double) mat[i][j];
            
        return Graph6.eigenvectors(dmat);
    }

    /** 
     * Auxiliary function to find eigenvectors of a double matrix.
     * Eigenvectors are returned in columns.
     * Assumption: mat is a square matrix
     */
    public static double[][] eigenvectors(double[][] dmat) {
        DoubleMatrix2D cernAdj = new DenseDoubleMatrix2D(dmat);
        EigenvalueDecomposition cernDecomposition = new EigenvalueDecomposition(cernAdj);
        DoubleMatrix2D eV = cernDecomposition.getV();
        
        int dim = dmat.length;
        double[][] eigenvector = new double[dim][dim];
        for (int i=0; i<dim; i++)
            for (int j=0; j<dim; j++)
                eigenvector[i][j] = eV.get(i,j);
        
        return eigenvector;
    }    
    
    /** 
     * Adjacency spectrum and eigenvectors
     * Aspectrum()[0] is the smallest, Aspectrum()[n-1] is the largest eigenvalue
     */
    public double[] Aspectrum() {
        return Graph6.spectrum(A);
    }
    
    public double[][] Aeigenvectors() {
        return Graph6.eigenvectors(A);
    }
    
    /** 
     * Laplacian spectrum and eigenvectors
     * Lspectrum()[0] is the smallest, Lspectrum()[n-1] is the largest eigenvalue
     */
    public double[] Lspectrum() {
        Lmatrix();
        return Graph6.spectrum(L);
    }
    
    public double[][] Leigenvectors() {
        return Graph6.eigenvectors(L);
    }
    
    /**
     * Signless Laplacian spectrum and eigenvectors
     * Qspectrum()[0] is the smallest, Qspectrum()[n-1] is the largest eigenvalue
     */
    public double[] Qspectrum() {
        Qmatrix();
        return Graph6.spectrum(Q);
    }
    
    public double[][] Qeigenvectors() {
        return Graph6.eigenvectors(Q);
    }
        
    /** 
     * Distance spectrum and eigenvectors
     * Dspectrum()[0] is the smallest, Dspectrum()[n-1] is the largest eigenvalue
     */
    public double[] Dspectrum() {
        Dmatrix();
        return Graph6.spectrum(D);
    }

    public double[][] Deigenvectors() {
        return Graph6.eigenvectors(D);
    }
     /** 
     * Signless Distance Laplacian spectrum and eigenvectors
     * QDspectrum()[0] is the smallest, QDspectrum()[n-1] is the largest eigenvalue
     */   
    public double[] SQDspectrum() {
        SQDmatrix();
        return Graph6.spectrum(SQD);
    }
    
    public double[][] SQDeigenvectors() {
        return Graph6.eigenvectors(SQD);
    }
     /** 
     * Distance Laplacian spectrum and eigenvectors
     * QDspectrum()[0] is the smallest, QDspectrum()[n-1] is the largest eigenvalue
     */    
    public double[] QDspectrum() {
        QDmatrix();
        return Graph6.spectrum(QD);
    }
    
    public double[][] QDeigenvectors() {
        return Graph6.eigenvectors(QD);
    }   
    /** 
     * Modularity spectrum
     * Mspectrum()[0] is the smallest, Mspectrum()[n-1] is the largest eigenvalue
     */
    public double[] Mspectrum() {
        Mmatrix();
        return Graph6.spectrum(M);
    }
    
    public double[][] Meigenvectors() {
        return Graph6.eigenvectors(M);
    }
    /** 
     * ABC spectrum
     * Zspectrum()[0] is the smallest, Zspectrum()[n-1] is the largest eigenvalue
     */
    public double[] Zspectrum() {
        Zmatrix();
        return Graph6.spectrum(Z);
    }
    
    public double[][] Zeigenvectors() {
        return Graph6.eigenvectors(Z);
    }    
    /**
     * Graovac-Ghorbani index of ABC matrix
     */
    public double ZGGindex(){
        Dmatrix();
        double abc2=0.0;
        int di=0,dj=0;
        for (int i=0; i<n; i++)
            for (int j=i+1; j<n; j++)
               if (D[i][j]==1) {
                  di=0; dj=0; 
                  for (int k=0; k<n; k++){
                      if (D[i][k] > D[j][k])  
                          di++;         
                      if (D[i][k] < D[j][k])
                          dj++; 
                  }  
                  abc2+=Math.sqrt(((double)di+dj-2)/(di*dj));  
               }
        return abc2;
    }     
 /** 
    * Atom-Bond connectivity index of ABC matrix
    */
    public double Zindex() {
        double abc = 0.0;
        for (int i=0; i<n; i++)
            for (int j=0; j<i; j++)
                if (A[i][j]==1)
                    abc += (Math.sqrt(degree[i]+degree[j]-2))/Math.sqrt(degree[i]*degree[j]);
        return abc;
    }    
    /** 
     * Checks whether two graphs have the same adjacency spectrum
     */
    public boolean Acospectral(Graph6 h) {
        return DoubleUtil.equals(Aspectrum(), h.Aspectrum());
    }
    
    /**
     * Checks whether two graphs have the same Laplacian spectrum
     */
    public boolean Lcospectral(Graph6 h) {
        return DoubleUtil.equals(Lspectrum(), h.Lspectrum());
    }
    
    /** 
     * Checks whether two graphs have the same signless Laplacian spectrum
     */
    public boolean Qcospectral(Graph6 h) {
        return DoubleUtil.equals(Qspectrum(), h.Qspectrum());
    }
    
    /** 
     * Checks whether two graphs have the same distance spectrum
     */
    public boolean Dcospectral(Graph6 h) {
        return DoubleUtil.equals(Dspectrum(), h.Dspectrum());
    }
    
    /** 
     * Checks whether two graphs have the same modularity spectrum
     */
    public boolean Mcospectral(Graph6 h) {
        return DoubleUtil.equals(Mspectrum(), h.Mspectrum());
    }

    /** 
     * Auxiliary function to check whether an integer matrix has integer eigenvalues.
     * "static" means it is a method of the class itself,
     * so that it has to be called as Graph6.integralSpectrum(matrix).
     * Assumption: mat is a square matrix
     */
    public static boolean integralSpectrum(int[][] mat) {
        double[] eigenvalues = Graph6.spectrum(mat);
        int dim = mat.length;
        
        for (int i=0; i<dim; i++)
            if (!DoubleUtil.equals(eigenvalues[i], (double) Math.round(eigenvalues[i])))
                return false;
        
        return true;
    }    

    /**
     * Auxiliary function to check whether a double matrix has integer eigenvalues.
     * Assumption: mat is a square matrix
     */
    public static boolean integralSpectrum(double[][] dmat) {
        double[] eigenvalues = Graph6.spectrum(dmat);
        int dim = dmat.length;
        
        for (int i=0; i<dim; i++)
            if (!DoubleUtil.equals(eigenvalues[i], (double) Math.round(eigenvalues[i])))
                return false;
        
        return true;
    }    

    /** 
     * Is adjacency spectrum integral?
     */
    public boolean Aintegral() {
        return Graph6.integralSpectrum(A);
    }
    
    /** 
     * Is Laplacian spectrum integral?
     */
    public boolean Lintegral() {
        Lmatrix();
        return Graph6.integralSpectrum(L);
    }
    
    /** 
     * Is signless Laplacian spectrum integral?
     */
    public boolean Qintegral() {
        Qmatrix();
        return Graph6.integralSpectrum(Q);
    }
    
    /** 
     * Is distance spectrum integral?
     */
    public boolean Dintegral() {
        Dmatrix();
        return Graph6.integralSpectrum(D);
    }
    
    /**
     * Is modularity spectrum integral?
     */
    public boolean Mintegral() {
        Mmatrix();
        return Graph6.integralSpectrum(M);
    }
        
    /**
     * Auxiliary function for calculation of energies
     */
    public static double deviation(double[] eigs) {
        double average = 0.0;
        for (int i=0; i < eigs.length; i++)
            average += eigs[i];
        average = average / eigs.length;
        
        double deviation = 0.0;
        for (int i=0; i< eigs.length; i++)
            deviation += Math.abs(eigs[i] - average);
            
        return deviation;
    }
    
    /** 
     * Matrix energy is deviation from the average of its eigenvalues.
     * "static" means it is a method of the class itself,
     * so that it has to be called as Graph6.matrixEnergy(matrix).
     * Assumption: mat is a square matrix
     */
    public static double matrixEnergy(int[][] mat) {
        return Graph6.deviation(Graph6.spectrum(mat));
    }

    public static double matrixEnergy(double[][] dmat) {
        return Graph6.deviation(Graph6.spectrum(dmat));
    }

    /**
     * Adjacency energy
     */
    public double energy() {
        return Aenergy();
    }
    
    public double Aenergy() {
        return Graph6.deviation(Aspectrum());
    }
    
    /**
     * Laplacian energy
     */
    public double Lenergy() {
        return Graph6.deviation(Lspectrum());
    }
    
    /**
     * Signless Laplacian energy
     */
    public double Qenergy() {
        return Graph6.deviation(Qspectrum());
    }
    
    /**
     * Distance energy
     */
    public double Denergy() {
        return Graph6.deviation(Dspectrum());
    }
    
    /**
     * Laplacian Distance energy
     */
    public double QDenergy() {
        return Graph6.deviation(QDspectrum());
    }

    /**
     * Signless Laplacian Distance energy
     */
    public double SQDenergy() {
        return Graph6.deviation(SQDspectrum());
    } 
    
    /**
     * Modularity energy
     */
    public double Menergy() {
        return Graph6.deviation(Mspectrum());
    }
    
    /** 
     * LEL, Laplacian-like energy
     */
    public double LEL() {
        double[] eigs = Lspectrum();
        
        double lel = 0.0;
        for (int i=0; i<n; i++)
            if (eigs[i]>0)
                lel += Math.sqrt(eigs[i]);
                
        return lel;
    }
    
    /**
     * Estrada index
     */ 
    public double estrada() {
        double[] eigs = Aspectrum();
        
        double estrada = 0.0;
        for (int i=0; i<n; i++)
            estrada += Math.exp(eigs[i]);
        return estrada;
    }
    
    /** 
     * Laplacian Estrada index
     */
    public double Lestrada() {
        double[] eigs = Lspectrum();
        
        double lestrada = 0.0;
        for (int i=0; i<n; i++)
            lestrada += Math.exp(eigs[i]);
        return lestrada;
    }
    
    /**
     * Diameter
     */ 
    public int diameter() {
        Dmatrix();
        int diameter = 0;
        for (int i=0; i<n; i++)
            for (int j=0; j<i; j++)
                if (D[i][j]>diameter)
                    diameter = D[i][j];
        return diameter;
    }
    
    /** 
     * Radius
     */
    public int radius() {
        Dmatrix();
        int radius = n;
        for (int i=0; i<n; i++) {
            int ecc = 0;
            for (int j=0; j<n; j++)
                if (D[i][j]>ecc)
                    ecc = D[i][j];
            if (ecc<radius)
                radius=ecc;
        }
        return radius;
    }
    
    /** 
     * Wiener index
     */
    public int wiener() {
        Dmatrix();
        int wiener = 0;
        for (int i=0; i<n; i++)
            for (int j=0; j<i; j++)
                wiener += D[i][j];
        return wiener;
    }
    
    /** 
     * Randic index
     */
    public double randic() {
        double randic = 0.0;
        for (int i=0; i<n; i++)
            for (int j=0; j<i; j++)
                if (A[i][j]==1)
                    randic += 1/Math.sqrt(degree[i]*degree[j]);
        return randic;
    }    
    
    /** 
     * First Zagreb index
     */
    public int zagreb1() {
        int zagreb1 = 0;
        for (int i=0; i<n; i++)
            zagreb1 += degree[i]*degree[i];
        return zagreb1;
    }
    
    /**
     * Second Zagreb index
     */
    public int zagreb2() {
        int zagreb2 = 0;
        for (int i=0; i<n; i++)
            for (int j=0; j<i; j++)
                if (A[i][j]==1)
                    zagreb2 += degree[i]*degree[j];
        return zagreb2;
    }
    
    /** 
     * Distance-sum heterogeneity index is defined by Estrada and Vargas-Estrada
     * in Appl. Math. Comput. 218 (2012), 10393-10405 as
     * dshi = \sum_{i=1}^n \frac{d_i}{s_i} - 2\sum_{ij\in E} (s_is_j)^{-1/2},
     * where d_i is the degree of vertex i, while s_i is the sum of distances from i to all other vertices.
     */
    public double dshi() {
        Dmatrix();
        int[] s = new int[n];
        for (int i=0; i<n; i++) {
            s[i]=0;
            for (int j=0; j<n; j++)
                s[i] += D[i][j];
        }
    
        double dshi = 0.0;
        for (int i=0; i<n; i++)
            dshi += ((double)degree[i])/s[i];
            
        for (int i=0; i<n; i++)
            for (int j=0; j<i; j++)
                if (A[i][j]==1)
                    dshi -= 2/Math.sqrt(s[i]*s[j]);

        return dshi;
    }
    
    boolean isSafe(int v, int graph[][], int color[], 
                   int c) 
    { 
        for (int i = 0; i < n; i++) 
            if (graph[v][i] == 1 && c == color[i]) 
                return false; 
        return true; 
    } 
  
    /* A recursive utility function to solve m 
       coloring  problem */
    boolean graphColoringUtil(int graph[][],
                              int color[], int v) 
    { 
        /* base case: If all vertices are assigned 
           a color then return true */
        if (v == n) 
            return true; 
  
        /* Consider this vertex v and try different 
           colors */
        for (int c = 1; c <= m; c++) 
        { 
            /* Check if assignment of color c to v 
               is fine*/
            if (isSafe(v, graph, color, c)) 
            { 
                color[v] = c; 
  
                /* recur to assign colors to rest 
                   of the vertices */
                if (graphColoringUtil(graph, 
                                      color, v + 1)) 
                    return true; 
  
                /* If assigning color c doesn't lead 
                   to a solution then remove it */
                color[v] = 0; 
            } 
        } 
  
        /* If no color can be assigned to this vertex 
           then return false */
        return false; 
    } 
    int chromaticNumber() 
    { 
        // Initialize all color values as 0. This 
        // initialization is needed correct functioning 
        // of isSafe()
        int max = 0;
        color = new int[n]; 
        for (int i = 0; i < n; i++) 
            color[i] = 0; 
  
        // Call graphColoringUtil() for vertex 0 
        if (!graphColoringUtil(A, color, 0)) 
        { 
            return 0; 
        } 
  
        // Print the solution 
        for (int i = 0; i < n; i++) 
            if (color[i]>max)
                max = color[i];
        
        return max; 
    } 
    int cliqueNumber() {
        cpuTime = System.currentTimeMillis();
        nodes = 0;
        nodes=maxSize=0;
        cpuTime = timeLimit = -1;
        style=1;
        solution=new int[n];
        ArrayList<Integer> C = new ArrayList<>();
        ArrayList<Integer> P = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            P.add(i);
        }
        expand(C, P);
        return maxSize;
    }

    void expand(ArrayList<Integer> C, ArrayList<Integer> P) {
        Amatrix();
        if (timeLimit > 0 && System.currentTimeMillis() - cpuTime >= timeLimit) {
            return;
        }
        nodes++;
        for (int i = P.size() - 1; i >= 0; i--) {
            if (C.size() + P.size() <= maxSize) {
                return;
            }
            int v = P.get(i);
            C.add(v);
            ArrayList<Integer> newP = new ArrayList<>(i);
            for (int j = 0; j <= i; j++) {
                int w = P.get(j);
                if (A[v][w] == 1) {
                    newP.add(w);
                }
            }
            if (newP.isEmpty() && C.size() > maxSize) {
                saveSolution(C);
            }
            if (!newP.isEmpty()) {
                expand(C, newP);
            }
            C.remove(C.size() - 1);
            P.remove(i);
        }
    }

    void saveSolution(ArrayList<Integer> C) {
        Arrays.fill(solution, 0);
        for (int i : C) {
            solution[i] = 1;
        }
        maxSize = C.size();
    }  
    /* A utility function to print solution */
    /** 
     * Output printing formats for graph and its matrices
     * Returns multiline string representing integer matrix
     * "static" means it is a method of the class itself,
     * so that it has to be called as Graph6.printMatrix(mat, delims)
     * delims is a three-character string, 
     * where character at position 0 is put at the beginning of a matrix,
     * character at position 1 is put between entries,
     * and character at position 2 is put at the end of a matrix (think of "[,]").
     */
    public static String printMatrix(int[][] mat, String delims) {
        StringBuffer buf = new StringBuffer("");

        buf.append(delims.charAt(0) + " ");
        for (int i=0; i<mat.length; i++) {
            buf.append(delims.charAt(0) + " ");
            for (int j=0; j<mat[i].length; j++) {
                buf.append("" + mat[i][j]);
                
                if (j!=mat[i].length-1)         // was it the last column?
                    buf.append(delims.charAt(1) + " ");
            }
            buf.append(delims.charAt(2));

            if (i!=mat.length-1)                // was it the last row?
                buf.append(delims.charAt(1) + "\n");
        }
        buf.append(delims.charAt(2));
        
        return buf.toString();
    }

    public static String printMatrix(int[][] mat) {
        return Graph6.printMatrix(mat, "[,]");
    }
    
    /** 
     * Returns multiline string representing double matrix
     */
    public static String printMatrix(double[][] dmat, String delims) {
        StringBuffer buf = new StringBuffer("");

        buf.append(delims.charAt(0) + " ");
        for (int i=0; i<dmat.length; i++) {
            buf.append(delims.charAt(0) + " ");
            for (int j=0; j<dmat[i].length; j++) {
                buf.append("" + dmat[i][j]);
                
                if (j!=dmat[i].length-1)         // was it the last column?
                    buf.append(delims.charAt(1) + " ");
            }
            buf.append(delims.charAt(2));

            if (i!=dmat.length-1)               // was it the last row?
                buf.append(delims.charAt(1) + " ");
        }
        buf.append(delims.charAt(2));
        
        return buf.toString();
    }

    public static String printMatrix(double[][] dmat) {
        return Graph6.printMatrix(dmat, "[,]");
    }
    
    /** 
     * String representing adjacency matrix
     */
    public String printAmatrix() {
        return Graph6.printMatrix(A);
    }

    /**
     * String representing Laplacian matrix    
     */
    public String printLmatrix() {
        Lmatrix();
        return Graph6.printMatrix(L);
    }
    
    /**
     * String representing signless Laplacian matrix
     */
    public String printQmatrix() {
        Qmatrix();
        return Graph6.printMatrix(Q);
    }
    
    /**
     * String representing distance matrix
     */
    public String printDmatrix() {
        Dmatrix();
        return Graph6.printMatrix(D);
    }
    /**
     * String representing Signless Laplacian distance martix
     */
    public String printSQDmatrix() {
        SQDmatrix();
        return Graph6.printMatrix(SQD);
    }
    /**
     * String representing Laplacian distance martix
     */
    public String printQDmatrix() {
        QDmatrix();
        return Graph6.printMatrix(QD);
    }      
    /**
     * String representing modularity matrix
     */
    public String printMmatrix() {
        Mmatrix();
        return Graph6.printMatrix(M);
    }
    
    /**
     * Returns string containing list of edges
     */
    public String printEdgeList() {
        StringBuffer buf = null;

        for (int i=0; i<n; i++)
            for (int j=i+1; j<n; j++)
                if (A[i][j]==1) {
                    if (buf == null) {
                        buf = new StringBuffer("");
                    }
                    else
                        buf.append(", ");
                    buf.append("" + i + " " + j);
                }
        
        return buf.toString();
    }
    
    /**
     * Returns string describing graph in a .dot format,
     * needed for visualisation with Graph6viz.
     */
    public String printDotFormat() {
        StringBuffer buf = new StringBuffer("Graph {\n");
        
        for (int i=0; i<n; i++){
            buf.append("" + i + "\n");
            for (int j=i+1; j<n; j++){
                if (A[i][j]==1)
                    buf.append("" + i + " -- " + j + "\n");
            }
        }
        buf.append("}\n");
        return buf.toString();
    }
        
    /**
     * Together with the graph, you can visualise additional data
     * by placing them in a data string. 
     * This string is put as a label of a separate isolated vertex,
     * and visualised by Graph6viz in the same image next to the graph itself.
     */
    public String printDotFormat(String data) {
        StringBuffer buf = new StringBuffer("Graph {\n");
        
        for (int i=0; i<n; i++)
            for (int j=i+1; j<n; j++)
                if (A[i][j]==1)
                    buf.append("" + i + " -- " + j + "\n");
        
        buf.append("data [shape=box, label=\"" + data + "\"]\n");            
        buf.append("}\n");
        return buf.toString();        
    }
    
    /** 
     * Writes the .dot format description of a graph to the file
     */
    public void saveDotFormat(String filename) throws IOException {
        PrintWriter outfile = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
        outfile.println(printDotFormat());
        outfile.close();
    }
    
    /**
     * Writes the .dot format description of a graph to the file,
     * together with additional data placed as a label of a separate isolated vertex
     */
    public void saveDotFormat(String filename, String data) throws IOException {
        PrintWriter outfile = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
        outfile.println(printDotFormat(data));
        outfile.close();
    }
}