package com.mavl.im.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.mavl.im.sdk.IMConnectionClient;
import com.mavl.im.sdk.Logger;
import com.mavl.im.sdk.entity.IMMessage;
import com.mavl.im.sdk.listener.IMCallback;
import com.mavl.im.sdk.listener.IMReceivedMessageListener;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;

public class ReceiverFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private EditText editText, publicText;
    private Button button;
    private IMConnectionClient client;
    private MessageAdapter adapter;
    private List<IMMessage> mList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = IMManager.getInstance(getActivity()).createDefaultAccount1();
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

        publicText = view.findViewById(R.id.edit_public);
        editText = view.findViewById(R.id.edit);
        button = view.findViewById(R.id.send);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable editable = editText.getText();
                String text = editable.toString();
                String topic = publicText.getText().toString();

                if (TextUtils.isEmpty(text) || TextUtils.isEmpty(topic)) {
                    Toast.makeText(getActivity(), "message not empty...", Toast.LENGTH_SHORT).show();
                    return;
                }

                IMMessage message = new IMMessage();
                message.messageClientId = 122;
                message.qos = 0;
                message.retained = true;
                message.payload = text;
                try {
                    client.publish(topic, message, null);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    updateData(message);
                }
            }
        });

        client.setIMCallback(new IMCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Logger.e("client1 ---->  connectionLost() : " + (cause != null ? cause.toString() : null));
            }

            @Override
            public void messageArrived(String topic, IMMessage message) throws Exception {
                Logger.d("client1 ---->  topic : " + topic + "    message : " + (message != null ? message.toString() : null));

                mList.add(message);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Logger.d("client1 ----> deliveryComplete() " + (token != null ? token.toString() : null));
            }
        });
        client.setIMReceivedMessageListener(new IMReceivedMessageListener() {
            @Override
            public void onMessageReceived(IMMessage message) {
                Logger.e("client1 ----> onMessageReceived() : " + message);
            }
        });
    }

    public String getClientName() {
        return client != null ? client.getClientId() : "Receiver";
    }

    private void updateData(IMMessage message) {
        if (mList == null) mList = new ArrayList<>();
        mList.add(message);
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

                tvReceived.setText(message.toString());
            }
        }
    }
}
