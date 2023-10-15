package ru.rodipit.calculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;


import ru.rodipit.calculator.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private CalculatorViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(CalculatorViewModel.class);
        viewModel.init();

    }

    @Override
    protected void onStart() {
        super.onStart();
        viewModel.text.observe(this, strings -> {
            String result = strings.toString().replace("[", "").replace(", ", "").replace("]", "");
            InputFilter[] fArray = new InputFilter[1];
            int maxLength;
            int maxLines = 1;
            float size;
            if (result.length() > 9){
                maxLength = 12;
                size = 44;
                if(result.length() > 12){
                    maxLines = 2;
                    maxLength *= 2;
                }
            } else {
                maxLength = 9;
                size = 64;
            }
            fArray[0] = new InputFilter.LengthFilter(maxLength);

            binding.resultTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
            binding.resultTextView.setFilters(fArray);
            binding.resultTextView.setMaxLines(maxLines);

            binding.resultTextView.setText(result.substring(Math.max((result.length() - maxLength), 0)));
        });

    }

    public void onClickButton(View v){
        String buttonText = ((TextView) v).getText().toString();
        if ("0123456789.".contains(buttonText)){
            viewModel.addDigit(buttonText);
        } else if (buttonText.equals("%")) {
            viewModel.percent();
        } else if (buttonText.equals("=")){
            viewModel.calculate();
        } else if (buttonText.equals("AC")) {
            viewModel.clearAll();
        } else if (buttonText.equals("C")) {
            viewModel.delete();
        } else {
            viewModel.addOperator(buttonText);
        }
    }


}