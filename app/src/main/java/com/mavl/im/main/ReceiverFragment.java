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
import com.mavl.im.db.DaoUtil;
import com.mavl.im.event.ConnectEvent;
import com.mavl.im.sdk.IMConnectionClient;
import com.mavl.im.sdk.IMConstants;
import com.mavl.im.sdk.Logger;
import com.mavl.im.sdk.entity.IMMessage;
import com.mavl.im.sdk.listener.IMMessageStatusListener;
import com.mavl.im.util.TimeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class ReceiverFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private EditText editText;
    private Button button;
    private TextView clientStatusTv;
    private IMConnectionClient client;
    private MessageAdapter adapter;
    private List<IMMessage> mList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerEventBus();
        client = IMManager.getInstance(getActivity()).getClient("client1");
        if (client == null) client = IMManager.getInstance(getActivity()).createDefaultAccount1();
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
        mList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.rv);
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
                    IMManager.getInstance(getActivity()).client1logout();
                } else {
                    try {
                        client.doConnect(null);
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

                IMMessage message = new IMMessage();
                message.retained = true;
                message.payload = text;
                message.timeStamp = System.currentTimeMillis();
                try {
                    IMConnectionClient client2 = IMManager.getInstance(getActivity()).getClient("client2");
                    if (client2 == null) {
                        Toast.makeText(getActivity(), "对方用户不存在", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    client.publishOneToOne(client2.getClientId(), message, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        client.setIMCallback(new IMMessageStatusListener() {
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
                IMMessage message = DaoUtil.getIMMessageByClientId(getActivity(), imMessage.messageClientId);
//                Logger.d("onSendingMessage() message ---> " + message);
                if (message == null) {
                    DaoUtil.saveMessage(getActivity(), imMessage);
                } else {
                    DaoUtil.updateMessageStatus(getActivity(), imMessage);
                }
                updateData();
            }

            @Override
            public void onSendCompletedMessage(int msgClientId, String payload) {
                IMMessage message = DaoUtil.getIMMessageByClientId(getActivity(), msgClientId);
//                Logger.d("onSendCompletedMessage() message ---> " + message);
                if (message != null) {
                    message.status = IMConstants.MSG_SEND_STATUS_COMPLETED;
                    DaoUtil.updateMessageStatus(getActivity(), message);
                }
            }

            @Override
            public void onReceivedMessage(IMMessage imMessage) {
//                Logger.d("received last msg ---> " + imMessage);

                IMMessage message = DaoUtil.getIMMessageByClientId(getActivity(), imMessage.messageClientId);
//                Logger.d("onReceivedMessage() message ---> " + message);
                if (message == null) {
                    DaoUtil.saveMessage(getActivity(), imMessage);
                } else {
                    DaoUtil.updateMessage(getActivity(), imMessage);
                }
                updateData();
            }

            @Override
            public void onSendFailedMessage(IMMessage imMessage, Throwable exception) {
                IMMessage message = DaoUtil.getIMMessageByClientId(getActivity(), imMessage.messageClientId);
                if (message == null) {
                    DaoUtil.saveMessage(getActivity(), imMessage);
                } else {
                    DaoUtil.updateMessageStatus(getActivity(), imMessage);
                }
                updateData();
            }
        });
    }

    public String getClientName() {
        return client != null ? client.getClientId() : "Receiver";
    }

    public void updateClientStatus() {
        if (clientStatusTv == null) return;
        if (client == null) {
            clientStatusTv.setText("client1用户还未创建");
        } else if (client.isConnect()) {
            clientStatusTv.setText("client1用户已登陆");
        } else {
            clientStatusTv.setText("client1用户未登陆");
        }
    }

    private void updateData() {
        ArrayList messages = DaoUtil.getMessageList(getActivity());
        if (mList == null) mList = new ArrayList<>();
        mList.clear();
        if (messages != null && messages.size() > 0) mList.addAll(messages);
        if (adapter != null) adapter.updateList(mList);
    }

    private class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

        private List<IMMessage> mList;

        /**
         * 刷新列表数据。
         */
        public void updateList(List<IMMessage> list) {
            if (mList == null) mList = new ArrayList<>();
            mList.clear();
            if (list != null) mList.addAll(list);
            notifyDataSetChanged();
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bindView(mList.get(position));
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            private TextView tvReceived, tvSend;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvReceived = itemView.findViewById(R.id.tv_receiver);
                tvSend = itemView.findViewById(R.id.tv_send);
            }

            public void bindView(IMMessage message) {
                String fromUid = message.fromUid;
                if (fromUid.contains(client.getClientId())) {
                    tvSend.setText(fromUid + " : <status :  "
                            + message.status
                            + "  "
                            + TimeUtil.getDataTimeFormat(message.timeStamp, "yyyy-MM-dd HH:mm:ss")
                            + " >" + message.payload);
                    tvSend.setVisibility(View.VISIBLE);
                    tvReceived.setVisibility(View.GONE);
                } else {
                    tvReceived.setText(message.toUid + " : <status :  "
                            + message.status
                            + "  "
                            + TimeUtil.getDataTimeFormat(message.timeStamp, "yyyy-MM-dd HH:mm:ss")
                            + " >" + message.payload);
                    tvReceived.setVisibility(View.VISIBLE);
                    tvSend.setVisibility(View.GONE);
                }
            }
        }
    }
}
