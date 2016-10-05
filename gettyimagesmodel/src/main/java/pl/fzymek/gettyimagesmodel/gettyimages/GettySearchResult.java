
package pl.fzymek.gettyimagesmodel.gettyimages;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Filip Zymek on 2015-06-08.
 */
public class GettySearchResult {

    @SerializedName("result_count")
    Integer resultCount;
    @SerializedName("images")
    List<Image> images;

    public Integer getResultCount() {
        return resultCount;
    }

    public List<Image> getImages() {
        return images;
    }

    @Override
    public String toString() {
        return "Result{" +
                "resultCount=" + resultCount +
                ", images=" + images +
                '}';
    }
}