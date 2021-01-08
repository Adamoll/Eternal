package com.eternalsrv.ui.adapters.chat;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eternalsrv.App;
import com.eternalsrv.R;
import com.eternalsrv.ui.chat.ChatActivity;
import com.eternalsrv.ui.swipe.adapters.ItemTouchHelperAdapter;
import com.eternalsrv.ui.widget.circularprogressindicator.CircularProgressIndicator;
import com.eternalsrv.utils.ImageLoader;
import com.eternalsrv.utils.SharedPrefsHelper;
import com.eternalsrv.utils.qb.QbDialogUtils;
import com.quickblox.chat.model.QBChatDialog;

import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DialogsAdapter extends RecyclerView.Adapter<DialogsAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private static final int REQUEST_DIALOG_ID_FOR_UPDATE = 165;

    private ImageLoader imageLoader;
    private List<QBChatDialog> dialogList;
    private LayoutInflater inflater;

    private FragmentActivity fragmentActivity;

    public DialogsAdapter(List<QBChatDialog> dialogs, FragmentActivity fragmentActivity) {
        imageLoader = App.getImageLoader();
        this.dialogList = dialogs;
        this.inflater = LayoutInflater.from(App.getAppContext());
        this.fragmentActivity = fragmentActivity;
    }


    private int getUnreadMsgCount(QBChatDialog chatDialog){
        Integer unreadMessageCount = chatDialog.getUnreadMessageCount();
        if (unreadMessageCount == null) {
            return 0;
        } else {
            return unreadMessageCount;
        }
    }

    private boolean isLastMessageAttachment(QBChatDialog dialog) {
        String lastMessage = dialog.getLastMessage();
        Integer lastMessageSenderId = dialog.getLastMessageUserId();
        return TextUtils.isEmpty(lastMessage) && lastMessageSenderId != null;
    }

    private String prepareTextLastMessage(QBChatDialog chatDialog){
        if (isLastMessageAttachment(chatDialog)){
            return App.getAppContext().getString(R.string.chat_attachment);
        } else {
            if (SharedPrefsHelper.getInstance().getQbUser().getId().equals(chatDialog.getLastMessageUserId()))
                return "↱ " + chatDialog.getLastMessage();
            else
                return chatDialog.getLastMessage();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_dialog, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final int pos = holder.getAdapterPosition();
        final QBChatDialog dialog = dialogList.get(pos);
        holder.nameTextView.setText(QbDialogUtils.getDialogName(dialog));
        holder.lastMessageTextView.setText(prepareTextLastMessage(dialog));

        int unreadMessagesCount = getUnreadMsgCount(dialog);
        if (unreadMessagesCount == 0) {
            holder.unreadCounterTextView.setVisibility(View.GONE);
        } else {
            holder.unreadCounterTextView.setVisibility(View.VISIBLE);
            holder.unreadCounterTextView.setText(String.valueOf(unreadMessagesCount > 99 ? 99 : unreadMessagesCount));
        }

        List<String> userPhotos = QbDialogUtils.getQBUserPhotos(dialog);
        imageLoader.downloadImage(userPhotos.get(0), holder.dialogImageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QBChatDialog selectedDialog = dialogList.get(pos);
                ChatActivity.startForResult(fragmentActivity, REQUEST_DIALOG_ID_FOR_UPDATE, selectedDialog);

            }
        });

        holder.progressIndicator.setProgress(dialog.getCustomData().getInteger("matchValue"), 100);
        holder.progressIndicator.setAnimationEnabled(false);

    }

    @Override
    public int getItemCount() {
        return dialogList.size();
    }

    @Override
    public long getItemId(int position) {
        return dialogList.get(position).getDialogId().hashCode();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(dialogList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(dialogList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {

    }

    public void updateList(List<QBChatDialog> newData) {
        dialogList = newData;
        notifyDataSetChanged();
    }

    public void addToList(QBChatDialog dialog) {
        dialogList.add(dialog);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewGroup rootLayout;
        CircleImageView dialogImageView;
        TextView nameTextView;
        TextView lastMessageTextView;
        TextView unreadCounterTextView;
        CircularProgressIndicator progressIndicator;

        ViewHolder(View view) {
            super(view);
            this.rootLayout =  view.findViewById(R.id.root);
            this.nameTextView =  view.findViewById(R.id.text_dialog_name);
            this.lastMessageTextView =  view.findViewById(R.id.text_dialog_last_message);
            this.dialogImageView =  view.findViewById(R.id.image_dialog_icon);
            this.unreadCounterTextView =  view.findViewById(R.id.text_dialog_unread_count);
            this.progressIndicator = view.findViewById(R.id.dialog_match_progress_indicator);
        }

    }
}
