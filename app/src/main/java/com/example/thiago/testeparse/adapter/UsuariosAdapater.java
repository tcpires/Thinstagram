package com.example.thiago.testeparse.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.thiago.testeparse.R;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thiago on 20/06/17.
 */

public class UsuariosAdapater extends ArrayAdapter<ParseUser>{

    private Context context;
    private ArrayList<ParseUser> ususarios;

    public UsuariosAdapater(@NonNull Context c, @NonNull ArrayList<ParseUser> objects) {
        super(c, 0, objects);
        this.context = c;
        this.ususarios = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.lista_usuarios, parent, false);
        }

        TextView username = (TextView) view.findViewById(R.id.text_username);

        ParseUser parseUser = ususarios.get(position);
        username.setText(parseUser.getUsername());

        return view;
    }


}
