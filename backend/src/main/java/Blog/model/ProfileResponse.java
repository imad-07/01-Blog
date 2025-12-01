package Blog.model;

import java.util.List;

public class ProfileResponse {
    private Profile profile;
    private List<String> error;

    public ProfileResponse(Profile profile, List<String> error) {
        this.error = error;
        this.profile = profile;
    }

    public List<String> getError() {
        return error;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setError(List<String> error) {
        this.error = error;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
