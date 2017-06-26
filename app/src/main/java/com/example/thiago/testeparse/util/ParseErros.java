package com.example.thiago.testeparse.util;

import java.util.HashMap;

/**
 * Created by thiago on 08/06/17.
 */

public class ParseErros {
    private HashMap<Integer, String> erros;

    public ParseErros() {
        this.erros = new HashMap<>();
        this.erros.put(202, "Usuário já existente, por favor escolha outro usuário!!!");
        this.erros.put(201, "Senha não preenchida, por favor escolha uma senha!!!");

    }

    public String getErro (int codErro){
        return this.erros.get(codErro);
    }
}
