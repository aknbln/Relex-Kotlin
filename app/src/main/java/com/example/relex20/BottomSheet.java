package com.example.relex20;

import android.os.Bundle;
import android.os.DeadObjectException;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.relex20.model.TransactionViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheet extends BottomSheetDialogFragment {
    public BottomSheet() {

    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewModel accountViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        View view = inflater.inflate(R.layout.scanned_bottom_sheet, container, false);

        Button addExpense = view.findViewById(R.id.addExpense);
        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText expenseName = view.findViewById(R.id.expenseName);

                if (!TextUtils.isEmpty(expenseName.getText().toString())) {
                    String expenseString = expenseName.getText().toString();

                    // ADD TO VIEW MODEL TOTAL
                    Double expenseNum = Double.parseDouble(expenseString);
                    System.out.println("ExpenseNum is " + expenseNum + "------------");
                    ((TransactionViewModel) accountViewModel).updateTotal(expenseNum);


                    // NOTIFY USER
                    Toast.makeText(getContext(), "Added: " + expenseString, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Empty Expense", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }


}
