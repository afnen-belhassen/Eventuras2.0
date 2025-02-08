package eqprit.tn.class3a17.titans;

import java.sql.SQLException;
import java.util.ArrayList;

public interface Iuser<T> {
    void ajouter(T t) throws SQLException;
    boolean updateUser(T t) throws SQLException;
    void delete(T t) throws SQLException;
    ArrayList<T> afficherAll() throws SQLException;

}
