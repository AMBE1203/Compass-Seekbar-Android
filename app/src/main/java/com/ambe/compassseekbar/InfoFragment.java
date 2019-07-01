package com.ambe.compassseekbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ambe.compassseekbar.seekbars.GenderImage;
import com.ambe.compassseekbar.seekbars.SeekbarCircle;
import com.ambe.compassseekbar.seekbars.SeekbarVertical;
import com.ambe.compassseekbar.seekbars.UnitWheelView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by AMBE on 1/7/2019 at 10:45 AM.
 */
public class InfoFragment extends Fragment {

    private final String TAG = InfoFragment.class.getSimpleName();
    @BindView(R.id.btn_tick)
    CardView btnTick;
    @BindView(R.id.tb_intro_first)
    RelativeLayout tbIntroFirst;
    @BindView(R.id.txt_male)
    TextView txtMale;
    @BindView(R.id.txt_female)
    TextView txtFemale;
    @BindView(R.id.switch_gender)
    LinearLayout switchGender;
    @BindView(R.id.seek_bar_vertical)
    SeekbarVertical seekBarVertical;
    @BindView(R.id.tvWeight)
    TextView tvWeight;
    @BindView(R.id.seek_bar_circle)
    SeekbarCircle seekBarCircle;
    @BindView(R.id.unitView)
    UnitWheelView unitView;
    private GenderImage genderImage;
    private Unbinder unbinder;
    private int idGender;
    private String height = "170 cm";
    private String weight = "70 kg";
    private String unit = "kg/cm";
    private NumberFormat formatter = new DecimalFormat("#0.0");
    private int mPro;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    public InfoFragment() {
        // Required empty public constructor
    }

    public static InfoFragment newInstance() {

        InfoFragment introFirstFragment = new InfoFragment();
        return introFirstFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int nbWeight = 70;
        int nbHeight = 170;
        mPro = nbWeight;


        genderImage = new GenderImage(getContext());
        genderImage.setImageResource(idGender == 0 ? R.drawable.ic_male_svg : R.drawable.ic_female);
        if (idGender == 0) switchGender(txtMale, txtFemale);
        else switchGender(txtFemale, txtMale);
        if (unit.equals(UnitWheelView.UNIT_KG_CM)) {
            tvWeight.setText(nbWeight + " kg");

        } else {
            tvWeight.setText(formatter.format(Double.parseDouble(weight.split(" ")[0]) * 2.20462262) + " ibs");
            tvWeight.setTextSize(12);

        }

        seekBarVertical.setmIndicatorYStart(nbHeight / 2);
        seekBarVertical.setGenderImage(genderImage);

        seekBarVertical.post(() -> {
            seekBarVertical.setUnit(unit);
            seekBarVertical.initialize();
            seekBarVertical.setmIndicatorYStart(nbHeight / 2);
        });
        seekBarCircle.setProgressSweep(nbWeight);
        seekBarCircle.post(() -> {
            seekBarCircle.setUnit(unit);
            seekBarCircle.initialize();
            seekBarCircle.setProgressSweep(nbWeight);
        });

        btnTick.setCardBackgroundColor(Color.parseColor("#1FD07B"));

        int width = getScreenWidth();
        genderImage.currentX = width / 2 - nbWeight;
        genderImage.sizeX = nbWeight * 2;
        seekBarVertical.setmOnCustomsSeekbarChangeListener(new SeekbarVertical.OnCustomsSeekbarChangeListener() {
            @Override
            public void onChanged(SeekbarVertical seekbarVertical, int progress, boolean frommUser) {
                height = progress * 2 + " cm";
            }

            @Override
            public void onStartTrackingTouch(SeekbarVertical seekbarVertical) {

            }

            @Override
            public void onStopTrackingTouch(SeekbarVertical seekbarVertical) {

            }
        });

        seekBarCircle.setmOnSwagSeekbarChangeListener(new SeekbarCircle.OnSwagSeekbarChangeListener() {
            @Override
            public void onSeekbarChanged(SeekbarCircle seekbarCircle, int progress, boolean fromUser) {
                if (fromUser) {
                    genderImage.currentX = width / 2 - progress;
                    genderImage.sizeX = progress * 2;
                    mPro = progress;
                    weight = progress + " kg";

                    if (unit.equals(UnitWheelView.UNIT_KG_CM)) {
                        tvWeight.setTextSize(20);
                        tvWeight.setText(weight);


                    } else {
                        String weight1 = formatter.format(progress * 2.20462262) + " Ibs";
                        tvWeight.setTextSize(12);
                        tvWeight.setText(weight1);


                    }
                    seekBarVertical.invalidate();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekbarCircle seekbarCircle) {
            }

            @Override
            public void onStopTrackingTouch(SeekbarCircle seekbarCircle) {
            }
        });

        unitView.setUnit(unit);
        unitView.setUnitChangeListener((oldUnitType, newUnitType) -> {
            unit = newUnitType;
            seekBarCircle.setUnit(newUnitType);
            seekBarVertical.setUnit(newUnitType);
            if (newUnitType.equals(UnitWheelView.UNIT_KG_CM)) {
                tvWeight.setTextSize(20);
                tvWeight.setText(mPro + " Kg");

            } else {
                String weight2 = formatter.format(mPro * 2.20462262) + " Ibs";
                tvWeight.setTextSize(12);
                tvWeight.setText(weight2);

            }


        });

    }


    private int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }


    private void switchGender(TextView txt1, TextView txt2) {
        txt1.setTextColor(getResources().getColor(R.color.white));
        txt1.setBackground(getResources().getDrawable(R.drawable.bg_gender));
        txt2.setBackground(getResources().getDrawable(R.drawable.bg_gender_not));
        txt2.setTextColor(getResources().getColor(R.color.black));

    }

    @OnClick({R.id.btn_tick, R.id.txt_male, R.id.txt_female})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_tick:


                break;
            case R.id.txt_male:
                switchGender(txtMale, txtFemale);
                genderImage.setImageResource(R.drawable.ic_male_svg);
                idGender = 0;
                seekBarVertical.invalidate();
                break;
            case R.id.txt_female:
                switchGender(txtFemale, txtMale);
                genderImage.setImageResource(R.drawable.ic_female);
                idGender = 1;
                seekBarVertical.invalidate();

                break;
        }
    }


}
