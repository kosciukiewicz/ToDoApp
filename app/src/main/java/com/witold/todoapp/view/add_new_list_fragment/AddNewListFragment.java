package com.witold.todoapp.view.add_new_list_fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.thebluealliance.spectrum.SpectrumDialog;
import com.witold.todoapp.R;

import io.reactivex.disposables.Disposable;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddNewListFragment extends Fragment {
    private FrameLayout colorHolder;
    private EditText editTextNewListName;
    private TextInputLayout inputLayoutListName;
    private SpectrumDialog spectrumDialog;
    private MenuItem submitMenuItem;

    private AddNewListFragmentViewModel addNewListFragmentViewModel;

    public AddNewListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_new_list, container, false);
        this.initializeComponents(view);
        setHasOptionsMenu(true);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getResources().getString(R.string.add_new_list));
        }
        this.addNewListFragmentViewModel = ViewModelProviders.of(getActivity()).get(AddNewListFragmentViewModel.class);
        this.initializeColorHolder();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_new_list_menu, menu);
        this.submitMenuItem = menu.findItem(R.id.submit_add_new_list);
        this.submitMenuItem.setVisible(false);
        this.initializeValidationObservables();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.submit_add_new_list) {
            this.submitForm();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.editTextNewListName.setText("");
    }

    private void initializeComponents(View view) {
        this.colorHolder = view.findViewById(R.id.color_container_add_new_list_color);
        this.editTextNewListName = view.findViewById(R.id.edit_text_add_new_list_name);
        this.inputLayoutListName = view.findViewById(R.id.text_input_layout_add_new_list_name);
    }

    private void initializeColorHolder() {
        this.colorHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPickerDialog();
            }
        });
    }

    private void initializeValidationObservables() {
        Disposable disposable = RxTextView.textChanges(this.editTextNewListName)
                .skipInitialValue()
                .map(text -> text.length() > 0)
                .map(validation -> {
                    setValidationError(!validation);
                    return validation;
                }).subscribe(aBoolean -> {
                    this.submitMenuItem.setVisible(aBoolean);
                });
    }

    private void setValidationError(boolean error) {
        if (error) {
            inputLayoutListName.setError(getResources().getString(R.string.value_cannot_be_empty));
        } else {
            inputLayoutListName.setError(null);
        }
    }

    private void submitForm() {
        this.addNewListFragmentViewModel.addNewList(this.editTextNewListName.getText().toString(), ((ColorDrawable)this.colorHolder.getBackground()).getColor());
    }

    private void initializeColorDialog(int selectedColor) {
        SpectrumDialog.Builder builder = new SpectrumDialog.Builder(getContext())
                .setTitle(getResources().getString(R.string.pick_color))
                .setColors(R.array.demo_colors)
                .setSelectedColor(selectedColor)
                .setDismissOnColorSelected(true)
                .setOnColorSelectedListener((positiveResult, color) -> colorHolder.setBackgroundColor(color));
        this.spectrumDialog = builder.build();
    }

    private void showColorPickerDialog() {
        this.initializeColorDialog(((ColorDrawable) this.colorHolder.getBackground()).getColor());
        this.spectrumDialog.show(getFragmentManager(), "ColorDialog");
    }
}
