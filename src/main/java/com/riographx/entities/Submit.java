package com.riographx.entities;
  
import java.io.Serializable;
  
public class Submit
        implements Serializable {
  
private int id_submit;
private String function;
private boolean min_max;
private int min_order;
private int max_order;
private int min_degree;
private int max_degree;
private int maxresults;
private boolean triang_free;
private boolean connected;
private boolean bipartite;
private int edges;
private int id_user;
private boolean spark_ok;
private String email;
private String username;
    
    public Submit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Submit(int id_submit, String function, boolean min_max, int min_order, int max_order, int min_degree, int max_degree, int max_results, boolean triangle_free, boolean connected, boolean bipartite, int id_user, String email, String username) {
       this.id_submit = id_submit;
       this.function = function;
       this.min_max = min_max;
       this.min_order = min_order;
       this.max_order = max_order;
       this.min_degree = min_degree;
       this.max_degree = max_degree;
       this.maxresults = max_results;
       this.triang_free = triangle_free;
       this.connected = connected;
       this.bipartite = bipartite;
       this.id_user = id_user;
       this.email = email;
       this.username = username;
    }

    /**
     * @return the edges
     */
    public int getEdges() {
        return edges;
    }

    /**
     * @return the function
     */
    public String getFunction() {
        return function;
    }

    /**
     * @return the id_submit
     */
    public int getId_submit() {
        return id_submit;
    }

    /**
     * @return the id_user
     */
    public int getId_user() {
        return id_user;
    }

    /**
     * @return the max_degree
     */
    public int getMax_degree() {
        return max_degree;
    }

    /**
     * @return the max_order
     */
    public int getMax_order() {
        return max_order;
    }

    /**
     * @return the maxresults
     */
    public int getMaxresults() {
        return maxresults;
    }

    /**
     * @return the min_degree
     */
    public int getMin_degree() {
        return min_degree;
    }

    /**
     * @return the min_order
     */
    public int getMin_order() {
        return min_order;
    }

    /**
     * @return the bipartite
     */
    public boolean isBipartite() {
        return bipartite;
    }

    /**
     * @return the connected
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * @return the min_max
     */
    public boolean isMin_max() {
        return min_max;
    }

    /**
     * @return the spark_ok
     */
    public boolean isSpark_ok() {
        return spark_ok;
    }

    /**
     * @return the triang_free
     */
    public boolean isTriang_free() {
        return triang_free;
    }

    /**
     * @param bipartite the bipartite to set
     */
    public void setBipartite(boolean bipartite) {
        this.bipartite = bipartite;
    }

    /**
     * @param connected the connected to set
     */
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    /**
     * @param edges the edges to set
     */
    public void setEdges(int edges) {
        this.edges = edges;
    }

    /**
     * @param function the function to set
     */
    public void setFunction(String function) {
        this.function = function;
    }

    /**
     * @param id_submit the id_submit to set
     */
    public void setId_submit(int id_submit) {
        this.id_submit = id_submit;
    }

    /**
     * @param id_user the id_user to set
     */
    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    /**
     * @param max_degree the max_degree to set
     */
    public void setMax_degree(int max_degree) {
        this.max_degree = max_degree;
    }

    /**
     * @param max_order the max_order to set
     */
    public void setMax_order(int max_order) {
        this.max_order = max_order;
    }

    /**
     * @param maxresults the maxresults to set
     */
    public void setMaxresults(int maxresults) {
        this.maxresults = maxresults;
    }

    /**
     * @param min_degree the min_degree to set
     */
    public void setMin_degree(int min_degree) {
        this.min_degree = min_degree;
    }

    /**
     * @param min_max the min_max to set
     */
    public void setMin_max(boolean min_max) {
        this.min_max = min_max;
    }

    /**
     * @param min_order the min_order to set
     */
    public void setMin_order(int min_order) {
        this.min_order = min_order;
    }

    /**
     * @param spark_ok the spark_ok to set
     */
    public void setSpark_ok(boolean spark_ok) {
        this.spark_ok = spark_ok;
    }

    /**
     * @param triang_free the triang_free to set
     */
    public void setTriang_free(boolean triang_free) {
        this.triang_free = triang_free;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

 

  
  
}