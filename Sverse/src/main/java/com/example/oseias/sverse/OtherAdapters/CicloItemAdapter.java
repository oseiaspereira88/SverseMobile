package com.example.oseias.sverse.OtherAdapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.oseias.sverse.MainActivity;
import com.example.oseias.sverse.OthersActivitys.CicloItemCreator;
import com.example.oseias.sverse.SQLite.dao.ContainerDAO;
import com.example.oseias.sverse.SQLite.model.CicloItem;
import com.example.oseias.sverse.SQLite.model.Container;
import com.exemple.oseias.sverse.R;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by oseias on 03/04/2018.
 */

public class CicloItemAdapter extends BaseAdapter {
    ImageView imgSegmento1, imgSegmento2, imgYes, imgBGItem, imgItem;
    TextView tvDia, tvHora, tvNome;
    CardView cardItem;
    ArrayList<CicloItem> itens;
    ContainerDAO containerDAO;
    ArrayList<Container> containers;
    boolean isLongeClick;
    Context ctx;

    public CicloItemAdapter(Context ctx, ArrayList<CicloItem> itens) {
        this.ctx = ctx;
        this.itens = itens;
        isLongeClick = false;
    }

    @Override
    public int getCount() {
        return itens.size();
    }

    @Override
    public Object getItem(int position) {
        return itens.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String weekDay(Calendar cal) {
        return new DateFormatSymbols().getWeekdays()[cal.get(Calendar.DAY_OF_WEEK)];
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        view = LayoutInflater.from(ctx).inflate(R.layout.ciclo_item_model, null);
        findAllViews(view);
        initAllViews(view, i);
        return view;
    }

    private void findAllViews(View view) {
        imgSegmento1 = view.findViewById(R.id.imgSegmento1);
        imgSegmento2 = view.findViewById(R.id.imgSegmento2);
        imgYes = view.findViewById(R.id.imgYes);
        imgBGItem = view.findViewById(R.id.imgBGItem);
        imgItem = view.findViewById(R.id.imgItem);
        tvDia = (TextView) view.findViewById(R.id.tvDia);
        tvHora = (TextView) view.findViewById(R.id.tvHora);
        tvNome = (TextView) view.findViewById(R.id.tvNome);
        cardItem = (CardView) view.findViewById(R.id.cardItem);
    }

    private void initAllViews(View view, int i) {
        containerDAO = new ContainerDAO(ctx);
        containers = containerDAO.listarContainers();
        CicloItem item = itens.get(i);
        Container container = new Container(
                "Indefinido",
                "Indefinido",
                "Indefinido",
                0,
                0,
                0,
                0,
                "Indefinido",
                "Indefinido",
                0,
                "Indefinido",
                "Indefinido");

        for(Container c : containers){
            if(c.get_id() == item.getIdContainer()){
                container = c;
            }
        }

        tvNome.setText(container.getName());
        //imgItem.setImageResource(container.getImageContainer());
        imgItem.setImageResource(R.mipmap.ic_play_pomodoro);

        ArrayList<String> dias = listarDias();
        tvDia.setText(dias.get(item.getDiaDaSemana()-1).toString());

        //Seta bgItem como Green e dá yes no estudo
        if(item.isConcluido()){
            cardItem.setCardBackgroundColor(Color.GREEN);
            imgBGItem.setImageResource(R.mipmap.ic_item_green);
            imgYes.setVisibility(View.VISIBLE);
        } else{
            imgYes.setVisibility(View.INVISIBLE);
        }

        //verifica o momento do item e seta a cor de acordo com o dia atual
        //(se já passou e não foi estudado, se já foi estudado, ou se ainda não foi estudado)
        verificarEstadoDoItem(item, dias);

        //seta as horas e minutos no formato 00:00
        tvHora.setText(item.getHora()<10 ? "0" + item.getHora().toString() : item.getHora().toString());
        tvHora.setText(item.getMinuto()<10 ? tvHora.getText() + ":0" + item.getMinuto().toString() :tvHora.getText() + ":" + item.getMinuto().toString());


        //estiliza a img de segmento do primeiro e ultimo item listados.
        if(i == 0){
            imgSegmento1.setVisibility(View.INVISIBLE);
        } else if (i == itens.size()-1){
            imgSegmento2.setVisibility(View.INVISIBLE);
        }

        //setando OnLongClick
        final int finalId = item.get_id();
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isLongeClick = true;
                YoYo.with(Techniques.Wave)
                        .duration(700)
                        .repeat(0)
                        .playOn(v);

                //Abrindo activity CicloItemCreator passando o id do item em questão para edição.
                Intent it = new Intent(ctx, CicloItemCreator.class);
                Bundle b = new Bundle();
                b.putInt("cicloItemId", finalId +1);
                it.putExtra("cicloItemId", b);
                ctx.startActivity(it);
                return false;
            }
        });

        //setando OnClick
        //Que da reopen na MainActivity predefinindo as configs da ferramenta de Pomodoro no MuralFragment
        imgItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLongeClick) {
                    isLongeClick = false;
                } else {
                    YoYo.with(Techniques.Landing)
                            .duration(700)
                            .repeat(0)
                            .playOn(v);

                    //Abrir a ferramenta de Pomodoro (na MainActivity) com as info do item selected.
                    Intent it = new Intent(ctx, MainActivity.class);
                    Bundle b = new Bundle();
                    b.putInt("cicloItemId", finalId +1);
                    it.putExtra("cicloItemId", b);
                    ctx.startActivity(it);
                }
            }
        });
    }

    private void verificarEstadoDoItem(CicloItem item, ArrayList<String> dias) {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        String weekDay = weekDay(calendar);
        int diaAtual = dias.indexOf(weekDay)+1;

        if(item.getDiaDaSemana() < diaAtual && !item.isConcluido()){
            cardItem.setCardBackgroundColor(Color.RED);
            imgBGItem.setImageResource(R.mipmap.ic_item_red);
        } else if (item.getDiaDaSemana() > diaAtual && !item.isConcluido()){
            cardItem.setCardBackgroundColor(Color.BLUE);
            imgBGItem.setImageResource(R.mipmap.ic_item_blue2);
        } else if (item.getDiaDaSemana() == diaAtual && !item.isConcluido()){
            if(item.getHora() < date.getHours() && !item.isConcluido()){
                cardItem.setCardBackgroundColor(Color.RED);
                imgBGItem.setImageResource(R.mipmap.ic_item_red);
            } else if (item.getHora() > date.getHours() && !item.isConcluido()){
                cardItem.setCardBackgroundColor(Color.BLUE);
                imgBGItem.setImageResource(R.mipmap.ic_item_blue2);
            } else if (item.getHora() == date.getHours()){
                if(item.getMinuto() < date.getMinutes() && !item.isConcluido()){
                    cardItem.setCardBackgroundColor(Color.RED);
                    imgBGItem.setImageResource(R.mipmap.ic_item_red);
                } else if (item.getMinuto() > date.getMinutes() && !item.isConcluido()){
                    cardItem.setCardBackgroundColor(Color.BLUE);
                    imgBGItem.setImageResource(R.mipmap.ic_item_red);
                } else if(item.getMinuto() == date.getMinutes()){
                    cardItem.setCardBackgroundColor(Color.YELLOW);
                    imgBGItem.setImageResource(R.mipmap.ic_item_blue2);
                }
            }
        }
    }

    private ArrayList<String> listarDias() {
        ArrayList<String> dias = new ArrayList<>();
        dias.add("domingo");
        dias.add("segunda");
        dias.add("terça");
        dias.add("quarta");
        dias.add("quinta");
        dias.add("sexta");
        dias.add("sábado");
        return dias;
    }

    public void actionButton(View view){
        YoYo.with(Techniques.Pulse)
                .duration(700)
                .repeat(0)
                .playOn(view);
    }
}
