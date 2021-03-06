package com.example.oseias.sverse.OthersClass;

import java.util.ArrayList;
import java.util.Date;

public class ItemArea {
    private String titulo;
    private String descricao;
    private Date dataDeCriacao;
    private ArrayList<SubItemArea> subItens;

    public ItemArea(String titulo, String descricao) {
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public ArrayList<SubItemArea> getSubItens() {
        return subItens;
    }

    public void setSubItens(ArrayList<SubItemArea> subItens) {
        this.subItens = subItens;
    }

    public void addSubItem(SubItemArea subItem) {
        subItens.add(subItem);
    }

    public void removeSubItem(int idSubItem) {
        subItens.remove(idSubItem);
    }
}
