package com.echovault.dao;
import com.echovault.util.*;import java.sql.*;
public class UserDAO {
 public boolean exists(String email)throws SQLException{try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement("SELECT id FROM users WHERE email=?")){p.setString(1,email);try(ResultSet r=p.executeQuery()){return r.next();}}}
 public void create(String name,String email,String password)throws SQLException{try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement("INSERT INTO users(full_name,email,password_hash) VALUES(?,?,?)")){p.setString(1,name);p.setString(2,email);p.setString(3,PasswordUtil.hash(password));p.executeUpdate();}}
 public int authenticate(String email,String password)throws SQLException{try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement("SELECT id,password_hash FROM users WHERE email=? AND active=1")){p.setString(1,email);try(ResultSet r=p.executeQuery()){if(r.next()&&PasswordUtil.verify(password,r.getString("password_hash")))return r.getInt("id");return -1;}}}
 public String name(int id)throws SQLException{try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement("SELECT full_name FROM users WHERE id=?")){p.setInt(1,id);try(ResultSet r=p.executeQuery()){return r.next()?r.getString(1):"Time traveler";}}}
}