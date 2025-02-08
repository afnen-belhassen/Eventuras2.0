package eqprit.tn.class3a17.titans;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {

        MyConnection mc = MyConnection.getInstance();
        user p = new user(54329654, "koukouuudds","heki heyaa","kssay@esprit.tn","admin","true",5);
        user l = new user(54329654, "khraa","heki heyaa","kousay@.tn","admin","true",1);

        Cuser c = new Cuser();

        try{
            System.out.println(c.afficherAll());
        }
        catch(Exception a){
            System.out.println(a.getMessage());

        }
       try{

           c.delete(p);

        }
        catch(Exception k){
            System.out.println(k.getMessage());
        }

        try{
            System.out.println(c.afficherAll());
        }
        catch(Exception a){
            System.out.println(a.getMessage());

        }}}