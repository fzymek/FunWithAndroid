package pl.fzymek.imagegallery.network;

import pl.fzymek.imagegallery.config.Config;
import pl.fzymek.imagegallery.model.SearchResponse;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by filip on 29.09.2016.
 */
public interface PixabayService {

    @GET("?key="+ Config.PIXABAY_API_KEY)
    Observable<SearchResponse> search(@Query("q") String query);

}
