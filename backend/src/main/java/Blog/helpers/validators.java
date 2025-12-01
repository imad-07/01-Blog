package Blog.helpers;

import java.io.File;

public class validators {
    private static  final String myDir = "src/main/resources/avatars/";

    public static boolean ValidatePassword(String password) {
        return true;
    }
    public static boolean ValidateContent(String content){
        return content.length() >= 2 && content.length() <= 2500;
    }
    public static boolean Validatetitle(String title){
        return title.length() >= 3 && title.length() <= 30;
    }
    public static boolean ValidateAvatar(String avatar){
        File file = new File(myDir + avatar.replace("http:/localhost:8080/avatars/", ""));
        return file.exists();
    }
    public static String getFileType(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "other";
        }

        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

        String[] imageExtensions = {"jpg", "jpeg", "png", "gif", "bmp", "webp"};
        String[] videoExtensions = {"mp4", "mov", "avi", "mkv", "webm", "ogg"};

        for (String ext : imageExtensions) {
            if (extension.equals(ext)) {
                return "image";
            }
        }

        for (String ext : videoExtensions) {
            if (extension.equals(ext)) {
                return "video";
            }
        }

        return "other";
    }
    
}


