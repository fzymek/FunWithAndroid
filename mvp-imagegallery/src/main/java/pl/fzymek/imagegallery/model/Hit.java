package pl.fzymek.imagegallery.model;


import com.google.gson.annotations.SerializedName;

public class Hit {

    @SerializedName("id")
    int id;
    @SerializedName("previewURL")
    String previewURL;
    @SerializedName("likes")
    int likes;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPreviewURL() {
        return previewURL;
    }

    public void setPreviewURL(String previewURL) {
        this.previewURL = previewURL;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
