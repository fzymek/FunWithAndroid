
package pl.fzymek.gettyimagesmodel.gettyimages;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Filip Zymek on 2015-06-09.
 */
public class DisplaySize implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.uri);
    }

    public DisplaySize() {
    }

    protected DisplaySize(Parcel in) {
        this.name = in.readString();
        this.uri = in.readString();
    }

    public static final Creator<DisplaySize> CREATOR = new Creator<DisplaySize>() {
        @Override
        public DisplaySize createFromParcel(Parcel source) {
            return new DisplaySize(source);
        }

        @Override
        public DisplaySize[] newArray(int size) {
            return new DisplaySize[size];
        }
    };
}