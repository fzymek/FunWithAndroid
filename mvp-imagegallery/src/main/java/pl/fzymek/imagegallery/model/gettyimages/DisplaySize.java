
package pl.fzymek.imagegallery.model.gettyimages;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Filip Zymek on 2015-06-09.
 */
public class DisplaySize {
    @SerializedName("name")
    String name;
    @SerializedName("uri")
    String uri;

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return "DisplaySize{" +
                "name='" + name + '\'' +
                ", uri='" + uri + '\'' +
                '}';
    }
}