package com.eternalsrv.ui.swipe.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eternalsrv.App;
import com.eternalsrv.R;
import com.eternalsrv.ui.chat.ChatActivity;
import com.eternalsrv.ui.widget.circularprogressindicator.CircularProgressIndicator;
import com.eternalsrv.utils.ImageLoader;
import com.eternalsrv.utils.qb.QbDialogUtils;
import com.quickblox.chat.model.QBChatDialog;

import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.eternalsrv.ui.chat.DialogsFragment.REQUEST_DIALOG_ID_FOR_UPDATE;

public class HorizontalListDialogsRecyclerViewAdapter
        extends RecyclerView.Adapter<HorizontalListDialogsRecyclerViewAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private List<QBChatDialog> dialogList;
    private final ImageLoader imageLoader;
    private final FragmentActivity fragmentActivity;
    private final String PROPERTY_MATCH_VALUE = "matchValue";

    public HorizontalListDialogsRecyclerViewAdapter(FragmentActivity fragmentActivity, List<QBChatDialog> dialogs) {
        this.dialogList = dialogs;
        this.fragmentActivity = fragmentActivity;
        imageLoader = App.getImageLoader();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dialogs_horizontal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final int pos = holder.getAdapterPosition();
        final QBChatDialog dialog = dialogList.get(position);

        List<String> userPhotos = QbDialogUtils.getQBUserPhotos(dialog);
        boolean empty = userPhotos.isEmpty();
        imageLoader.downloadImage(!empty ? userPhotos.get(0) : "sd", holder.dialogImage);

        holder.dialogName.setText(QbDialogUtils.getDialogName(dialog));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QBChatDialog selectedDialog = dialogList.get(pos);
                ChatActivity.startForResult(fragmentActivity, REQUEST_DIALOG_ID_FOR_UPDATE, selectedDialog);
            }
        });

        holder.progressIndicator.setProgress(dialog.getCustomData().getInteger(PROPERTY_MATCH_VALUE), 100);
    }

    @Override
    public long getItemId(int position) {
        return dialogList.get(position).getDialogId().hashCode();
    }

    @Override
    public int getItemCount() {
        return dialogList.size();
    }

    public void updateList(List<QBChatDialog> newData) {
        dialogList = newData;
        notifyDataSetChanged();
    }

    public void addToList(QBChatDialog dialog) {
        dialogList.add( dialog);
        notifyDataSetChanged();
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


    class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView dialogImage;
        TextView dialogName;
        CircularProgressIndicator progressIndicator;

        ViewHolder(View view) {
            super(view);
            progressIndicator = view.findViewById(R.id.horizontal_dialog_match_progress_indicator);
            dialogImage = view.findViewById(R.id.list_dialogs_slider_image);
            dialogName = view.findViewById(R.id.list_dialogs_slider_name);
        }
    }
}
