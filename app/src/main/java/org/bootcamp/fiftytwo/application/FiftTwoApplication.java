package org.bootcamp.fiftytwo.application;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.interceptors.ParseLogInterceptor;

import org.bootcamp.fiftytwo.models.User;

/**
 * Created by baphna on 11/18/2016.
 */
public class FiftTwoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(User.class);


        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("fiftyTwoAppId") // should correspond to APP_ID env variable
                .clientKey(null)  // set explicitly unless clientKey is explicitly configured on Parse server
                .addNetworkInterceptor(new ParseLogInterceptor())
                .server("https://fiftytwo.herokuapp.com/parse/").build());
    }
}
