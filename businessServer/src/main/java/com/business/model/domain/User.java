package com.business.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class User {

    @JsonIgnore
    private String _id;

    private int userId;

    private String username;

    private String password;

    private boolean first;

    private long timestamp;

    private List<String> prefGenres = new ArrayList<>();

    public boolean passwordMatch(String password) {
        return this.password.compareTo(password) == 0;
    }


}