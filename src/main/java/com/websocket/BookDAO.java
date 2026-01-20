package com.websocket;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {


    private DbConector conector;

    

    public BookDAO(DbConector conector) {
        this.conector = conector;
    }

    
        

    public List<Book> getAll() throws SQLException {
        List<Book> books = new ArrayList<>();
        try (Connection con = conector.connect()) {
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM books");
            while (rs.next()) {
                books.add(new Book(
                    rs.getInt("id"), 
                    rs.getString("title"), 
                    rs.getString("author"), 
                    rs.getInt("year")
                ));
            }
        }
        return books;
    }

    public Book getById(int id) throws SQLException {
    try (Connection con = conector.connect()) {
        PreparedStatement ps = con.prepareStatement("SELECT * FROM books WHERE id = ?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"), rs.getInt("year"));
        }
    }
    return null; 
}

    public void insert(Book book) throws SQLException {
        try (Connection con = conector.connect()) {
            String sql = "INSERT INTO books (title, author, year) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor()); 
            ps.setInt(3, book.getYear());    
            ps.executeUpdate();
        }
    }




public void update(Book book) throws SQLException {
    try (Connection con = conector.connect()) {
        String sql = "UPDATE books SET title = ?, author = ?, year = ? WHERE id = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, book.getTitle());
        ps.setString(2, book.getAuthor());
        ps.setInt(3, book.getYear());
        ps.setInt(4, book.getId());
        ps.executeUpdate();
    }
}

    public void delete(int id) throws SQLException {
        try (Connection con = conector.connect()) {
            PreparedStatement ps = con.prepareStatement("DELETE FROM books WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}