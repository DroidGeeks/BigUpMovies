package bankzworld.movies.injection;

import javax.inject.Singleton;

import bankzworld.movies.activity.DetailsActivity;
import bankzworld.movies.activity.FavouriteActivity;
import bankzworld.movies.activity.MainActivity;
import bankzworld.movies.activity.ReviewActivity;
import bankzworld.movies.activity.SettingsActivity;
import bankzworld.movies.activity.YoutubeActivity;
import bankzworld.movies.fragment.MainPreferenceFragment;
import bankzworld.movies.viewmodel.MoviesCategoryViewmodel;
import bankzworld.movies.viewmodel.ReviewViewmodel;
import bankzworld.movies.viewmodel.TrailerViewModel;
import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(DaggerApplication daggerApplication);

    void inject(MoviesCategoryViewmodel moviesCategoryViewmodel);

    void inject(TrailerViewModel trailerViewModel);

    void inject(DetailsActivity detailsActivity);

    void inject(MainPreferenceFragment mainPreferenceFragment);

    void inject(MainActivity mainActivity);

    void inject(SettingsActivity settingsActivity);

    void inject(YoutubeActivity youtubeActivity);

    void inject(FavouriteActivity favouriteActivity);

    void inject(ReviewViewmodel reviewViewmodel);

    void inject(ReviewActivity reviewActivity);

}
