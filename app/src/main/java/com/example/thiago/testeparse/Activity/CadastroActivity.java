package com.example.thiago.testeparse.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thiago.testeparse.R;
import com.example.thiago.testeparse.util.ParseErros;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class CadastroActivity extends AppCompatActivity {

    private EditText textoUsuario;
    private EditText textoSenha;
    private EditText textoEmail;
    private Button botaoCadastrar;
    private TextView facaLogin;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        textoUsuario = findViewById(R.id.edit_login_usuario);
        textoEmail = findViewById(R.id.text_email);
        textoSenha = findViewById(R.id.text_senha);
        botaoCadastrar = findViewById(R.id.button_logar);
        facaLogin = findViewById(R.id.text_faca_login);


        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadastrarUsuario();
            }
        });
        facaLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirLoginUsuario();
            }
        });
    }

    private void cadastrarUsuario() {
        ParseUser usuario = new ParseUser();
        usuario.setUsername(textoUsuario.getText().toString());
        usuario.setEmail(textoEmail.getText().toString());
        usuario.setPassword(textoSenha.getText().toString());

        usuario.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(CadastroActivity.this, "Sucesso ao cadastrar usuario", Toast.LENGTH_SHORT).show();
                    abrirLoginUsuario();
                } else {
                    ParseErros parseErros = new ParseErros();
                    String erro = parseErros.getErro(e.getCode());
                    Toast.makeText(CadastroActivity.this, erro, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void abrirLoginUsuario() {
        Intent intent = new Intent(CadastroActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
