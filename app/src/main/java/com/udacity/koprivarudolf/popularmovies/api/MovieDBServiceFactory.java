package com.udacity.koprivarudolf.popularmovies.api;

import com.udacity.koprivarudolf.popularmovies.BuildConfig;
import com.udacity.koprivarudolf.popularmovies.Constants;

import java.io.IOException;
import java.util.Locale;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by rudolfkopriva on 04.03.17.
 */

public class MovieDBServiceFactory {

    /**
     * Create or return Retrofit service instance
     * @return Returns instance of IMovieDBService
     */
    public static IMovieDBService getMovieDBService() {
        //Read API key and check if it was configured
        String apiKey = BuildConfig.THEMOVIEDB_APIKEY;
        //Here I'm checking if reviewer configured the API key in bundle.gradle
        if (apiKey.equals("[YOUR API KEY]")) {
            throw new RuntimeException("Please setup the THEMOVIEDB_APIKEY in gradle.properties");
        }

        //Add interceptor to append api_key and locale for each request
        final String apiKeyValue = apiKey;
        OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();

                HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter("api_key", apiKeyValue)
                        .addQueryParameter("language",
                                String.format(Locale.US, "%s-%s",
                                        Locale.getDefault().getLanguage(),
                                        Locale.getDefault().getCountry()))
                        .build();

                //Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .url(url);

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.MOVIE_DB_API)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        return retrofit.create(IMovieDBService.class);
    }

}
