package org.bootcamp.fiftytwo.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.bootcamp.fiftytwo.R;
import org.bootcamp.fiftytwo.application.ChatApplication;
import org.bootcamp.fiftytwo.services.AllJoynService;

import java.util.List;

public class CreateJoinGameActivity extends AppCompatActivity {

    Button joinGameButton;
    Button createGameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_join_game);

        joinGameButton = (Button) findViewById(R.id.joinGameButton);
        createGameButton = (Button) findViewById(R.id.createGameButton);

        joinGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(CreateJoinGameActivity.this);
                dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.usejoindialog);

                ArrayAdapter<String> channelListAdapter = new ArrayAdapter<String>(((ChatApplication)getApplication()),
                        android.R.layout.test_list_item);
                final ListView channelList = (ListView)dialog.findViewById(R.id.useJoinChannelList);
                channelList.setAdapter(channelListAdapter);

                List<String> channels = ((ChatApplication)getApplication()).getFoundChannels();
                for (String channel : channels) {
                    if (channel.length() > AllJoynService.NAME_PREFIX.length() + 1) {
                        channelListAdapter.add(channel.substring(AllJoynService.NAME_PREFIX.length() + 1));
                    }
                }
                channelListAdapter.notifyDataSetChanged();

                channelList.setOnItemClickListener(new ListView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String name = channelList.getItemAtPosition(position).toString();
                        ((ChatApplication)getApplication()).useSetChannelName(name);
                        ((ChatApplication)getApplication()).useJoinChannel();

                    }
                });

                Button cancel = (Button)dialog.findViewById(R.id.useJoinCancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {

                    }
                });
                dialog.show();
                //startActivity(new Intent(CreateJoinGameActivity.this, GameViewManagerActivity.class));
            }
        });

        createGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateJoinGameActivity.this, CreateGameActivity.class));
            }
        });
    }

    public Dialog createUseJoinDialog(final AppCompatActivity activity, final ChatApplication application) {
        //Log.i(TAG, "createUseJoinDialog()");
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.usejoindialog);

        ArrayAdapter<String> channelListAdapter = new ArrayAdapter<String>(activity, android.R.layout.test_list_item);
        final ListView channelList = (ListView)dialog.findViewById(R.id.useJoinChannelList);
        channelList.setAdapter(channelListAdapter);

        List<String> channels = application.getFoundChannels();
        for (String channel : channels) {
            if (channel.length() > AllJoynService.NAME_PREFIX.length() + 1) {
                channelListAdapter.add(channel.substring(AllJoynService.NAME_PREFIX.length() + 1));
            }
        }
        channelListAdapter.notifyDataSetChanged();

        channelList.setOnItemClickListener(new ListView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = channelList.getItemAtPosition(position).toString();
                application.useSetChannelName(name);
                application.useJoinChannel();

            }
        });

        Button cancel = (Button)dialog.findViewById(R.id.useJoinCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

            }
        });

        return dialog;
    }
}
