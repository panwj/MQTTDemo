package com.mavl.im.main;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mavl.im.BaseFragment;
import com.mavl.im.IMManager;
import com.mavl.im.R;
import com.mavl.im.db.DaoUtil2;
import com.mavl.im.entity.Message;
import com.mavl.im.event.ConnectEvent;
import com.mavl.im.sdk.IMMessageBroker;
import com.mavl.im.sdk.listener.IMMessageStatusDelegate;
import com.mavl.im.sdk.util.Logger;
import com.mavl.im.sdk.entity.IMMessage;
import com.mavl.im.util.TimeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class SenderFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private EditText editText;
    private Button button;
    private TextView clientStatusTv;
    private IMMessageBroker client;
    private List<Message> mList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerEventBus();
        client = IMManager.getInstance(getActivity()).getClient("client2");
        if (client == null) client = IMManager.getInstance(getActivity()).createDefaultAccount2();
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        initView(view);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_client;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterEventBus();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ConnectEvent(ConnectEvent connectEvent) {
        updateClientStatus();
    }

    private void registerEventBus() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    private void unregisterEventBus() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.rv);
        //添加Divider
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        Drawable dividerDrawable = getActivity().getDrawable(R.drawable.divider_horizontal_1dp);
        if (dividerDrawable != null)
            dividerItemDecoration.setDrawable(dividerDrawable);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        adapter = new MessageAdapter();
        recyclerView.setAdapter(adapter);
        updateData();

        editText = view.findViewById(R.id.edit);
        clientStatusTv = view.findViewById(R.id.client_status_tv);
        clientStatusTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.d("onClick client status : " + (client != null ? client.isConnect() : null));
                if (client == null) {
                    client = IMManager.getInstance(getActivity()).getClient("client1");
                    updateClientStatus();
                    return;
                }
                if (client.isConnect()) {
                    IMManager.getInstance(getActivity()).client2logout();
                } else {
                    try {
                        client.login();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Logger.e("client1 do connect exception : " + e.toString());

                    }
                }
            }
        });
        updateClientStatus();

        button = view.findViewById(R.id.send);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable editable = editText.getText();
                String text = editable.toString();

                if (TextUtils.isEmpty(text)) {
                    Toast.makeText(getActivity(), "message not empty...", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    IMMessageBroker client1 = IMManager.getInstance(getActivity()).getClient("client1");
                    if (client1 == null) {
                        Toast.makeText(getActivity(), "对方用户不存在", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    IMMessage message = new IMMessage();
                    message.payload = text;
                    message.timeStamp = System.currentTimeMillis();

                    client.sendToMessage(text, false, client1.getClientId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        client.setIMMessageStatusDelegate(new IMMessageStatusDelegate() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                updateClientStatus();
            }

            @Override
            public void connectionLost(Throwable cause) {
                updateClientStatus();
            }

            @Override
            public void onSendingMessage(IMMessage imMessage) {
                Message messageOld = DaoUtil2.getIMMessageByClientId(getActivity(), imMessage.messageLocalId);
                Logger.d("客户端2 onSendingMessage() message ---> " + messageOld);

                Message newMsg = new Message();
                newMsg.messageId = imMessage.messageId;
                newMsg.messageLocalId = imMessage.messageLocalId;
                newMsg.fromUid = imMessage.fromUid;
                newMsg.toUid = imMessage.toUid;
                newMsg.isReceived = false;
                newMsg.payload = imMessage.payload;
                newMsg.status = 0;

                if (messageOld == null) {
                    DaoUtil2.saveMessage(getActivity(), newMsg);
                } else {
                    DaoUtil2.updateMessageStatus(getActivity(), newMsg);
                }
                updateData();
            }

            @Override
            public void onSendDoneMessage(IMMessage imMessage) {
                Message messageOld = DaoUtil2.getIMMessageByClientId(getActivity(), imMessage.messageLocalId);
                Logger.d("客户端2 onSendDoneMessage() message ---> " + messageOld);
                if (messageOld != null) {
                    messageOld.status = 1;
                    DaoUtil2.updateMessageStatus(getActivity(), messageOld);
                }
            }

            @Override
            public void onReceivedMessage(IMMessage imMessage) {
                Message messageOld = DaoUtil2.getIMMessageByClientId(getActivity(), imMessage.messageLocalId);
                Logger.d("客户端2 onReceivedMessage() message ---> " + messageOld);

                Message newMsg = new Message();
                newMsg.messageId = imMessage.messageId;
                newMsg.messageLocalId = imMessage.messageLocalId;
                newMsg.fromUid = imMessage.fromUid;
                newMsg.toUid = imMessage.toUid;
                newMsg.isReceived = false;
                newMsg.payload = imMessage.payload;
                newMsg.status = 1;

                if (messageOld == null) {
                    DaoUtil2.saveMessage(getActivity(), newMsg);
                } else {
                    DaoUtil2.updateMessage(getActivity(), newMsg);
                }
                updateData();
            }

            @Override
            public void onSendFailedMessage(IMMessage imMessage, Throwable exception) {
                Message messageOld = DaoUtil2.getIMMessageByClientId(getActivity(), imMessage.messageLocalId);

                Message newMsg = new Message();
                newMsg.messageId = imMessage.messageId;
                newMsg.messageLocalId = imMessage.messageLocalId;
                newMsg.fromUid = imMessage.fromUid;
                newMsg.toUid = imMessage.toUid;
                newMsg.isReceived = false;
                newMsg.payload = imMessage.payload;
                newMsg.status = 2;

                if (messageOld == null) {
                    DaoUtil2.saveMessage(getActivity(), newMsg);
                } else {
                    DaoUtil2.updateMessageStatus(getActivity(), newMsg);
                }
                updateData();
            }
        });
    }

    public String getClientName() {
        return client != null ? client.getClientId() : "Sender";
    }

    public void updateClientStatus() {
        if (clientStatusTv == null) return;
        if (client == null) {
            clientStatusTv.setText("client2用户还未创建");
        } else if (client.isConnect()) {
            clientStatusTv.setText("client2用户已登陆");
        } else {
            clientStatusTv.setText("client2用户未登陆");
        }
    }

    private void updateData() {
        ArrayList messages = DaoUtil2.getMessageList(getActivity());
        if (mList == null) mList = new ArrayList<>();
        mList.clear();
        if (messages != null && messages.size() > 0) mList.addAll(messages);
        if (adapter != null) adapter.updateList(mList);
    }

    private class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

        private List<Message> mList;

        /**
         * 刷新列表数据。
         */
        public void updateList(List<Message> list) {
            if (mList == null) mList = new ArrayList<>();
            mList.clear();
            if (list != null) mList.addAll(list);

            notifyDataSetChanged();
        }


        @NonNull
        @Override
        public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MessageAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
            holder.bindView(mList.get(position));
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            private TextView tvReceivedName, tvReceivedMsg;
            private TextView tvSendName, tvSendMsg;
            private ImageView ivReceivedStatus, ivSendStatus;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvReceivedName = itemView.findViewById(R.id.tv_receiver_name);
                tvReceivedMsg = itemView.findViewById(R.id.tv_receiver_msg);
                ivReceivedStatus = itemView.findViewById(R.id.iv_receiver_status);

                tvSendName = itemView.findViewById(R.id.tv_send);
                tvSendMsg = itemView.findViewById(R.id.tv_send_msg);
                ivSendStatus = itemView.findViewById(R.id.iv_send_status);
            }

            public void bindView(Message message) {
                String fromUid = message.fromUid;
                if (fromUid.contains(client.getClientId())) {

                    tvSendName.setText(message.fromUid);
                    tvSendMsg.setText(message.payload + "\n\n" + TimeUtil.getDataTimeFormat(message.timeStamp, "yyyy-MM-dd HH:mm:ss"));
                    if (message.status == 0) {
                        ivSendStatus.setImageResource(R.drawable.ic_loading_blue);
                    } else if (message.status == 1) {
                        ivSendStatus.setImageResource(R.drawable.ic_send_success);
                    } else {
                        ivSendStatus.setImageResource(R.drawable.ic_send_failed);
                    }

                    tvSendName.setVisibility(View.VISIBLE);
                    tvSendMsg.setVisibility(View.VISIBLE);
                    ivSendStatus.setVisibility(View.VISIBLE);
                    tvReceivedName.setVisibility(View.GONE);
                    tvReceivedMsg.setVisibility(View.GONE);
                    ivReceivedStatus.setVisibility(View.GONE);

                } else {
                    tvReceivedName.setText(message.fromUid);
                    tvReceivedMsg.setText(message.payload + "\n  " + TimeUtil.getDataTimeFormat(message.timeStamp, "yyyy-MM-dd HH:mm:ss"));
                    if (message.status == 0) {
                        ivReceivedStatus.setImageResource(R.drawable.ic_loading_blue);
                    } else if (message.status == 1) {
                        ivReceivedStatus.setImageResource(R.drawable.ic_send_success);
                    } else {
                        ivReceivedStatus.setImageResource(R.drawable.ic_send_failed);
                    }

                    tvReceivedName.setVisibility(View.VISIBLE);
                    tvReceivedMsg.setVisibility(View.VISIBLE);
                    ivReceivedStatus.setVisibility(View.VISIBLE);
                    tvSendName.setVisibility(View.GONE);
                    tvSendMsg.setVisibility(View.GONE);
                    ivSendStatus.setVisibility(View.GONE);
                }
            }
        }
    }
}
