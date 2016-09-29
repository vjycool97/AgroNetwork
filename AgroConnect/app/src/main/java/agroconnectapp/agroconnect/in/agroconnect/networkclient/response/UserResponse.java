package agroconnectapp.agroconnect.in.agroconnect.networkclient.response;

import agroconnectapp.agroconnect.in.agroconnect.networkclient.model.User;

/**
 * Created by shakti on 5/7/2016.
 */
public class UserResponse extends TResponse {
    User user = null;


    public User getUser() {
        return user;
    }

    public UserResponse(User user) {
        super();
        this.user = user;
    }

}
