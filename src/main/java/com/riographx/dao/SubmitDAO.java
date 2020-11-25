package com.riographx.dao;
  
import com.riographx.entities.Submit;
import com.riographx.connection.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
  
public class SubmitDAO
        implements DAO<Submit> {
  

    public Submit getSingle(){
        Connection conn = Conexao.open();
        PreparedStatement ps = null;
        ResultSet rs = null;        
        try{
            ps=conn.prepareStatement(  
            "SELECT id_submit, function, min_max, min_order, max_order, min_degree, max_degree, maxresults, triang_free, connected, bipartite, s.id_user, email, username " +
            "FROM public.submit s inner join public.usuario u on s.id_user = u.id_user " +
            "WHERE spark_ok = 'false' LIMIT 1;");  
            rs = ps.executeQuery();
            if (rs.next()) {
                return new Submit(
                        rs.getInt("id_submit"), 
                        rs.getString("function"),                         
                        rs.getBoolean("min_max"),
                        rs.getInt("min_order"),
                        rs.getInt("max_order"),
                        rs.getInt("min_degree"),
                        rs.getInt("max_degree"),
                        rs.getInt("maxresults"),
                        rs.getBoolean("triang_free"),
                        rs.getBoolean("connected"),
                        rs.getBoolean("bipartite"),
                        rs.getInt("id_user"),
                        rs.getString("email"),
                        rs.getString("username"));
            }            
        }catch(SQLException e){System.out.println(e);}  
        finally {
            Conexao.close(rs, ps, conn);
        }
        return null; 
    }
    public Submit setProcessed(Submit su){
        
        try{
            Connection conn = Conexao.open();
            PreparedStatement ps=conn.prepareStatement(  
            "UPDATE public.submit SET spark_ok = 'true' WHERE id_submit = ?;");  
            ps.setInt(1, su.getId_submit());
            ps.execute();
            ps.closeOnCompletion();
        }catch(SQLException e){System.out.println(e);}
        return null;      
    }
    public Submit setProcessing(Submit su){
        
        try{
            Connection conn = Conexao.open();
            PreparedStatement ps=conn.prepareStatement(  
            "update public.results set status = 'PROCESSING', start_datetime = now() where id_submit = ?;");  
            ps.setInt(1, su.getId_submit());
            ps.execute();
            ps.closeOnCompletion();
        }catch(SQLException e){System.out.println(e);}
        return null;      
    }
    public Submit setFinished(Submit su){
        
        try{
            Connection conn = Conexao.open();
            PreparedStatement ps=conn.prepareStatement(  
            "update public.results set status = 'FINISHED', end_datetime=now() where id_submit = ?;");  
            ps.setInt(1, su.getId_submit());
            ps.execute();
            ps.closeOnCompletion();
        }catch(SQLException e){System.out.println(e);}
        return null;      
    }
    public Submit setError(Submit su){
        
        try{
            Connection conn = Conexao.open();
            PreparedStatement ps=conn.prepareStatement(  
            "update public.results set status = 'ERROR', end_datetime=now() where id_submit = ?;");  
            ps.setInt(1, su.getId_submit());
            ps.execute();
            ps.closeOnCompletion();
        }catch(SQLException e){System.out.println(e);}
        return null;      
    }    

    @Override
    public Submit getSingle(Object... chave) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Submit save(Submit su) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Submit> getList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Submit> getList(int top) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}