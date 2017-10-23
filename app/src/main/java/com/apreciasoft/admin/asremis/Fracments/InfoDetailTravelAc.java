package com.apreciasoft.admin.asremis.Fracments;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.apreciasoft.admin.asremis.Activity.HomeActivity;
import com.apreciasoft.admin.asremis.Entity.InfoTravelEntity;
import com.apreciasoft.admin.asremis.Http.HttpConexion;
import com.apreciasoft.admin.asremis.R;
import com.apreciasoft.admin.asremis.Services.ServicesTravel;
import com.apreciasoft.admin.asremis.Util.GlovalVar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by usario on 14/9/2017.
 */

public class InfoDetailTravelAc extends AppCompatActivity {



    ServicesTravel apiService = null;
    public Button button1 = null;
    public Button bt_confirmar_reserva = null;
    public Button btn_init_reserva = null;
    public ServicesTravel daoTravel = null;
    public GlovalVar gloval;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_info_detail_travel);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Detalles De Viaje!");
        this.apiService = HttpConexion.getUri().create(ServicesTravel.class);


        final InfoTravelEntity travel = (InfoTravelEntity) getIntent().getSerializableExtra("TRAVEL");
        final TextView txt_nr_travel = (TextView) findViewById(R.id.txt_nr_travel);
        final TextView txt_client_info = (TextView) findViewById(R.id.txt_client_info);
        final TextView txt_km_info = (TextView) findViewById(R.id.txt_km_info);
        final TextView txt_amount_info = (TextView) findViewById(R.id.txt_amount_info);
        final TextView txt_date_info = (TextView) findViewById(R.id.txt_date_info);
        final TextView txt_origin_info = (TextView) findViewById(R.id.txt_origin_info);
        final TextView txt_destination_info = (TextView) findViewById(R.id.txt_destination_info);
        final TextView txt_observationFromDriver = (TextView) findViewById(R.id.txt_observationFromDriver);
        final TextView txt_pasajeros_info = (TextView) findViewById(R.id.txt_pasajeros_info);



        bt_confirmar_reserva = (Button) findViewById(R.id.bt_confirmar_reserva);
        btn_init_reserva = (Button) findViewById(R.id.btn_init_reserva);


        txt_nr_travel.setText(travel.getCodTravel().toString());
        txt_client_info.setText(travel.getClient().toString());

        txt_km_info.setText(travel.getDistanceLabel().toString());
        button1 = (Button) findViewById(R.id.button1);

        if(HomeActivity.param25 == 1) {
            txt_amount_info.setText(travel.getAmountCalculate().toString());
        }else {
            txt_amount_info.setText("---");
        }


        if(travel.getIdSatatusTravel() != 5 && travel.getIdSatatusTravel() != 4) {
            if (travel.getIsAceptReservationByDriver() == 1) {
                if (HomeActivity.currentTravel == null) {
                    btn_init_reserva.setVisibility(View.VISIBLE);
                } else {

                    btn_init_reserva.setEnabled(false);
                }


                bt_confirmar_reserva.setVisibility(View.INVISIBLE);

            } else {
                btn_init_reserva.setVisibility(View.INVISIBLE);
                bt_confirmar_reserva.setVisibility(View.VISIBLE);
            }
        }else
        {
            btn_init_reserva.setEnabled(false);
            bt_confirmar_reserva.setEnabled(false);
            button1.setEnabled(false);

        }



        txt_date_info.setText(travel.getMdate().toString());
        txt_origin_info.setText(travel.getNameOrigin().toString());
        txt_destination_info.setText(travel.getNameDestination().toString());
        txt_observationFromDriver.setText(travel.getObservationFromDriver().toString());
        txt_pasajeros_info.setText(travel.getPasajero());

        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                event_cancel(travel.getIdTravel());
            }
        });




        bt_confirmar_reserva.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                event_confirm(travel.getIdTravel());
            }
        });

        btn_init_reserva.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                aceptTravel(travel.getIdTravel());
            }
        });

        // variable global //
        gloval = ((GlovalVar)getApplicationContext());


    }




    /* METODO PARA ACEPATAR EL VIAJE*/
    public  void  aceptTravel(int idTravel)
    {

        if (this.daoTravel == null) { this.daoTravel = HttpConexion.getUri().create(ServicesTravel.class); }


        try {
            Call<InfoTravelEntity> call = this.daoTravel.accept(idTravel);

            Log.d("fatal", call.request().toString());
            Log.d("fatal", call.request().headers().toString());

            call.enqueue(new Callback<InfoTravelEntity>() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onResponse(Call<InfoTravelEntity> call, Response<InfoTravelEntity> response) {

                    Toast.makeText(getApplicationContext(), "VIAJE ACEPTADO...", Toast.LENGTH_LONG).show();
                    Log.d("fatal",response.body().toString());

                   gloval.setGv_travel_current(response.body());



                    Intent i = new Intent(InfoDetailTravelAc.this,HomeActivity.class);
                    startActivity(i);


                }

                public void onFailure(Call<InfoTravelEntity> call, Throwable t) {
                    AlertDialog alertDialog = new AlertDialog.Builder(InfoDetailTravelAc.this).create();
                    alertDialog.setTitle("ERROR");
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.setMessage(t.getMessage());



                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }


            });

        } finally {
            this.daoTravel = null;
        }
    }




    public  void event_confirm(int idTravel) {

        button1.setEnabled(false);
        bt_confirmar_reserva.setEnabled(false);


        final GlovalVar gloval = ((GlovalVar) getApplicationContext());
        Call<List<InfoTravelEntity>> call =
                this.apiService.readReservation(idTravel
                        ,gloval.getGv_id_driver());

        // Log.d("***",call.request().body().toString());

        call.enqueue(new Callback<List<InfoTravelEntity>>() {
            @Override
            public void onResponse(Call<List<InfoTravelEntity>> call, Response<List<InfoTravelEntity>> response) {

                Log.d("Call request", call.request().toString());
                Log.d("Call request header", call.request().headers().toString());
                Log.d("Response raw header", response.headers().toString());
                Log.d("Response raw", String.valueOf(response.raw().body()));
                Log.d("Response code", String.valueOf(response.code()));


                if (response.code() == 200) {

                    //the response-body is already parseable to your ResponseBody object

                    Toast.makeText(getApplicationContext(), "Reserva Confirmada!", Toast.LENGTH_SHORT).show();
                    finish();

                } else if (response.code() == 404) {
                    AlertDialog alertDialog = new AlertDialog.Builder(InfoDetailTravelAc.this).create();
                    alertDialog.setTitle("ERROR" + "(" + response.code() + ")");
                    alertDialog.setMessage(response.errorBody().source().toString());
                    Toast.makeText(getApplicationContext(), "Sin Reservas!", Toast.LENGTH_SHORT).show();


                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }

                button1.setEnabled(true);
                bt_confirmar_reserva.setEnabled(true);


            }

            @Override
            public void onFailure(Call<List<InfoTravelEntity>> call, Throwable t) {
                AlertDialog alertDialog = new AlertDialog.Builder(InfoDetailTravelAc.this).create();
                alertDialog.setTitle("ERROR");
                alertDialog.setMessage(t.getMessage());

                Log.d("**", t.getMessage());

                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                button1.setEnabled(true);
                bt_confirmar_reserva.setEnabled(true);
            }
        });


    }

    public  void event_cancel(int idTravel) {
        button1.setEnabled(false);
        bt_confirmar_reserva.setEnabled(false);

        final GlovalVar gloval = ((GlovalVar)getApplicationContext());
        Call<List<InfoTravelEntity>> call =
                this.apiService.cacelReservation(idTravel,gloval.getGv_id_driver());


        call.enqueue(new Callback<List<InfoTravelEntity>>() {
            @Override
            public void onResponse(Call<List<InfoTravelEntity>> call, Response<List<InfoTravelEntity>> response) {

                Log.d("Call request", call.request().toString());
                Log.d("Call request header", call.request().headers().toString());
                Log.d("Response raw header", response.headers().toString());
                Log.d("Response raw", String.valueOf(response.raw().body()));
                Log.d("Response code", String.valueOf(response.code()));


                if (response.code() == 200) {

                    //the response-body is already parseable to your ResponseBody object

                    Toast.makeText(getApplicationContext(), "Reserva Cancelada!", Toast.LENGTH_SHORT).show();
                    finish();

                    //
                } else if (response.code() == 404) {
                    AlertDialog alertDialog = new AlertDialog.Builder(InfoDetailTravelAc.this).create();
                    alertDialog.setTitle("ERROR" + "(" + response.code() + ")");
                    alertDialog.setMessage(response.errorBody().source().toString());

                    Toast.makeText(getApplicationContext(), "Sin Reservas!", Toast.LENGTH_SHORT).show();


                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }

                button1.setEnabled(true);
                bt_confirmar_reserva.setEnabled(true);


            }

            @Override
            public void onFailure(Call<List<InfoTravelEntity>> call, Throwable t) {
                AlertDialog alertDialog = new AlertDialog.Builder(InfoDetailTravelAc.this).create();
                alertDialog.setTitle("ERROR");
                alertDialog.setMessage(t.getMessage());

                Log.d("**", t.getMessage());

                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                button1.setEnabled(true);
                bt_confirmar_reserva.setEnabled(true);
            }
        });


    }


    @Override
    public void onBackPressed() {
        finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
