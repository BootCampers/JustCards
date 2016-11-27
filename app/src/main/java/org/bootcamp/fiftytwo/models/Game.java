package org.bootcamp.fiftytwo.models;

import com.google.gson.Gson;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.bootcamp.fiftytwo.utils.Constants;

import java.util.List;

/**
 * Created by baphna on 11/26/2016.
 */
@ParseClassName("Game")
public class Game extends ParseObject{

    public Game() {
        super();
    }

    public void addPlayer(User user){
        Gson gson = new Gson();
        put(Constants.PARAMS_PLAYER_GAME, gson.toJson(user));
    }

    public void removePlayer(User player){
        List<User> players = getList(Constants.PARAMS_PLAYER_GAME);
        for(User user: players){
            if(user.getUserId().equals(player.getUserId())){
                players.remove(user);
            }
        }
    }

    public void setGameName (String gameName){
        put(Constants.PARAM_GAME_NAME, gameName);
    }

    public String getGameName (){
        return getString(Constants.PARAM_GAME_NAME);
    }
}
