package edu.byu.cs.tweeter.server;

import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.lambda.LoginHandler;
import edu.byu.cs.tweeter.server.lambda.RegisterHandler;

public class main {
    public static void main(String args[]) {
        LoginRequest request = new LoginRequest("@a", "a");
        LoginHandler loginHandler = new LoginHandler();
        LoginResponse response = loginHandler.handleRequest(request, null);
        int x = 0;
    }
}
