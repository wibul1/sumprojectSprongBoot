package com.example.project.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class UserModel {
    @Id
    private String id;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private List<UserTokenModel> tokens = new ArrayList<>();

    public UserModel() {}

    // Getters and Setters
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<UserTokenModel> getTokens() {
        return tokens;
    }

    public void setTokens(List<UserTokenModel> tokens) {
        this.tokens = tokens;
    }

    public void addToken(UserTokenModel token) {
        if (this.tokens.size() >= 3) {
            this.tokens.remove(0);
        }
        this.tokens.add(token);
    }
}