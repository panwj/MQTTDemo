package com.mavl.im.main;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.mavl.im.sdk.config.IMClientConfig;
import com.mavl.im.sdk.entity.IMSubscribe;
import com.mavl.im.sdk.listener.ConnectionStatus;
import com.mavl.im.sdk.listener.IMClientListener;

import org.eclipse.paho.client.mqttv3.IMqttToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContactFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private ClientAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        initView(view);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_contacts;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        adapter = new ClientAdapter();
        recyclerView.setAdapter(adapter);
        updateData();
    }

    public void updateData() {
        Map<String, IMConnectionClient> maps = IMManager.getInstance(getActivity()).getClients();
        List<IMConnectionClient> list = new ArrayList<>();
        for (IMConnectionClient client : maps.values()) {
            list.add(client);
        }
        if (adapter != null) adapter.updateList(list);
    }

    private class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ViewHolder> {

        private List<IMConnectionClient> mList;

        /**
         * 刷新列表数据。
         */
        public void updateList(List<IMConnectionClient> list) {
            if (mList == null) mList = new ArrayList<>();
            mList.clear();
            if (list != null) mList.addAll(list);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_client, parent, false));
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

            private TextView tvName, tvStatus, tvConnect, tvSub, tvConfig;
            private EditText editText;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_name);
                tvStatus = itemView.findViewById(R.id.tv_status);
                tvConnect = itemView.findViewById(R.id.tv_connect);
                tvSub = itemView.findViewById(R.id.tv_sub);
                tvConfig = itemView.findViewById(R.id.tv_config);
                editText = itemView.findViewById(R.id.edit);
            }

            public void bindView(final IMConnectionClient client) {
                IMClientConfig config = client.getIMClientConfig();
                tvName.setText(config != null ? config.userName : "null");
                tvStatus.setText("" + client.getConnectionStatus());
                if (client.isConnect()) {
                    tvConnect.setText("disConnect");
                } else {
                    tvConnect.setText("Connect");
                }
                tvConnect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (client.getConnectionStatus() == ConnectionStatus.CONNECTING
                                || client.getConnectionStatus() == ConnectionStatus.DISCONNECTING) return;

                        if (client.isConnect()) {
                            try {
                                tvConnect.setText("disConnecting");
                                client.disConnect(-1, null);
                            } catch (Exception e) {
                                e.toString();
                                tvConnect.setText("disConnecting Error");
                            }
                        } else {
                            try {
                                tvConnect.setText("Connecting");
                                client.doConnect(null);
                            } catch (Exception e) {
                                e.toString();
                                tvConnect.setText("Connecting Error");
                            }
                        }
                    }
                });

                tvConfig.setText(config != null ? config.toString() : null);
                tvSub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IMSubscribe subscribe = new IMSubscribe();
                        subscribe.qos = 0;
                        subscribe.topic = editText.getText().toString();

                        Logger.d("click sub subscribe : " + subscribe.toString());

                        try {
                            client.subscribe(subscribe, null);
                        } catch (Exception e) {
                            e.toString();
                            Logger.d("click sub exception : " + e.toString());
                        }
                    }
                });

                client.setIMClientListener(new IMClientListener() {
                    @Override
                    public void onConnectSuccess(IMqttToken asyncActionToken) {
                        tvConnect.setText("Connect Success");
                        tvStatus.setText("" + client.getConnectionStatus());
                    }

                    @Override
                    public void onConnectFailure(IMqttToken asyncActionToken, Throwable exception) {
                        tvConnect.setText("Connect Failure");
                        tvStatus.setText("" + client.getConnectionStatus());
                    }

                    @Override
                    public void onDisConnectSuccess(IMqttToken asyncActionToken) {
                        tvConnect.setText("disConnect Success");
                        tvStatus.setText("" + client.getConnectionStatus());
                    }

                    @Override
                    public void onDisConnectFailure(IMqttToken asyncActionToken, Throwable exception) {
                        tvConnect.setText("disConnect Failure");
                        tvStatus.setText("" + client.getConnectionStatus());
                    }

                    @Override
                    public void onPublishSuccess(IMqttToken asyncActionToken) {

                    }

                    @Override
                    public void onPublishFailure(IMqttToken asyncActionToken, Throwable exception) {

                    }

                    @Override
                    public void onSubscribeSuccess(IMqttToken asyncActionToken) {
                        Toast.makeText(getActivity(), "sub success!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSubscribeFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Toast.makeText(getActivity(), "sub failure!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onUnSubscribeSuccess(IMqttToken asyncActionToken) {
                        Toast.makeText(getActivity(), "un sub success!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onUnSubscribeFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Toast.makeText(getActivity(), "un sub failure!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
