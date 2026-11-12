package com.echovault.dao;
import com.echovault.util.*;import java.sql.*;
public class AdminDAO {
 public int authenticate(String username,String password)throws SQLException{try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement("SELECT id,password_hash FROM admins WHERE username=?")){p.setString(1,username);try(ResultSet r=p.executeQuery()){if(r.next()&&PasswordUtil.verify(password,r.getString("password_hash")))return r.getInt("id");return -1;}}}
 public void seedDefaultAdmin()throws SQLException{try(Connection c=DBConnection.getConnection();PreparedStatement q=c.prepareStatement("SELECT id FROM admins LIMIT 1");ResultSet r=q.executeQuery()){if(!r.next())try(PreparedStatement p=c.prepareStatement("INSERT INTO admins(username,password_hash) VALUES(?,?)")){p.setString(1,"admin");p.setString(2,PasswordUtil.hash("EchoAdmin@123"));p.executeUpdate();}}}
}