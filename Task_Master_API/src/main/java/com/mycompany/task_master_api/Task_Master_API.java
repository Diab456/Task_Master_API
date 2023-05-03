package com.mycompany.task_master_api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;



public class Task_Master_API {
    private  int id;
    private  String title;
    private  String description;
    private  boolean completed;
    private Connection conn;
    
   /** public class TodoDAO {
        private Connection conn;

    // constructor and methods for CRUD operations
    }

    **/
    
    public Task_Master_API(int id, String title, String description, boolean completed){
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
            this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isCompleted() {
        return completed;
    
    }
    
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    
    public Task_Master_API()throws SQLException {
        
        try {
            Class.forName("org.sqlite.JDBC");
            this.conn = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\ALKODS\\Downloads\\Programs\\jar_files.db");
            
            } catch (ClassNotFoundException e){
                e.printStackTrace();
            }
        
        }
    
    public void create(Task_Master_API todo)throws SQLException {
        
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO todos (title, description, completed) VALUES (?, ?, ?)");
        pstmt.setString(1, todo.getTitle());
        pstmt.setString(2, todo.getDescription());
        pstmt.setBoolean(3, todo.isCompleted());
        pstmt.executeUpdate();
    }
    
    public Task_Master_API read(int id)throws SQLException{
        
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM todos WHERE id=?");
        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()){
            
            Task_Master_API todo = new Task_Master_API();
            todo.setId(rs.getInt("id"));
            todo.setTitle(rs.getString("title"));
            todo.setDescription(rs.getString("description"));
            todo.setCompleted(rs.getBoolean("completed"));
            return todo; 
        }
        return null;
    }
    
    public List<Task_Master_API> readAll() throws SQLException {
        
        List<Task_Master_API> todos = new ArrayList<>();
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM todos");
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            
            Task_Master_API todo = new Task_Master_API();
            todo.setId(rs.getInt("id"));
            todo.setTitle(rs.getString("title"));
            todo.setDescription(rs.getString("description"));
            todo.setCompleted(rs.getBoolean("completed"));
            todos.add(todo);
        }
        return todos;
    }
    
    public void update(Task_Master_API todo) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("UPDATE todos SET title=?, description=?, completed=? WHERE id=?");
        pstmt.setString(1, todo.getTitle());
        pstmt.setString(2, todo.getDescription());
        pstmt.setBoolean(3, todo.isCompleted());
        pstmt.setInt(4, todo.getId());
        pstmt.executeUpdate();
    }
    
    public void delete(int id) throws SQLException{
        PreparedStatement pstmt = conn.prepareStatement("DELETE FROM todos WHERE id=?");
        pstmt.setInt(1, id);
        pstmt.executeUpdate();
    }
    
    

    
    

    
    
    

    public static void main(String[] args) throws SQLException {
        Task_Master_API todoDAO = new Task_Master_API();

    // create a new task
    Spark.post("/tasks", (request, response) -> {
        response.type("application/json");
        Task_Master_API todo = new Gson().fromJson(request.body(), Task_Master_API.class);
        todoDAO.create(todo);
        return new Gson().toJson(todo);
    });

    // read a task by id
    Spark.get("/tasks/:id", (request, response) -> {
        response.type("application/json");
        int id = Integer.parseInt(request.params("id"));
        Task_Master_API todo = todoDAO.read(id);
        if (todo != null) {
            return new Gson().toJson(todo);
        } else {
            response.status(404); // 404 Not found
            return new Gson().toJson("Task not found");
        }
    });

    // read all tasks
    Spark.get("/tasks", (request, response) -> {
        response.type("application/json");
        List<Task_Master_API> todos = todoDAO.readAll();
        return new Gson().toJson(todos);
    });

    // update a task
    Spark.put("/tasks/:id", (request, response) -> {
        response.type("application/json");
        int id = Integer.parseInt(request.params("id"));
        Task_Master_API todo = new Gson().fromJson(request.body(), Task_Master_API.class);
        todo.setId(id);
        todoDAO.update(todo);
        return new Gson().toJson(todo);
    });

    // delete a task
    Spark.delete("/tasks/:id", (request, response) -> {
        response.type("application/json");
        int id = Integer.parseInt(request.params("id"));
        todoDAO.delete(id);
        return new Gson().toJson("Task deleted");
    });
        
        
       // System.out.println("Hello World!");
    }
}
