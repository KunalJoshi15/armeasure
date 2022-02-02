package com.example.armeasure;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MeasureActivity extends AppCompatActivity {

    private float seekbarlength = 0f;
    private ArFragment arFragment;
    private ModelRenderable model_renderable;
    private AnchorNode myanchornode;
    private DecimalFormat form_numbers = new DecimalFormat("#0.00 m");
    private Set<String> myset;
    private Anchor a1 = null, a2 = null;

    private HitResult hit;

    private TextView text;
    private SeekBar sk;
    private Button btn_save, btn_width, btn_height;

    List<AnchorNode> anchorNodes = new ArrayList<>();
    private boolean measure_height = false;
    private float measurement = 0.0f;
    private String message;
    private RadioButton selectedButton;
    private String name = "Height";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.measurementFragment);
        text = (TextView) findViewById(R.id.text);
        sk = (SeekBar) findViewById(R.id.sk_height_control);
        btn_height = (Button) findViewById(R.id.btn_height);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_width = (Button) findViewById(R.id.btn_width);
        sk.setEnabled(false);

        btn_width.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetLayout();
                measure_height = false;
                text.setText("Place objects in two anchornodes for width");
            }
        });

        btn_height.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetLayout();
                measure_height = true;
                text.setText("Use seek bar for finding height.");
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(measurement != 0.0f)
                    saveDialog();
                else
                    Toast.makeText(MeasureActivity.this, "Make a measurement before saving", Toast.LENGTH_SHORT).show();
            }
        });

        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekbarlength = progress;
                measurement = progress/100f;
                text.setText("Height: "+form_numbers.format(measurement));
                myanchornode.setLocalScale(new Vector3(1f, progress/10f, 1f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        ModelRenderable.builder()
                .setSource(this, R.raw.cube)
                .build()
                .thenAccept(renderable -> model_renderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load model_renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (model_renderable == null) {
                        return;
                    }
                    hit = hitResult;

                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    if(!measure_height) {
                        if(a2 != null){
                            deleteAnchors();
                        }
                        if (a1 == null) {
                            a1 = anchor;
                        } else {
                            a2 = anchor;
                            measurement = getDistanceBetweenAnchors(a1, a2);
                            text.setText("Width: " + form_numbers.format(measurement));
                        }
                    }
                    else{
                        deleteAnchors();
                        a1 = anchor;
                        text.setText("Move the slider till the cube reaches the upper base");
                        sk.setEnabled(true);
                    }

                    myanchornode = anchorNode;
                    anchorNodes.add(anchorNode);
                    TransformableNode a = new TransformableNode(arFragment.getTransformationSystem());
                    a.setParent(anchorNode);
                    a.setRenderable(model_renderable);
                    a.select();
                    a.getScaleController().setEnabled(false);
                });
    }

    private float getDistanceBetweenAnchors(Anchor anchor1, Anchor anchor2) {
        float[] distance_vector = anchor1.getPose().inverse()
                .compose(anchor2.getPose()).getTranslation();
        float totalDistanceSquared = 0;
        for (int i = 0; i < 3; ++i)
            totalDistanceSquared += distance_vector[i] * distance_vector[i];
        return (float) Math.sqrt(totalDistanceSquared);
    }

    private void saveDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MeasureActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_save, null);

        EditText et_measure = (EditText) mView.findViewById(R.id.et_measure);
        RadioGroup radioGroup = (RadioGroup) mView.findViewById(R.id.radioGroup);
        mBuilder.setTitle("Measurement title");
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i==R.id.heightButton){
                    Log.e("error","height");
                }else if(i==R.id.widthButton){
                    Log.e("error","width");

                }
                selectedButton = radioGroup.findViewById(i);
              name=  selectedButton.getText().toString();
            }
        });

        mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(et_measure.getText().toString().length() != 0){
                    SharedPreferences sharedPref = getSharedPreferences("myKey",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
//                    Log.d("radioid","this is radio group button"+selectedRadioButtonId);
//                   Log.e("error",name);
                    if(name.equals("Width")){
                        editor.putString("width", et_measure.getText().toString()+":"+form_numbers.format(measurement));
                        editor.commit();
//                        Log.d("width",sharedPref.getString("width",""));
                    }
                    if(name.equals("Height")) {
                        editor.putString("height", et_measure.getText().toString()+":"+form_numbers.format(measurement));
                        editor.commit();
//                        Log.d("height",sharedPref.getString("height",""));
                    }
                    dialogInterface.dismiss();
                }
                else
                    Toast.makeText(MeasureActivity.this, "Title can't be empty", Toast.LENGTH_SHORT).show();
            }
        });

        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();

        dialog.show();
    }

    private void resetLayout(){
        sk.setProgress(10);
        sk.setEnabled(false);
        measure_height = false;
        deleteAnchors();
    }

    private void deleteAnchors(){
        a1 = null;
        a2 = null;
        for (AnchorNode n : anchorNodes) {
            arFragment.getArSceneView().getScene().removeChild(n);
            n.getAnchor().detach();
            n.setParent(null);
            n = null;
        }
    }
}
