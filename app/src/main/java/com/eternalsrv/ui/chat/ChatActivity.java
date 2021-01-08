package com.eternalsrv.ui.chat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eternalsrv.App;
import com.eternalsrv.R;
import com.eternalsrv.ui.ProfileActivity;
import com.eternalsrv.ui.adapters.chat.AttachmentPreviewAdapter;
import com.eternalsrv.ui.adapters.chat.ChatAdapter;
import com.eternalsrv.ui.interpolator.SpringInterpolator;
import com.eternalsrv.ui.swipe.UserProfileInfo;
import com.eternalsrv.ui.widget.AttachmentPreviewAdapterView;
import com.eternalsrv.ui.widget.StrokedTextView;
import com.eternalsrv.ui.widget.circularprogressindicator.CircularProgressIndicator;
import com.eternalsrv.utils.PushUtils;
import com.eternalsrv.utils.TimeUtils;
import com.eternalsrv.utils.Toaster;
import com.eternalsrv.utils.chat.ChatHelper;
import com.eternalsrv.utils.holders.UserProfileInfoHolder;
import com.eternalsrv.utils.imagepick.ImagePickHelper;
import com.eternalsrv.utils.imagepick.OnImagePickedListener;
import com.eternalsrv.utils.qb.PaginationHistoryListener;
import com.eternalsrv.utils.qb.QbChatDialogMessageListenerImp;
import com.eternalsrv.utils.holders.QbDialogHolder;
import com.eternalsrv.utils.qb.QbDialogUtils;
import com.eternalsrv.utils.qb.VerboseQbChatConnectionListener;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import static com.eternalsrv.ui.swipe.UserSwipeProfileAdapter.EXTRA_SWIPE_VIEW_SOURCE;
import static com.eternalsrv.ui.swipe.UserSwipeProfileAdapter.EXTRA_USER_PROFILE;

public class ChatActivity extends BaseActivity implements OnImagePickedListener {
    private static final String TAG = ChatActivity.class.getSimpleName();
    private static final int REQUEST_CODE_ATTACHMENT = 721;

    private static final String PROPERTY_SAVE_TO_HISTORY = "save_to_history";
    public static final String EXTRA_DIALOG_ID = "dialogId";
    public static final String EXTRA_DELETE_DIALOG = "deleteDialog";
    public static final String EXTRA_DIALOG_DATA = "dialogData";

    private ProgressBar progressBar;
    private StickyListHeadersListView messagesListView;
    private EditText messageEditText;

    private RelativeLayout emptyChatLayout;
    private TextView emptyChatMatchText;
    private TextView emptyChatTimeText;
    private CircleImageView emptyChatCircleImageView;
    private StrokedTextView emptyChatMatchValueText;
    private CircularProgressIndicator emptyChatIndicator;

    private LinearLayout attachmentPreviewContainerLayout;
    private Snackbar snackbar;

    private ChatAdapter chatAdapter;
    private AttachmentPreviewAdapter attachmentPreviewAdapter;
    private ConnectionListener chatConnectionListener;

    private QBChatDialog qbChatDialog;
    private ArrayList<QBChatMessage> unShownMessages;
    private ChatMessageListener chatMessageListener;

    private View.OnClickListener openProfileActivityOnClickListener;
    private int skipPagination = 0;

    public static void startForResult(Activity activity, int code, QBChatDialog dialogId) {
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_DIALOG_ID, dialogId);
        activity.startActivityForResult(intent, code);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Log.v(TAG, "onCreate ChatActivity on Thread ID = " + Thread.currentThread().getId());
        qbChatDialog = (QBChatDialog) getIntent().getSerializableExtra(EXTRA_DIALOG_ID);
        Log.v(TAG, "deserialized dialog = " + qbChatDialog);
        qbChatDialog.initForChat(QBChatService.getInstance());

        chatMessageListener = new ChatMessageListener();
        qbChatDialog.addMessageListener(chatMessageListener);
        initChatConnectionListener();
        initViews();
        initChat();
    }

    private void sendDialogId() {
        Intent result = new Intent();
        result.putExtra(EXTRA_DIALOG_ID, qbChatDialog.getDialogId());
        setResult(RESULT_OK, result);
    }

    @Override
    public void onImagePicked(int requestCode, File file) {
        switch (requestCode) {
            case REQUEST_CODE_ATTACHMENT:
                attachmentPreviewAdapter.add(file);
                break;
        }
    }

    @Override
    public void onImagePickError(int requestCode, Exception e) {
        showErrorSnackbar(0, e, null);
    }

    @Override
    public void onImagePickClosed(int requestCode) {
    }

    @Override
    protected View getSnackbarAnchorView() {
        return findViewById(R.id.list_chat_messages);
    }

    public void onSendChatClick(View view) {
        int totalAttachmentsCount = attachmentPreviewAdapter.getCount();
        Collection<QBAttachment> uploadedAttachments = attachmentPreviewAdapter.getUploadedAttachments();
        if (!uploadedAttachments.isEmpty()) {
            if (uploadedAttachments.size() == totalAttachmentsCount) {
                for (QBAttachment attachment : uploadedAttachments) {
                    sendChatMessage(null, attachment);
                }
            } else {
                Toaster.shortToast(R.string.chat_wait_for_attachments_to_upload);
            }
        }

        String text = messageEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(text)) {
            sendChatMessage(text, null);
        }
    }

    public void onAttachmentsClick(View view) {
        new ImagePickHelper().pickAnImage(this, REQUEST_CODE_ATTACHMENT);
    }

    public void showMessage(QBChatMessage message) {
        if (emptyChatLayout.getVisibility() == View.VISIBLE)
            emptyChatLayout.setVisibility(View.GONE);
        if (chatAdapter != null) {
            chatAdapter.add(message);
            scrollMessageListDown();
        } else {
            if (unShownMessages == null) {
                unShownMessages = new ArrayList<>();
            }
            unShownMessages.add(message);
        }
    }

    private void initViews() {
        actionBar.setDisplayHomeAsUpEnabled(true);

        messagesListView = _findViewById(R.id.list_chat_messages);
        messageEditText = _findViewById(R.id.edit_chat_message);
        progressBar = _findViewById(R.id.progress_chat);

        emptyChatLayout = _findViewById(R.id.empty_chat_layout);
        emptyChatMatchText = _findViewById(R.id.empty_chat_layout_match_text);
        emptyChatTimeText = _findViewById(R.id.empty_chat_layout_match_time);
        emptyChatCircleImageView = _findViewById(R.id.empty_chat_layout_circle_image_view);
        emptyChatMatchValueText = _findViewById(R.id.empty_chat_layout_match_value);
        emptyChatIndicator = _findViewById(R.id.empty_chat_layout_indicator);

        attachmentPreviewContainerLayout = _findViewById(R.id.layout_attachment_preview_container);

        attachmentPreviewAdapter = new AttachmentPreviewAdapter(this,
                new AttachmentPreviewAdapter.OnAttachmentCountChangedListener() {
                    @Override
                    public void onAttachmentCountChanged(int count) {
                        attachmentPreviewContainerLayout.setVisibility(count == 0 ? View.GONE : View.VISIBLE);
                    }
                },
                new AttachmentPreviewAdapter.OnAttachmentUploadErrorListener() {
                    @Override
                    public void onAttachmentUploadError(QBResponseException e) {
                        showErrorSnackbar(0, e, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onAttachmentsClick(v);
                            }
                        });
                    }
                });
        AttachmentPreviewAdapterView previewAdapterView = _findViewById(R.id.adapter_view_attachment_preview);
        previewAdapterView.setAdapter(attachmentPreviewAdapter);

        openProfileActivityOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                UserProfileInfo userProfileInfo = UserProfileInfoHolder.getInstance().getProfileInfo(qbChatDialog.getRecipientId());
                userProfileInfo.setMatchValue(qbChatDialog.getCustomData().getInteger("matchValue"));
                intent.putExtra(EXTRA_USER_PROFILE, userProfileInfo);
                intent.putExtra(EXTRA_SWIPE_VIEW_SOURCE, false);
                startActivity(intent);
            }
        };
    }

    private void sendChatMessage(String text, QBAttachment attachment) {
        QBChatMessage chatMessage = new QBChatMessage();
        if (attachment != null) {
            chatMessage.addAttachment(attachment);
        } else {
            chatMessage.setBody(text);
        }
        chatMessage.setProperty(PROPERTY_SAVE_TO_HISTORY, "1");
        chatMessage.setDateSent(System.currentTimeMillis() / 1000);
        chatMessage.setMarkable(true);

        try {
            qbChatDialog.sendMessage(chatMessage);
            PushUtils.sendPushAboutMessage(qbChatDialog, text);
            showMessage(chatMessage);

            if (attachment != null) {
                attachmentPreviewAdapter.remove(attachment);
            } else {
                messageEditText.setText("");
            }
        } catch (SmackException.NotConnectedException e) {
            Log.w(TAG, e);
            Toaster.shortToast("Can't send a message, You are not connected to chat");
        }
    }

    private void initChat() {
        switch (qbChatDialog.getType()) {
            case PRIVATE:
                loadDialogUsers();
                break;

            default:
                Toaster.shortToast(String.format("%s %s", getString(R.string.chat_unsupported_type), qbChatDialog.getType().name()));
                finish();
                break;
        }
    }


    private void releaseChat() {
        qbChatDialog.removeMessageListrener(chatMessageListener);
    }

    private void updateDialog(final ArrayList<QBUser> selectedUsers) {
        ChatHelper.getInstance().updateDialogUsers(qbChatDialog, selectedUsers,
                new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog dialog, Bundle args) {
                        qbChatDialog = dialog;
                        loadDialogUsers();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        showErrorSnackbar(R.string.chat_info_add_people_error, e,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        updateDialog(selectedUsers);
                                    }
                                });
                    }
                }
        );
    }

    private void loadDialogUsers() {
        ChatHelper.getInstance().getUsersFromDialog(qbChatDialog, new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> users, Bundle bundle) {
                setChatNameToActionBar();
                loadChatHistory();
            }

            @Override
            public void onError(QBResponseException e) {
                showErrorSnackbar(R.string.chat_load_users_error, e,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadDialogUsers();
                            }
                        });
            }
        });
    }

    private void setChatNameToActionBar() {
        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.action_bar_chat, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        viewActionBar.findViewById(R.id.actionbar_layout).setOnClickListener(openProfileActivityOnClickListener);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText(QbDialogUtils.getDialogName(qbChatDialog));
        CircleImageView actionBarImageView = viewActionBar.findViewById(R.id.actionbar_imageview);

        String photoLink = null;
        UserProfileInfo userProfileInfo = UserProfileInfoHolder.getInstance().getProfileInfo(qbChatDialog.getRecipientId());
        if (userProfileInfo != null)
            photoLink = UserProfileInfoHolder.getInstance().getProfileInfo(qbChatDialog.getRecipientId()).getPhotoLinks().get(0);
        else {
            photoLink = QbDialogUtils.getQBUserPhotos(qbChatDialog).get(0);
        }
        App.getImageLoader().downloadImage(photoLink, actionBarImageView);
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        abar.setDisplayHomeAsUpEnabled(true);
        abar.setIcon(R.color.transparent);
        abar.setHomeButtonEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.primary_purple), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    private void showEmptyChatView() {
        String photoLink = UserProfileInfoHolder.getInstance().getProfileInfo(qbChatDialog.getRecipientId()).getPhotoLinks().get(0);
        emptyChatMatchText.setText(String.format("%s %s", getString(R.string.chat_matched_with_message), QbDialogUtils.getDialogName(qbChatDialog)));
        emptyChatTimeText.setText(TimeUtils.getTimeSpan(qbChatDialog.getCreatedAt()));
        App.getImageLoader().downloadImage(photoLink, emptyChatCircleImageView);
        emptyChatCircleImageView.setOnClickListener(openProfileActivityOnClickListener);
        emptyChatIndicator.setProgress(qbChatDialog.getCustomData().getInteger("matchValue"), 100);
        emptyChatMatchValueText.setText(qbChatDialog.getCustomData().getInteger("matchValue").toString() + "%");

        Animation emptyChatAnim = AnimationUtils.loadAnimation(App.getAppContext(), R.anim.empty_chat_elements);
        emptyChatAnim.setInterpolator(new SpringInterpolator(0.4f));
        emptyChatLayout.setVisibility(View.VISIBLE);
        emptyChatLayout.startAnimation(emptyChatAnim);
    }

    private void loadChatHistory() {
        ChatHelper.getInstance().loadChatHistory(qbChatDialog, skipPagination, new QBEntityCallback<ArrayList<QBChatMessage>>() {
            @Override
            public void onSuccess(ArrayList<QBChatMessage> messages, Bundle args) {
                Collections.reverse(messages);
                if (chatAdapter == null) {
                    chatAdapter = new ChatAdapter(ChatActivity.this, qbChatDialog, messages);
                    chatAdapter.setPaginationHistoryListener(new PaginationHistoryListener() {
                        @Override
                        public void downloadMore() {
                            loadChatHistory();
                        }
                    });
                    chatAdapter.setOnItemInfoExpandedListener(new ChatAdapter.OnItemInfoExpandedListener() {
                        @Override
                        public void onItemInfoExpanded(final int position) {
                            if (isLastItem(position)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        messagesListView.setSelection(position);
                                    }
                                });
                            } else {
                                messagesListView.smoothScrollToPosition(position);
                            }
                        }

                        private boolean isLastItem(int position) {
                            return position == chatAdapter.getCount() - 1;
                        }
                    });
                    if (unShownMessages != null && !unShownMessages.isEmpty()) {
                        List<QBChatMessage> chatList = chatAdapter.getList();
                        for (QBChatMessage message : unShownMessages) {
                            if (!chatList.contains(message)) {
                                chatAdapter.add(message);
                            }
                        }
                    }
                    messagesListView.setAdapter(chatAdapter);
                    messagesListView.setAreHeadersSticky(false);
                    messagesListView.setDivider(null);
                } else {
                    chatAdapter.addList(messages);
                    messagesListView.setSelection(messages.size());
                }
                progressBar.setVisibility(View.GONE);
                if (messagesListView.getCount() == 0) {
                    showEmptyChatView();
                }
            }

            @Override
            public void onError(QBResponseException e) {
                progressBar.setVisibility(View.GONE);
                skipPagination -= ChatHelper.CHAT_HISTORY_ITEMS_PER_PAGE;
                snackbar = showErrorSnackbar(R.string.connection_error, e, null);
            }
        });
        skipPagination += ChatHelper.CHAT_HISTORY_ITEMS_PER_PAGE;
    }

    private void scrollMessageListDown() {
        messagesListView.setSelection(messagesListView.getCount() - 1);
    }

    private void deleteChat() {
        ChatHelper.getInstance().deleteDialog(qbChatDialog, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                Intent intentDeleteDialog = new Intent();
                intentDeleteDialog.putExtra(EXTRA_DELETE_DIALOG, true);
                intentDeleteDialog.putExtra(EXTRA_DIALOG_DATA, qbChatDialog);
                setResult(RESULT_OK, intentDeleteDialog);
                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                showErrorSnackbar(R.string.dialogs_deletion_error, e,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteChat();
                            }
                        });
            }
        });
    }

    private void initChatConnectionListener() {
        chatConnectionListener = new VerboseQbChatConnectionListener(getSnackbarAnchorView()) {
            @Override
            public void reconnectionSuccessful() {
                super.reconnectionSuccessful();
                skipPagination = 0;
                switch (qbChatDialog.getType()) {
                    case GROUP:
                        chatAdapter = null;
                        // Join active room if we're in Group Chat
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                              //  joinGroupChat();
                            }
                        });
                        break;
                }
            }
        };
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        if (qbChatDialog != null) {
            outState.putString(EXTRA_DIALOG_ID, qbChatDialog.getDialogId());
        }
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (qbChatDialog == null) {
            qbChatDialog = QbDialogHolder.getInstance().getChatDialogById(savedInstanceState.getString(EXTRA_DIALOG_ID));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_chat, menu);

        MenuItem menuItemDelete = menu.findItem(R.id.menu_chat_action_delete);
        menuItemDelete.setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case R.id.menu_chat_action_delete:
                deleteChat();
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ChatHelper.getInstance().addConnectionListener(chatConnectionListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ChatHelper.getInstance().removeConnectionListener(chatConnectionListener);
    }

    @Override
    public void onBackPressed() {
        releaseChat();
        sendDialogId();

        super.onBackPressed();
    }

    public class ChatMessageListener extends QbChatDialogMessageListenerImp {
        @Override
        public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
            showMessage(qbChatMessage);
        }
    }
}
