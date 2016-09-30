
package pl.fzymek.imagegallery.model.gettyimages;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Filip Zymek on 2015-06-08.
 */
public class Image {

    @SerializedName("id")
    String id;
    @SerializedName("caption")
    String caption;
    @SerializedName("title")
    String title;
    @SerializedName("artist")
    String artist;
    @SerializedName("collection_name")
    String collectionName;
    @SerializedName("date_created")
    String dateCreated;
    @SerializedName("display_sizes")
    List<DisplaySize> displaySizes;

    public String getId() {
        return id;
    }

    public String getCaption() {
        return caption;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public List<DisplaySize> getDisplaySizes() {
        return displaySizes;
    }

    public DisplaySize getDisplayByType(DisplaySizeType type) {
        for (DisplaySize size : displaySizes) {
            if (size.getName().equals(type.name)) {
                return size;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id='" + id + '\'' +
                ", caption='" + caption + '\'' +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", collectionName='" + collectionName + '\'' +
                ", dateCreated='" + dateCreated + '\'' +
                ", displaySizes=" + displaySizes +
                '}';
    }

    public enum DisplaySizeType {
        THUMB("thumb"),
        PREVIEW("preview"),
        LARGE("comp");

        protected final String name;

        DisplaySizeType(String type) {
            this.name= type;
        }

    }
}