package bankzworld.movies.injection;

import android.app.Application;
import android.content.Context;

public class DaggerApplication extends Application {
    AppComponent appComponent;
    static Context context;
    static DaggerApplication daggerApplication;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this.getApplicationContext();
        daggerApplication = this;

        appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();

        appComponent.inject(this);
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public static Context getAppContext(){
        return context;
    }

    public static DaggerApplication getDaggerApplication() {
        return daggerApplication;
    }
}