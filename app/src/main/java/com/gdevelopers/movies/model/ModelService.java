package com.gdevelopers.movies.model;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.util.LongSparseArray;

import com.gdevelopers.movies.helpers.Constants;
import com.gdevelopers.movies.mappers.JActorsMapper;
import com.gdevelopers.movies.mappers.JAdvancedSearchMapper;
import com.gdevelopers.movies.mappers.JCollectionMapper;
import com.gdevelopers.movies.mappers.JCompanyMapper;
import com.gdevelopers.movies.mappers.JEpisodeDetailsMapper;
import com.gdevelopers.movies.mappers.JEpisodesMapper;
import com.gdevelopers.movies.mappers.JFavouritesMapper;
import com.gdevelopers.movies.mappers.JGenreMapper;
import com.gdevelopers.movies.mappers.JListDetailsMapper;
import com.gdevelopers.movies.mappers.JListMapper;
import com.gdevelopers.movies.mappers.JMovieDetailsMapper;
import com.gdevelopers.movies.mappers.JMoviesMapper;
import com.gdevelopers.movies.mappers.JPopularActors;
import com.gdevelopers.movies.mappers.JRatedMoviesMapper;
import com.gdevelopers.movies.mappers.JSearchMapper;
import com.gdevelopers.movies.mappers.JSearchTraktMapper;
import com.gdevelopers.movies.mappers.JSessionMapper;
import com.gdevelopers.movies.mappers.JTVDetailsMapper;
import com.gdevelopers.movies.mappers.JTokenMapper;
import com.gdevelopers.movies.mappers.JTraktRatingMapper;
import com.gdevelopers.movies.mappers.JUserMapper;
import com.gdevelopers.movies.mappers.JUserMovieStateMapper;
import com.gdevelopers.movies.mappers.JWatchlistMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class ModelService extends Service {
    private ServiceBinder binder = null;
    private final JMovieDetailsMapper movieDetailsMapper;
    private final JSearchMapper searchMapper;
    private final JTVDetailsMapper tvDetailsMapper;
    private final LongSparseArray<List<KObject>> responses;
    private ResponseListener responseListener;
    private final JGenreMapper genreMapper;
    private final JEpisodesMapper episodesMapper;
    private final JActorsMapper actorsMapper;
    private final JMoviesMapper moviesMapper;
    private final JEpisodeDetailsMapper episodeDetailsMapper;
    private final JAdvancedSearchMapper advancedSearchMapper;
    private final JTokenMapper tokenMapper;
    private final JSessionMapper sessionMapper;
    private final JUserMapper userMapper;
    private final JUserMovieStateMapper stateMapper;
    private final JFavouritesMapper favouritesMapper;
    private final JWatchlistMapper watchlistMapper;
    private final JRatedMoviesMapper ratedMoviesMapper;
    private final JPopularActors popularActors;
    private final JListMapper listMapper;
    private final JListDetailsMapper listDetailsMapper;
    private final JCompanyMapper companyMapper;
    private final JCollectionMapper collectionMapper;
    private final JSearchTraktMapper searchTraktMapper;
    private final JTraktRatingMapper traktRatingMapper;
    private Context context = this;

    public void setContext(Context context) {
        this.context = context;
    }

    public ModelService() {
        Log.d("ModelService", "ModelService()");
        responses = new LongSparseArray<>();
        Server server = new HttpServer();
        movieDetailsMapper = new JMovieDetailsMapper(context, server);
        searchMapper = new JSearchMapper(context, server);
        actorsMapper = new JActorsMapper(context, server);
        tvDetailsMapper = new JTVDetailsMapper(context, server);
        episodesMapper = new JEpisodesMapper(context, server);
        genreMapper = new JGenreMapper(server);
        moviesMapper = new JMoviesMapper(context, server);
        episodeDetailsMapper = new JEpisodeDetailsMapper(context, server);
        advancedSearchMapper = new JAdvancedSearchMapper(context, server);
        tokenMapper = new JTokenMapper(server);
        sessionMapper = new JSessionMapper(server);
        userMapper = new JUserMapper(server);
        stateMapper = new JUserMovieStateMapper(server);
        favouritesMapper = new JFavouritesMapper(context, server);
        watchlistMapper = new JWatchlistMapper(context, server);
        ratedMoviesMapper = new JRatedMoviesMapper(context, server);
        popularActors = new JPopularActors(context, server);
        listMapper = new JListMapper(server, context);
        listDetailsMapper = new JListDetailsMapper(context, server);
        companyMapper = new JCompanyMapper(server);
        collectionMapper = new JCollectionMapper(context, server);
        searchTraktMapper = new JSearchTraktMapper(server);
        traktRatingMapper = new JTraktRatingMapper(server);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("ModelService", "onBind()");
        if (binder == null)
            binder = new ServiceBinder(this);
        return binder;
    }

    public void setOnResponseListener(ResponseListener onResponseListener) {
        this.responseListener = onResponseListener;
    }

    public interface ResponseListener {
        void onResponseListener(int responseID);
    }

    abstract class AsyncCommand extends Thread {
        private final int respId;

        AsyncCommand(int responseId) {
            respId = responseId;
        }

        abstract List<KObject> doRun();

        @Override
        public void run() {
            List<KObject> b = doRun();
            synchronized (responses) {
                Long k = (long) respId;
                if (responses.get(k) != null)
                    responses.remove(k);
                responses.put((long) respId, b);
            }
            final AppCompatActivity activity = (AppCompatActivity) context;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    responseListener.onResponseListener(respId);
                }
            });
        }
    }

    public List<KObject> getResponseFor(long l) {
        Long k = l;
        synchronized (responses) {
            if (responses.get(k) == null)
                return new ArrayList<>();
            List<KObject> ret = responses.get(k);
            if (ret == null)
                ret = new ArrayList<>();
            else
                responses.remove(k);
            return ret;
        }
    }

    public void getMovieDetails(final String id) {
        class GetMovieDetailsCommand extends AsyncCommand {

            private GetMovieDetailsCommand() {
                super(Constants.MOVIE_DETAILS);
            }

            @Override
            List<KObject> doRun() {
                return movieDetailsMapper.getMovieDetails(id);
            }
        }
        new GetMovieDetailsCommand().start();
    }

    public void getListDetails(final String listId) {
        class GetListDetailsCommand extends AsyncCommand {

            private GetListDetailsCommand() {
                super(Constants.GET_LIST_DETAILS);
            }

            @Override
            List<KObject> doRun() {
                return listDetailsMapper.getListDetails(listId);
            }
        }
        new GetListDetailsCommand().start();
    }

    public void checkItemStatus(final String listId, final String movieId) {
        class GetListDetailsCommand extends AsyncCommand {

            private GetListDetailsCommand() {
                super(Constants.CHECK_ITEM_STATUS);
            }

            @Override
            List<KObject> doRun() {
                return listDetailsMapper.checkItemStatus(listId, movieId);
            }
        }
        new GetListDetailsCommand().start();
    }

    public void getActorDetails(final String id) {
        class GetActorDetailsCommand extends AsyncCommand {

            private GetActorDetailsCommand() {
                super(Constants.ACTOR_DETAILS);
            }

            @Override
            List<KObject> doRun() {
                return actorsMapper.getActorDetails(id);
            }
        }
        new GetActorDetailsCommand().start();
    }

    public void getSearch(final String type, final String query) {
        class GetSearchCommand extends AsyncCommand {

            private GetSearchCommand() {
                super(Constants.SEARCH);
            }

            @Override
            List<KObject> doRun() {
                return searchMapper.getSearch(type, query);
            }
        }
        new GetSearchCommand().start();
    }

    public void getActors(final String query) {
        class GetActorsCommand extends AsyncCommand {

            private GetActorsCommand() {
                super(Constants.SEARCH_ACTORS);
            }

            @Override
            List<KObject> doRun() {
                return searchMapper.getActors(query);
            }
        }
        new GetActorsCommand().start();
    }

    public void getTVShowDetails(final String id) {
        class GetTVShowDetailsCommand extends AsyncCommand {

            private GetTVShowDetailsCommand() {
                super(Constants.TV_SHOW_DETAILS);
            }

            @Override
            List<KObject> doRun() {
                return tvDetailsMapper.getTVShowDetails(id);
            }
        }
        new GetTVShowDetailsCommand().start();
    }

    public void getEpisodes(final String id, final String number, final boolean reload) {
        class GetEpisodesCommand extends AsyncCommand {

            private GetEpisodesCommand() {
                super(Constants.EPISODES);
            }

            @Override
            List<KObject> doRun() {
                return episodesMapper.getEpisodes(id, number, reload);
            }
        }
        new GetEpisodesCommand().start();
    }

    public void getGenres(final Context context, final boolean reload) {
        class GetGenresCommand extends AsyncCommand {

            private GetGenresCommand() {
                super(Constants.GENRES);
            }

            @Override
            List<KObject> doRun() {
                return genreMapper.getGenres(context, reload);
            }
        }
        new GetGenresCommand().start();
    }

    public void getDiscoverMovies(final String genreId, final String page, final boolean more, final boolean reload) {
        class GetDiscoverMoviesCommand extends AsyncCommand {

            private GetDiscoverMoviesCommand() {
                super(Constants.DISCOVER_MOVIES);
            }

            @Override
            List<KObject> doRun() {
                return moviesMapper.getMovies(genreId, page, more, reload);
            }
        }
        new GetDiscoverMoviesCommand().start();
    }

    public void getEpisodeDetails(final String id, final String number, final String episodeNumber, final boolean reload) {
        class GetEpisodeDetailsCommand extends AsyncCommand {

            private GetEpisodeDetailsCommand() {
                super(Constants.EPISODE_DETAILS);
            }

            @Override
            List<KObject> doRun() {
                return episodeDetailsMapper.getEpisodeDetails(id, number, episodeNumber, reload);
            }
        }
        new GetEpisodeDetailsCommand().start();
    }

    public void getAdvancedSearch(final String type, final String page, final HashMap<String, String> hashMap, final boolean more, final boolean reload) {
        class GetAdvancedSearchCommand extends AsyncCommand {

            private GetAdvancedSearchCommand() {
                super(Constants.ADVANCED_SEARCH);
            }

            @Override
            List<KObject> doRun() {
                return advancedSearchMapper.getAdvancedSearch(type, page, hashMap, more, reload);
            }
        }
        new GetAdvancedSearchCommand().start();
    }

    public void getToken() {
        class GetTokenCommand extends AsyncCommand {

            private GetTokenCommand() {
                super(Constants.GET_TOKEN);
            }

            @Override
            List<KObject> doRun() {
                return tokenMapper.getToken();
            }
        }
        new GetTokenCommand().start();
    }

    public void getSessionId(final String token) {
        class GetSessionIdCommand extends AsyncCommand {

            private GetSessionIdCommand() {
                super(Constants.GET_SESSION_ID);
            }

            @Override
            List<KObject> doRun() {
                return sessionMapper.getSessionId(token);
            }
        }
        new GetSessionIdCommand().start();
    }

    public void getMovieCollection(final String id) {
        class GetMovieCollectionCommand extends AsyncCommand {

            private GetMovieCollectionCommand() {
                super(Constants.GET_COLLECTION);
            }

            @Override
            List<KObject> doRun() {
                return collectionMapper.getMovieCollection(id);
            }
        }
        new GetMovieCollectionCommand().start();
    }

    public void getAccountDetails(final String sessionId) {
        class GetAccountDetailsCommand extends AsyncCommand {

            private GetAccountDetailsCommand() {
                super(Constants.GET_ACCOUNT_DETAILS);
            }

            @Override
            List<KObject> doRun() {
                return userMapper.getUserDetails(sessionId);
            }
        }
        new GetAccountDetailsCommand().start();
    }

    public void getMovieState(final String movieId, final String sessionId) {
        class GetMovieStateCommand extends AsyncCommand {

            private GetMovieStateCommand() {
                super(Constants.GET_MOVIE_STATE);
            }

            @Override
            List<KObject> doRun() {
                return stateMapper.getMovieState(movieId, sessionId);
            }
        }
        new GetMovieStateCommand().start();
    }

    public void getFavouriteMovies(final String accountId, final String sessionId, final String page, final boolean more, final String sortBy, final boolean reload) {
        class GetFavouriteMoviesCommand extends AsyncCommand {

            private GetFavouriteMoviesCommand() {
                super(Constants.GET_FAVOURITE_MOVIES);
            }

            @Override
            List<KObject> doRun() {
                return favouritesMapper.getFavouriteMovies(accountId, sessionId, page, more, reload, sortBy);
            }
        }
        new GetFavouriteMoviesCommand().start();
    }

    public void getWatchlistMovies(final String accountId, final String sessionId, final String page, final boolean more, final String sortBy, final boolean reload) {
        class GetWatchlistMoviesCommand extends AsyncCommand {

            private GetWatchlistMoviesCommand() {
                super(Constants.GET_WATCHLIST_MOVIES);
            }

            @Override
            List<KObject> doRun() {
                return watchlistMapper.getWatchlistMovies(accountId, sessionId, page, more, reload, sortBy);
            }
        }
        new GetWatchlistMoviesCommand().start();
    }

    public void getRatedMovies(final String accountId, final String sessionId, final String page, final boolean more, final String sortBy, final boolean reload) {
        class GetRatedMoviesCommand extends AsyncCommand {

            private GetRatedMoviesCommand() {
                super(Constants.GET_RATED_MOVIES);
            }

            @Override
            List<KObject> doRun() {
                return ratedMoviesMapper.getRatedMovies(accountId, sessionId, page, more, reload, sortBy);
            }
        }
        new GetRatedMoviesCommand().start();
    }

    public void getPopularPeople(final String page, final boolean more, final boolean reload) {
        class GetPopularPeopleCommand extends AsyncCommand {

            private GetPopularPeopleCommand() {
                super(Constants.POPULAR_PEOPLE);
            }

            @Override
            List<KObject> doRun() {
                return popularActors.getPopularActors(page, more, reload);
            }
        }
        new GetPopularPeopleCommand().start();
    }

    public void getCreatedLists(final String accountId, final String sessionId, final boolean reload) {
        class GetCreatedListsCommand extends AsyncCommand {

            private GetCreatedListsCommand() {
                super(Constants.GET_CREATED_LISTS);
            }

            @Override
            List<KObject> doRun() {
                return listMapper.getCreatedLists(accountId, sessionId, reload);
            }
        }
        new GetCreatedListsCommand().start();
    }

    public void getCompanyDetails(final Context context, final String id) {
        class GetCompanyDetailsCommand extends AsyncCommand {

            private GetCompanyDetailsCommand() {
                super(Constants.COMPANY_DETAILS);
            }

            @Override
            List<KObject> doRun() {
                return companyMapper.getCompanyDetails(context, id);
            }
        }
        new GetCompanyDetailsCommand().start();
    }

    public void getTraktMovieId(final String id) {
        class GetTraktMovieId extends AsyncCommand {

            private GetTraktMovieId() {
                super(Constants.SEARCH_TRAKT);
            }

            @Override
            List<KObject> doRun() {
                return searchTraktMapper.getTraktMovieId(id);
            }
        }
        new GetTraktMovieId().start();
    }

    public void getTraktRatings(final String id) {
        class GetTraktRatings extends AsyncCommand {

            private GetTraktRatings() {
                super(Constants.TRAKT_RATINGS);
            }

            @Override
            List<KObject> doRun() {
                return traktRatingMapper.getTraktRatings(id);
            }
        }
        new GetTraktRatings().start();
    }
}
