package com.riographx.dao;
  
import com.riographx.entities.Submit;
import java.util.List;
  
public interface DAO<T> {
    public T getSingle(Object... chave);
    public T save(Submit su);
    public List<T> getList();
    public List<T> getList(int top);
}