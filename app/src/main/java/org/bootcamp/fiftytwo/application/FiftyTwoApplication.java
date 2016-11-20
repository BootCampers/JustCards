package org.bootcamp.fiftytwo.application;

import android.app.Application;

import com.parse.Parse;
import com.parse.interceptors.ParseLogInterceptor;

/**
 * Created by baphna on 11/18/2016.
 */
public class FiftyTwoApplication extends Application {

    public static final String APPLICATION_ID = "codepath-android";
    public static final String APPLICATION_SERVER = "https://codepath-maps-push-lab.herokuapp.com/parse/";
    public static final String CLIENT_KEY = "8bXPznF5eSLWq0sY9gTUrEF5BJlia7ltmLQFRh";

    @Override
    public void onCreate() {
        super.onCreate();

//        ParseObject.registerSubclass(User.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(APPLICATION_ID)
                .clientKey(null)
                .addNetworkInterceptor(new ParseLogInterceptor())
                .server(APPLICATION_SERVER)
                .build());
    }
}
