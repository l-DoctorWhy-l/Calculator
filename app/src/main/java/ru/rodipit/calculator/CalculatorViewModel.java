package ru.rodipit.calculator;


import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayDeque;
import java.util.ArrayList;


public class CalculatorViewModel extends ViewModel {
    MutableLiveData<ArrayList<String>> text = new MutableLiveData<>(new ArrayList<>(5));
    ArrayList<String> formula = new ArrayList<>();
    int focus = 0;

    void init(){
        formula.add("0");
        updateText();
    }

    void addDigit(String digit){
        System.out.println("Digit");
        if(getFormulaSize() > 23)
            return;

        if ("-÷+×".contains(formula.get(focus))){
            focus++;
            if(formula.size() - 1 < focus)
                formula.add("");
        }
        if(digit.equals(".") && formula.get(focus).length() == 0){
            focus--;
            return;
        }
        if (digit.equals(".") && formula.get(focus).contains(".")){
            return;
        }

        if((focus == 0 && formula.get(focus).equals("0"))){
            if (digit.equals(".")){
                formula.set(focus, formula.get(focus) + digit);
            }
            else
                formula.set(focus, digit);
            updateText();
            return;
        }
        if(formula.get(focus).contains("E") && focus == 0){
            clearAll();
            formula.set(focus, digit);
            updateText();
            return;
        }
        formula.set(focus, formula.get(focus) + digit);

        updateText();
    }

    void calculate(){
        System.out.println("Calc");
        ArrayList<String> postFix = toPostfix();
        if (postFix == null)
            return;
        System.out.println(postFix);

        Double result = calculatePostfix(postFix);
        System.out.println(result);
        clearAll();
        formula.set(focus, String.valueOf(result));
        updateText();
    }

    void percent(){
        System.out.println("percent");
        try{
            formula.set(focus, String.valueOf((Double.parseDouble(formula.get(focus)) / 100)));
        } catch (Exception e) {
            return;
        }
        updateText();
    }

    void addOperator(String operator){

        System.out.println("operator");
        if(getFormulaSize() > 23)
            return;
        if("-÷+×".contains(formula.get(focus)))
            formula.set(focus, operator);
        else {
            focus++;
            formula.add(focus, operator);
        }
        updateText();
    }

    void clearAll(){

        formula.clear();
        focus = 0;
        init();
        updateText();
    }
    void delete(){

        if (formula.get(focus).contains("E") || formula.get(focus).contains("-") && focus == 0){
            clearAll();
            return;
        }
        if(formula.get(focus).equals("")){
            if(focus > 0)
                focus--;
            else
                return;
        }
        formula.set(focus, formula.get(focus).substring(0, formula.get(focus).length() - 1));
        if(formula.get(focus).length() == 0 && focus != 0)
            focus--;
        updateText();
    }

    void updateText(){
        System.out.println(formula.toString());
        System.out.println(focus);
        if (focus == 0 && formula.get(focus).equals("") || formula.contains("Infinity") || formula.contains("NaN"))
            clearAll();
        else
            text.setValue(formula);
    }

    public static boolean isNumber(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    private double execute(String op, double first, double second){
        switch(op) {
            case "+":
                return first + second;
            case "-":
                return  first - second;
            case "÷":{
                return first / second;
            }
            case "×":
                return first * second;
        }
        return 0;
    }

    private ArrayList<String> toPostfix(){
        ArrayList<String> postFix = new ArrayList<>();
        ArrayDeque<String> operators = new ArrayDeque<>();
        if(!isNumber(formula.get(focus)))
            return null;
        for (String item : formula){
            if(item.equals(""))
                break;
            if(isNumber(item)){
                postFix.add(item);
            } else {
                if(operators.isEmpty())
                    operators.addLast(item);
                else {
                    if("÷×".contains(item)){
                        if("÷×".contains(operators.peekLast())){
                            postFix.add(operators.pollLast());
                            operators.addLast(item);
                        } else{
                            operators.addLast(item);
                        }
                    }
                    else{
                        if("-+".contains(operators.peekLast())){
                            postFix.add(operators.pollLast());
                            operators.addLast(item);
                        } else if("÷×".contains(operators.peekLast())){
                            while (!operators.isEmpty() && "÷×-+".contains(operators.peekLast())){
                                postFix.add(operators.pollLast());
                            }
                            operators.addLast(item);
                        } else {
                            postFix.add(item);
                        }
                    }
                }
            }
        }
        while (!operators.isEmpty()){
            postFix.add(operators.pollLast());
        }
        return postFix;
    }

    @SuppressLint("DefaultLocale")
    private Double calculatePostfix(ArrayList<String> postFix){
        ArrayDeque<Double> result = new ArrayDeque<>();
        for (String item : postFix){
            if(isNumber(item))
                result.addLast(Double.parseDouble(item));
            else{
                Double b = result.pollLast();
                Double a = result.pollLast();
                result.addLast(execute(item, a, b));
            }
        }
        return result.pollLast();
    }

    int getFormulaSize(){
        int count = 0;
        for(String item : formula){
            count += item.length();
        }
        return count;
    }
}
