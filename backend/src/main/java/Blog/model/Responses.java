package Blog.model;

public class Responses {
    public static class UserResponse {
        String response;
        String id;
        String username;
        String jwt;
        public String getId() {
            return id;
        }
        public String getJwt() {
            return jwt;
        }
        public String getUsername() {
            return username;
        }
        public String getResponse() {
            return response;
        }
        public UserResponse(String response, String id, String username, String jwt) {
            this.response = response;
            this.id = id;
            this.username = username;
            this.jwt = jwt;
        }

        public UserResponse(String response) {
            this.response = response;
        }
    }

}
