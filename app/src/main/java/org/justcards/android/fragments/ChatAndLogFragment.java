package org.justcards.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import org.justcards.android.R;
import org.justcards.android.adapters.ChatAndLogAdapter;
import org.justcards.android.models.ChatLog;
import org.justcards.android.models.User;
import org.justcards.android.views.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static org.justcards.android.utils.Constants.ARG_COLUMN_COUNT;

public class ChatAndLogFragment extends Fragment {

    private List<ChatLog> chatLogs = new ArrayList<>();
    private ChatAndLogAdapter chatAndLogAdapter;
    private OnChatAndLogListener mListener;
    private Unbinder unbinder;
    private int mColumnCount = 1;

    @BindView(R.id.rvChatLog) RecyclerView recyclerView;
    @BindView(R.id.etChatMsg) EditText etNewMessage;

    public interface OnChatAndLogListener {
        void onChat(ChatLog item);
    }

    public static ChatAndLogFragment newInstance(int columnCount) {
        ChatAndLogFragment fragment = new ChatAndLogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatandlog, container, false);
        unbinder = ButterKnife.bind(this, view);

        // Set the adapter
        Context context = view.getContext();
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);
        chatAndLogAdapter = new ChatAndLogAdapter(chatLogs, mListener);
        recyclerView.setAdapter(chatAndLogAdapter);
        recyclerView.smoothScrollToPosition(chatLogs.size());

        etNewMessage.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                User self = User.getCurrentUser(getActivity());
                addNewLogEvent(self.getDisplayName(), self.getAvatarUri(), v.getText().toString());
                ChatLog chatLog = new ChatLog(self.getDisplayName(), self.getAvatarUri(), v.getText().toString());
                mListener.onChat(chatLog); //send to activity for broadcast
                etNewMessage.setText("");
                etNewMessage.clearFocus();
                handled = true;
            }
            return handled;
        });

        return view;
    }

    public void addNewLogEvent(String whoPosted, String fromAvatar, String details) {
        ChatLog chatLog = new ChatLog(whoPosted, fromAvatar, details);
        chatLogs.add(chatLog);
        if (chatAndLogAdapter != null && recyclerView != null) {
            chatAndLogAdapter.notifyItemInserted(chatLogs.size() + 1);
            recyclerView.smoothScrollToPosition(chatLogs.size());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnChatAndLogListener) {
            mListener = (OnChatAndLogListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnChatAndLogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}