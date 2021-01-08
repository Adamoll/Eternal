package com.eternalsrv.ui.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eternalsrv.App;
import com.eternalsrv.R;
import com.eternalsrv.fcm.GooglePlayServicesHelper;
import com.eternalsrv.ui.adapters.chat.DialogsAdapter;
import com.eternalsrv.ui.swipe.ItemTouchHelperCallback;
import com.eternalsrv.ui.swipe.UserProfileInfo;
import com.eternalsrv.ui.swipe.adapters.HorizontalListDialogsRecyclerViewAdapter;
import com.eternalsrv.utils.asynctasks.AsyncTaskParams;
import com.eternalsrv.utils.asynctasks.BaseAsyncTask;
import com.eternalsrv.utils.chat.ChatHelper;
import com.eternalsrv.utils.chat.DialogsManager;
import com.eternalsrv.utils.constant.GcmConsts;
import com.eternalsrv.utils.constant.ServerMethodsConsts;
import com.eternalsrv.utils.holders.UserProfileInfoHolder;
import com.eternalsrv.utils.qb.QbChatDialogMessageListenerImp;
import com.eternalsrv.utils.holders.QbDialogHolder;
import com.eternalsrv.utils.qb.callback.QbEntityCallbackImpl;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBSystemMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogCustomData;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.model.QBUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DialogsFragment extends Fragment implements DialogsManager.ManagingDialogsCallbacks {
    private static final String TAG = DialogsFragment.class.getSimpleName();
    public static final int REQUEST_DIALOG_ID_FOR_UPDATE = 165;

    private SwipyRefreshLayout setOnRefreshListener;
    private NestedScrollView nestedScrollView;
    private RelativeLayout noDialogsLayout;
    private RelativeLayout matchedDialogsHorizontalLayout;
    private TextView matchedDialogsHorizontalText;
    private QBRequestGetBuilder requestBuilder;

    private BroadcastReceiver pushBroadcastReceiver;
    private GooglePlayServicesHelper googlePlayServicesHelper;
    private DialogsAdapter dialogsAdapter;
    private HorizontalListDialogsRecyclerViewAdapter horizontalDialogsAdapter;
    private RecyclerView dialogsRecyclerView;
    private RecyclerView horizontalDialogsRecyclerView;
    private QBChatDialogMessageListener allDialogsMessagesListener;
    private SystemMessagesListener systemMessagesListener;
    private QBSystemMessagesManager systemMessagesManager;
    private QBIncomingMessagesManager incomingMessagesManager;
    private DialogsManager dialogsManager;

    private int skipRecords = 0;
    private boolean openDialogFromSwiping;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        googlePlayServicesHelper = new GooglePlayServicesHelper();
        pushBroadcastReceiver = new PushBroadcastReceiver();

        allDialogsMessagesListener = new AllDialogsMessageListener();
        systemMessagesListener = new SystemMessagesListener();
        dialogsManager = new DialogsManager();

        registerQbChatListeners();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.fragment_dialogs, container, false);
        initUi(viewRoot);
        initHorizontalRecyclerView(viewRoot);
        return viewRoot;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadDialogsFromQb(QbDialogHolder.getInstance().getDialogs().size() > 0, true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            boolean deleteDialog = data.getExtras().getBoolean(ChatActivity.EXTRA_DELETE_DIALOG, false);
            if(deleteDialog) {
                QBChatDialog qbChatDialog = (QBChatDialog) data.getExtras().getSerializable(ChatActivity.EXTRA_DIALOG_DATA);
                dialogsManager.sendSystemMessageAboutDeletingDialog(systemMessagesManager, qbChatDialog);
                QbDialogHolder.getInstance().deleteDialog(qbChatDialog.getDialogId());
                updateDialogsAdapter();
            }

            if (requestCode == REQUEST_DIALOG_ID_FOR_UPDATE) {
                if (data != null) {
                    String dialogId = data.getStringExtra(ChatActivity.EXTRA_DIALOG_ID);
                    loadUpdatedDialog(dialogId);
                } else {
                    updateDialogsList();
                }
            }
        } else {
            updateDialogsAdapter();
        }
    }

    private void initHorizontalRecyclerView(View viewRoot){
        LinearLayoutManager layoutManager = new LinearLayoutManager(App.getAppContext(), LinearLayoutManager.HORIZONTAL, false);
        horizontalDialogsRecyclerView = viewRoot.findViewById(R.id.list_dialogs_profile_recyclerview);
        horizontalDialogsRecyclerView.setLayoutManager(layoutManager);
        horizontalDialogsAdapter = new HorizontalListDialogsRecyclerViewAdapter(getActivity(), new ArrayList<>(QbDialogHolder.getInstance().getDialogs().values()) );
        horizontalDialogsAdapter.setHasStableIds(true);
        horizontalDialogsRecyclerView.setAdapter(horizontalDialogsAdapter);
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(horizontalDialogsAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(horizontalDialogsRecyclerView);
    }

    private void loadUpdatedDialog(final String dialogId) {
        ChatHelper.getInstance().getDialogById(dialogId, new QbEntityCallbackImpl<QBChatDialog>() {
            @Override
            public void onSuccess(final QBChatDialog result, Bundle bundle) {
                QbDialogHolder.getInstance().addDialog(result);
                ChatHelper.getInstance().getUsersFromDialog(result, new QbEntityCallbackImpl<ArrayList<QBUser>>() {
                    @Override
                    public void onSuccess(ArrayList<QBUser> users, Bundle bundle) {
                        horizontalDialogsAdapter.addToList(result);
                        updateDialogsAdapter();
                    }
                });
            }
            @Override
            public void onError(QBResponseException e) {
            }
        });
    }

    private void getUsersFromDialog(final QBChatDialog chatDialog) {
        QbDialogHolder.getInstance().addDialog(chatDialog);
        ChatHelper.getInstance().getUsersFromDialog(chatDialog, new QbEntityCallbackImpl<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> users, Bundle bundle) {
                horizontalDialogsAdapter.addToList(chatDialog);
                updateDialogsAdapter();
            }
        });
    }

    private void updateDialogsList() {
        requestBuilder.setSkip(skipRecords = 0);
        loadDialogsFromQb(true, true);
    }

    private void initUi(View view) {
        dialogsRecyclerView = view.findViewById(R.id.dialogs_recycler_view);
        setOnRefreshListener =  view.findViewById(R.id.swipy_refresh_layout);

        dialogsAdapter = new DialogsAdapter(new ArrayList<>(QbDialogHolder.getInstance().getDialogs().values()), getActivity());

        dialogsAdapter.setHasStableIds(true);
        dialogsRecyclerView.setAdapter(dialogsAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(App.getAppContext(), LinearLayoutManager.VERTICAL, false);
        dialogsRecyclerView.setLayoutManager(layoutManager);
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(dialogsAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(dialogsRecyclerView);

        requestBuilder = new QBRequestGetBuilder();

        setOnRefreshListener.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                requestBuilder.setSkip(skipRecords += ChatHelper.DIALOG_ITEMS_PER_PAGE);
                loadDialogsFromQb(true, false);
            }
        });

        noDialogsLayout = view.findViewById(R.id.no_dialogs_layout);
        nestedScrollView = view.findViewById(R.id.dialogs_scroll_view);
        matchedDialogsHorizontalLayout = view.findViewById(R.id.dialogs_matched_users_list);
        matchedDialogsHorizontalText = view.findViewById(R.id.dialogs_matched_users_text);
    }

    private void registerQbChatListeners() {
        incomingMessagesManager = QBChatService.getInstance().getIncomingMessagesManager();
        systemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();

        if (incomingMessagesManager != null) {
            incomingMessagesManager.addDialogMessageListener(allDialogsMessagesListener != null
                    ? allDialogsMessagesListener : new AllDialogsMessageListener());
        }

        if (systemMessagesManager != null) {
            systemMessagesManager.addSystemMessageListener(systemMessagesListener != null
                    ? systemMessagesListener : new SystemMessagesListener());
        }

        dialogsManager.addManagingDialogsCallbackListener(this);
    }

    private void unregisterQbChatListeners() {
        if (incomingMessagesManager != null) {
            incomingMessagesManager.removeDialogMessageListrener(allDialogsMessagesListener);
        }

        if (systemMessagesManager != null) {
            systemMessagesManager.removeSystemMessageListener(systemMessagesListener);
        }

        dialogsManager.removeManagingDialogsCallbackListener(this);
    }

    public void createNewDialog(int recipientId, int matchValue) {
        QBChatDialog dialog = DialogUtils.buildPrivateDialog(recipientId);
        QBDialogCustomData dialogCustomData = new QBDialogCustomData("DialogMatchValue");
        dialogCustomData.putInteger("matchValue", matchValue);
        dialog.setCustomData(dialogCustomData);
        QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog result, Bundle params) {
                Log.d("Chat created", result.toString());
                dialogsManager.sendSystemMessageAboutCreatingDialog(systemMessagesManager, result);
                getUsersFromDialog(result);

                if (openDialogFromSwiping){
                    openDialogFromSwiping = false;
                    ChatActivity.startForResult(getActivity(), REQUEST_DIALOG_ID_FOR_UPDATE, result);
                }
            }

            @Override
            public void onError(QBResponseException responseException) {
            }
        });
    }

    private void loadDialogsFromQb(final boolean silentUpdate, final boolean clearDialogHolder) {
//        if (!silentUpdate) {
//        }

        ChatHelper.getInstance().getDialogs(requestBuilder, new QBEntityCallback<ArrayList<QBChatDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBChatDialog> dialogs, Bundle bundle) {
                setOnRefreshListener.setRefreshing(false);

                if (clearDialogHolder) {
                    QbDialogHolder.getInstance().clear();
                }
                QbDialogHolder.getInstance().addDialogs(dialogs);
                updateDialogsAdapter();

                if (dialogs.size() > 0) {
                    String recipientsIds = getRecipientsIds(dialogs);
                    getUserDialogInfo(recipientsIds);
                }
            }

            @Override
            public void onError(QBResponseException e) {
                setOnRefreshListener.setRefreshing(false);
                Toast.makeText(App.getAppContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getRecipientsIds(ArrayList<QBChatDialog> dialogs) {
        StringBuilder builder = new StringBuilder();
        String delimeter = "";
        for (int i = 0; i < dialogs.size(); i++) {
            builder.append(delimeter);
            delimeter = ";";
            builder.append(dialogs.get(i).getRecipientId());
        }
        return builder.toString();
    }

    private void getUserDialogInfo(String userIds) {
        AsyncTaskParams params = new AsyncTaskParams();
        params.put("user_profile_ids", userIds);
        params.put("user_id", App.getPreferences().getUserId());
        GetUsersProfileInfo baseAsyncTask = new GetUsersProfileInfo(ServerMethodsConsts.USERSPROFILEINFO, params);
        baseAsyncTask.setHttpMethod("POST");
        baseAsyncTask.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        googlePlayServicesHelper.checkPlayServicesAvailable();
        LocalBroadcastManager.getInstance(App.getAppContext()).registerReceiver(pushBroadcastReceiver,
                new IntentFilter(GcmConsts.ACTION_NEW_GCM_EVENT));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(App.getAppContext()).unregisterReceiver(pushBroadcastReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterQbChatListeners();
    }

    private void updateDialogsAdapter() {
        List<QBChatDialog> emptyDialogs = new ArrayList<>();
        List<QBChatDialog> startedDialogs = new ArrayList<>();
        for (QBChatDialog dialog : QbDialogHolder.getInstance().getDialogs().values()) {
            if (dialog.getLastMessage() != null || dialog.getLastMessageDateSent() > 0)
                startedDialogs.add(dialog);
            else
                emptyDialogs.add(0, dialog);
        }

        if (emptyDialogs.size() == 0 && startedDialogs.size() > 0) {
            matchedDialogsHorizontalText.setVisibility(View.GONE);
            matchedDialogsHorizontalLayout.setVisibility(View.GONE);
        } else {
            matchedDialogsHorizontalLayout.setVisibility(View.VISIBLE);
            matchedDialogsHorizontalText.setVisibility(View.VISIBLE);
        }

        if (emptyDialogs.size() > 0 || startedDialogs.size() > 0) {
            noDialogsLayout.setVisibility(View.INVISIBLE);
            nestedScrollView.setVisibility(View.VISIBLE);
        } else {
            nestedScrollView.setVisibility(View.INVISIBLE);
            noDialogsLayout.setVisibility(View.VISIBLE);
        }

        horizontalDialogsAdapter.updateList(emptyDialogs);
        dialogsAdapter.updateList(startedDialogs);
    }

    public boolean isOpenDialogFromSwiping() {
        return openDialogFromSwiping;
    }

    public void setOpenDialogFromSwiping(boolean openDialogFromSwiping) {
        this.openDialogFromSwiping = openDialogFromSwiping;
    }

    @Override
    public void onDialogCreated(QBChatDialog chatDialog) {
        updateDialogsAdapter();
    }

    @Override
    public void onDialogUpdated(String chatDialog) {
        updateDialogsAdapter();
    }

    @Override
    public void onNewDialogLoaded(QBChatDialog chatDialog) {
        updateDialogsAdapter();
    }

    @Override
    public void onDialogDeleted(String chatDialog) {
        updateDialogsAdapter();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private class PushBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(GcmConsts.EXTRA_GCM_MESSAGE);
            Log.v(TAG, "Received broadcast " + intent.getAction() + " with data: " + message);
            requestBuilder.setSkip(skipRecords = 0);
            loadDialogsFromQb(true, true);
        }
    }

    private class SystemMessagesListener implements QBSystemMessageListener {
        @Override
        public void processMessage(final QBChatMessage qbChatMessage) {
            dialogsManager.onSystemMessageReceived(qbChatMessage);
        }

        @Override
        public void processError(QBChatException e, QBChatMessage qbChatMessage) {

        }
    }

    private class AllDialogsMessageListener extends QbChatDialogMessageListenerImp {
        @Override
        public void processMessage(final String dialogId, final QBChatMessage qbChatMessage, Integer senderId) {
            if (!senderId.equals(ChatHelper.getCurrentUser().getId())) {
                dialogsManager.onGlobalMessageReceived(dialogId, qbChatMessage);
            }
        }
    }

    private class GetUsersProfileInfo extends BaseAsyncTask{

        public GetUsersProfileInfo(String urn, AsyncTaskParams params) {
            super(urn, params);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getString("status").equals("ok")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("users_profile_info");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject user = jsonArray.getJSONObject(i);
                        UserProfileInfo profileInfo = new UserProfileInfo();
                        profileInfo.setUserId(user.getInt("user_id"));
                        profileInfo.setName(user.getString("name"));
                        profileInfo.setUserQbId(user.getInt("qb_id"));
                        profileInfo.setDescription(user.getString("description"));
                        profileInfo.setAge(user.getInt("age"));
                        profileInfo.setDistance(user.getInt("distance"));
                        profileInfo.setPhotoLinks(copyLinks(user.getString("photo_links")));
                        UserProfileInfoHolder.getInstance().putProfileInfo(profileInfo);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private List<String> copyLinks(String links) throws JSONException {
            if (links != null) {
                String[] list = links.split(";");
                return Arrays.asList(list);
            }
            return null;
        }
    }
}
