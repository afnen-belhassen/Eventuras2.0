package services;

import java.sql.SQLException;
import java.util.ArrayList;

public interface IService <T>{
    void ajouter(T t) throws SQLException;
    void update(T t) throws SQLException;
    void delete(T t)throws SQLException;
    ArrayList<T> afficherAll() throws SQLException;
}
