package pl.fzymek.imagegallery.network;

import pl.fzymek.gettyimagesmodel.gettyimages.GettySearchResult;
import pl.fzymek.imagegallery.config.Config;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by filip on 30.09.2016.
 */
public interface GettyImagesService {

    @Headers("Api-Key: " + Config.GETTYIMAGES_CONSUMER_KEY)
    @GET("search/images/?fields=detail_set,display_set&sort_order=best_match")
    Observable<GettySearchResult> getImages(@Query("phrase") String phrase);

}
