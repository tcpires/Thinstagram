package com.example.thiago.testeparse.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thiago.testeparse.R;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends ArrayAdapter<ParseObject> {

    private Context context;
    private ArrayList<ParseObject> postagens;
    private TextView nomeAutor;

    public HomeAdapter(@NonNull Context c, @NonNull ArrayList<ParseObject> objects) {
        super(c, 0, objects);
        this.context = c;
        this.postagens = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        if (view==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.lista_postagem, parent, false);
        }

        if (postagens.size() >0){
            ImageView imagemPostagem = (ImageView) view.findViewById(R.id.image_lista_postagem);
            nomeAutor = view.findViewById(R.id.nome_autor);
            ParseObject parseObject = postagens.get(position);
            String username = parseObject.getString("username");
            nomeAutor.setText(username);

            Picasso.with(context)
                    .load(parseObject.getParseFile("imagem").getUrl())
                    .fit()
                    .into(imagemPostagem);
        }

        return view;
    }
}
