package Blog.model;

public class Errors {
    static public enum Register_Error {
        RegisterError, UsernameError, PasswordError, Null, Success
    }

    static public enum Post_Error {
        InvalidLength, InvalidTitle, Success, Media, IO, Fraud, Internal
    }

    static public enum Like_Error {
        Existing, Success, InvalidUser, InvalidPost
    }

    static public enum Comment_Error {
        InvalidLength, Success, Internal, InvalidUser, InvalidPost
    }

    static public enum Profile_Error {
        Posts, Followers, Following, Me, InvalidInfo
    }

    static public enum ReportReason {
        SPAM,
        HARASSMENT,
        INAPPROPRIATE,
        COPYRIGHT,
        MISINFORMATION,
        OTHER;

    }

    static public enum ReportError {
        Existing, Success, InvalidUser, InvalidPost, InvalidReason
    }

}
