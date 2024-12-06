package com.example.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.ArrayList;

@Document(collection = "users")
public class UserModel {
    @Id
    private String id;
    private String username;
    private String password;
    private List<UserTokenModel> tokens = new ArrayList<>();

    // Constructor, Getter and Setter methods
    public UserModel() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<UserTokenModel> getTokens() {
        return tokens;
    }

    public void setTokens(List<UserTokenModel> tokens) {
        this.tokens = tokens;
    }

    public void addToken(UserTokenModel token) {
        // Remove old tokens if needed (e.g., keep only last 3)
        if (this.tokens.size() >= 3) {
            this.tokens.remove(0);
        }
        this.tokens.add(token);
    }
}