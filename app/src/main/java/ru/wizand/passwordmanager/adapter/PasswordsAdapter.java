package ru.wizand.passwordmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.wizand.passwordmanager.MainActivity;
import ru.wizand.passwordmanager.MainScreenActivity;
import ru.wizand.passwordmanager.R;
import ru.wizand.passwordmanager.db.entity.Password;

public class PasswordsAdapter extends RecyclerView.Adapter<PasswordsAdapter.MyViewHolder> {

    // 1 - Variables
    private Context context;
    private ArrayList<Password> passwordArrayList;
    private MainScreenActivity mainActivity;

    // 2 - ViewHolder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public  TextView url;
        public  TextView login;
        public TextView password;
        public TextView additional;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.name);
            this.url = itemView.findViewById(R.id.url);
            this.login = itemView.findViewById(R.id.login);
            this.password = itemView.findViewById(R.id.password);
            this.additional = itemView.findViewById(R.id.additional);
        }
    }

    public PasswordsAdapter(Context context, ArrayList<Password> passwords, MainScreenActivity mainActivity) {
        this.context = context;
        this.passwordArrayList = passwords;
        this.mainActivity = mainActivity;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.password_list_item,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Password password = passwordArrayList.get(position);

        holder.name.setText(password.getName());
        holder.url.setText(password.getUrl());
        holder.login.setText(password.getLogin());
        holder.password.setText(password.getPassword());
        holder.additional.setText(password.getAdditional());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.addAndEditPasswords(true, password, position);
            }
        });



    }

    @Override
    public int getItemCount() {
        return  passwordArrayList.size();
    }
}
