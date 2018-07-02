package com.witold.todoapp.view.add_new_task_fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.witold.todoapp.R;
import com.witold.todoapp.view.task_list_fragment.SingleTaskListViewModel;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddNewTaskFragment extends Fragment {
    private int subListId;
    private MenuItem submitMenuItem;
    private EditText editTextName;
    private EditText editTextNote;
    private EditText editTextDateTime;
    private CheckBox checkBoxPriority;
    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutDateTime;
    private SimpleDateFormat dateFormat;
    private SingleTaskListViewModel singleTaskListViewModel;

    public AddNewTaskFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_new_task, container, false);
        this.initializeComponents(view);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.subListId = getArguments().getInt("subListId");
        this.singleTaskListViewModel = ViewModelProviders.of(getActivity()).get(SingleTaskListViewModel.class);
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
        this.clearForm();
    }

    private void initializeComponents(View view) {
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        this.dateFormat.setLenient(false);

        this.editTextName = view.findViewById(R.id.edit_text_add_new_task_name);
        this.editTextNote = view.findViewById(R.id.edit_text_add_new_task_name_note);
        this.editTextDateTime = view.findViewById(R.id.edit_text_add_new_task_name_alertDate);
        this.checkBoxPriority = view.findViewById(R.id.checkbox_priority);
        this.textInputLayoutDateTime = view.findViewById(R.id.text_input_layout_add_new_task_alertDate);
        this.textInputLayoutName = view.findViewById(R.id.text_input_layout_add_new_task_name);
        this.editTextDateTime.setText(this.dateFormat.format(Calendar.getInstance().getTime()));
    }

    private void clearForm(){
        this.editTextName.setText("");
        this.editTextNote.setText("");
        this.checkBoxPriority.setChecked(false);
        this.editTextDateTime.setText(this.dateFormat.format(Calendar.getInstance().getTime()));
    }

    private void initializeValidationObservables() {
        Observable nameObservable = RxTextView.textChanges(this.editTextName)
                .skipInitialValue()
                .map(text -> text.length() > 0)
                .map(validation -> {
                    setNameError(!validation);
                    return validation;
                });

        Observable dateTimeObservable = RxTextView.textChanges(this.editTextDateTime)
                .map(text -> {
                    Date date = getDate();
                    return !((editTextDateTime.getText().toString().length() != 0) && date == null);
                })
                .map(validation -> {
                    setDateTimeError(!validation);
                    return validation;
                });

        Disposable disposable = Observable.combineLatest(nameObservable, dateTimeObservable, (BiFunction<Boolean, Boolean, Boolean>) (aBoolean, aBoolean2) -> aBoolean && aBoolean2).subscribe(aBoolean -> {
            this.submitMenuItem.setVisible((boolean)aBoolean);
        });
    }

    private void submitForm() {
        this.singleTaskListViewModel.addNewTask(editTextName.getText().toString(), editTextNote.getText().toString(), getDate(), checkBoxPriority.isChecked(), this.subListId);
        (getActivity()).onBackPressed();
    }

    private Date getDate() {
        try {
            if (this.editTextDateTime.getText().toString().length() == 0) {
                return null;
            } else {
                return dateFormat.parse(this.editTextDateTime.getText().toString());
            }
        } catch (ParseException e) {
            return null;
        }
    }

    private void setNameError(boolean error) {
        if (error) {
            this.textInputLayoutName.setError(getResources().getString(R.string.value_cannot_be_empty));
        } else {
            this.textInputLayoutName.setError(null);
        }
    }

    private void setDateTimeError(boolean error) {
        if (error) {
            this.textInputLayoutDateTime.setError(getResources().getString(R.string.wrong_date_time_format));
        } else {
            this.textInputLayoutDateTime.setError(null);
        }
    }
}
