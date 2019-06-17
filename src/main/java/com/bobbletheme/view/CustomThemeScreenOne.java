package com.bobbletheme.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bobbletheme.R;
import com.bobbletheme.imagecropper.CropView;

public class CustomThemeScreenOne extends AbstractFragment implements CropView.OnImageTransformListener {

    private OnFragmentInteractionListener mListener;

    CropView cropView;
    Button nextBtn;
    Bitmap imageBitmap;

    public CustomThemeScreenOne() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CustomThemeScreenOne newInstance(String param1, String param2) {
        CustomThemeScreenOne fragment = new CustomThemeScreenOne();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_custom_theme_screen_one, container, false);
        cropView = (CropView) fragmentView.findViewById(R.id.image_cropper);
        cropView.setViewportRatio(1.25f);
        cropView.setViewportOverlayPadding(40);
        cropView.setOnImageTransformListener(this);
        if (imageBitmap != null  && cropView.getImageBitmap() == null) {
            cropView.setImageBitmap(imageBitmap);
        }
        nextBtn = (Button) fragmentView.findViewById(R.id.custom_fragment_1_next_btn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onNextPressed();
            }
        });
        return fragmentView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void setBitmap(Bitmap bitmap) {
        imageBitmap = bitmap;
        if (cropView != null && cropView.getImageBitmap() == null) {
            cropView.setImageBitmap(imageBitmap);
        }
    }

    @Override
    public void onImageTransform(Bundle bundle) {
        if (mListener != null) {
            mListener.onImageTransForm(bundle);
        }
    }

    public interface OnFragmentInteractionListener {
        void onNextPressed();
        void onImageTransForm(Bundle bundle);
    }
}
