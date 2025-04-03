package me.a8kj.reply.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T, ID> {
    
    T create(T entity);               
    
    Optional<T> read(ID id);          
    
    List<T> findAll();               
    
    T update(T entity);               
    
    boolean delete(ID id);            
}
