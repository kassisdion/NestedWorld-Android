package com.nestedworld.nestedworld.api.models.apiResponse.users;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nestedworld.nestedworld.api.models.User;

public class UserResponse {

    @Expose
    public User user;
}
