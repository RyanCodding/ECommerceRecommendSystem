package com.business.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private int userId;

    private String name;

    private String password;

    private String salt;

    private boolean first;

    private long timestamp;

    private List<String> prefGenres = new ArrayList<>();


}