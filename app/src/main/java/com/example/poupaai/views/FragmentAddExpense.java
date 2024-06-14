package com.example.poupaai.views;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.poupaai.R;
import com.example.poupaai.database.LocalDatabase;
import com.example.poupaai.databinding.FragmentAddExpensesBinding;
import com.example.poupaai.entities.Expense;
import com.example.poupaai.entities.Month;
import com.example.poupaai.entities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FragmentAddExpense extends Fragment {
    private FragmentAddExpensesBinding binding;
    private LocalDatabase db;
    private Expense expense = null;
    private Month selectedMonth = null;
    private User loggedUser = null;

    private static final Map<String, String> monthNameToNumberMap = new HashMap<>();
    private static final Map<Integer, String> monthNumberToNameMap = new HashMap<>();

    static {
        monthNameToNumberMap.put("janeiro", "01");
        monthNameToNumberMap.put("fevereiro", "02");
        monthNameToNumberMap.put("março", "03");
        monthNameToNumberMap.put("abril", "04");
        monthNameToNumberMap.put("maio", "05");
        monthNameToNumberMap.put("junho", "06");
        monthNameToNumberMap.put("julho", "07");
        monthNameToNumberMap.put("agosto", "08");
        monthNameToNumberMap.put("setembro", "09");
        monthNameToNumberMap.put("outubro", "10");
        monthNameToNumberMap.put("novembro", "11");
        monthNameToNumberMap.put("dezembro", "12");

        monthNumberToNameMap.put(1, "janeiro");
        monthNumberToNameMap.put(2, "fevereiro");
        monthNumberToNameMap.put(3, "março");
        monthNumberToNameMap.put(4, "abril");
        monthNumberToNameMap.put(5, "maio");
        monthNumberToNameMap.put(6, "junho");
        monthNumberToNameMap.put(7, "julho");
        monthNumberToNameMap.put(8, "agosto");
        monthNumberToNameMap.put(9, "setembro");
        monthNumberToNameMap.put(10, "outubro");
        monthNumberToNameMap.put(11, "novembro");
        monthNumberToNameMap.put(12, "dezembro");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddExpensesBinding.inflate(inflater, container, false);

        db = LocalDatabase.getDatabase(requireContext());

        Bundle arguments = getArguments();
        if (arguments != null) {
            expense = arguments.getParcelable("expense");
            selectedMonth = arguments.getParcelable("month");
            loggedUser = arguments.getParcelable("user");

            if (loggedUser == null) {
                Log.e("FragmentAddExpense", "loggedUser is null");
            }
        } else {
            Log.e("FragmentAddExpense", "Arguments are null");
        }

        if (selectedMonth != null) {
            binding.edtExpenseMonth.setText(monthNameToNumberMap.get(selectedMonth.getMonthName()));
            binding.edtExpenseYear.setText(String.valueOf(selectedMonth.getYear()));
        }

        if (expense != null) {
            binding.edtExpenseDay.setText(String.valueOf(expense.getDay()));
            binding.edtExpenseValue.setText(String.valueOf(expense.getValue()));
            binding.edtExpenseDescription.setText(expense.getDescription());
        }

        if (loggedUser != null) {
            List<Expense> expenseList = db.expenseModel().getExpensesByUserId(loggedUser.getUid());

            if (expenseList != null) {
                List<Expense> expenseListWithPrompt = new ArrayList<>();
                Expense promptExpense = new Expense();
                promptExpense.setCategory("Categorias");
                expenseListWithPrompt.add(promptExpense);
                expenseListWithPrompt.addAll(expenseList);

                ArrayAdapter<Expense> expenseArrayAdapter = getExpenseArrayAdapter(expenseListWithPrompt);
                binding.spinnerExpenseCategories.setAdapter(expenseArrayAdapter);

                if (expense != null) {
                    String expenseCategory = expense.getCategory();
                    for (int i = 0; i < expenseArrayAdapter.getCount(); i++) {
                        Expense adapterExpense = expenseArrayAdapter.getItem(i);
                        if (adapterExpense != null && adapterExpense.getCategory().equals(expenseCategory)) {
                            binding.spinnerExpenseCategories.setSelection(i);
                            break;
                        }
                    }
                }

                binding.spinnerExpenseCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position != 0) {
                            Expense expense = (Expense) parent.getItemAtPosition(position);
                            binding.edtExpenseCategory.setText(expense.getCategory());
                        } else {
                            binding.edtExpenseCategory.setText("");
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            }
        }

        Spinner spinnerExpenseStatus = binding.spinnerExpenseStatus;
        ArrayAdapter<CharSequence> statusArrayAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.expense_status_array, android.R.layout.simple_spinner_item);
        statusArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExpenseStatus.setAdapter(statusArrayAdapter);

        if (expense != null) {
            String expenseStatus = expense.getStatus();
            if (expenseStatus != null) {
                for (int i = 0; i < statusArrayAdapter.getCount(); i++) {
                    if (statusArrayAdapter.getItem(i).toString().equals(expenseStatus)) {
                        spinnerExpenseStatus.setSelection(i);
                        break;
                    }
                }
            }
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (expense != null) {
            binding.btnAddExpense.setOnClickListener(v -> saveExpense(view, false));
            binding.btnAddExpense.setText(R.string.btn_save_expense);
            binding.btnRemoveExpense.setVisibility(View.VISIBLE);
            binding.btnRemoveExpense.setOnClickListener(this::removeExpense);
        } else {
            binding.btnAddExpense.setOnClickListener(v -> saveExpense(view, true));
        }

        binding.edtExpenseValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if (text.contains(".")) {
                    binding.edtExpenseValue.setText(text.replace(".", ","));
                    binding.edtExpenseValue.setSelection(binding.edtExpenseValue.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private ArrayAdapter<Expense> getExpenseArrayAdapter(List<Expense> expenseListWithPrompt) {
        ArrayAdapter<Expense> adapter = new ArrayAdapter<Expense>(requireContext(), android.R.layout.simple_spinner_item, expenseListWithPrompt) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
                }
                Expense expense = getItem(position);
                if (expense != null) {
                    TextView textView = convertView.findViewById(android.R.id.text1);
                    textView.setText(expense.getCategory());
                }
                return convertView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
                }
                Expense expense = getItem(position);
                if (expense != null) {
                    TextView textView = convertView.findViewById(android.R.id.text1);
                    textView.setText(expense.getCategory());
                }
                return convertView;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private void saveExpense(View view, boolean isNew) {
        String expenseDay = binding.edtExpenseDay.getText().toString();
        String expenseMonth = binding.edtExpenseMonth.getText().toString();
        String expenseYear = binding.edtExpenseYear.getText().toString();
        String expenseCategory = binding.edtExpenseCategory.getText().toString();
        String expenseValue = binding.edtExpenseValue.getText().toString();
        String expenseStatus = binding.spinnerExpenseStatus.getSelectedItem().toString();
        String expenseDescription = binding.edtExpenseDescription.getText().toString();

        expenseValue = expenseValue.replace(",", ".");

        if (!validateExpenseFields(expenseDay, expenseMonth, expenseYear, expenseCategory, expenseValue, expenseStatus)) {
            return;
        }

        String monthName = monthNumberToNameMap.get(Integer.parseInt(expenseMonth));
        int monthYear = Integer.parseInt(expenseYear);

        if (selectedMonth == null || !Objects.equals(selectedMonth.getMonthName(), monthName)) {
            List<Month> months = db.monthModel().findByMonthNameAndYear(monthName, monthYear);
            if (months.isEmpty()) {
                selectedMonth = new Month();
                selectedMonth.setMonthName(monthName);
                selectedMonth.setYear(monthYear);
                db.monthModel().insert(selectedMonth);
                selectedMonth = db.monthModel().findByMonthNameAndYear(monthName, monthYear).get(0);
            } else {
                selectedMonth = months.get(0);
            }
        }

        Expense newExpense = (expense != null && !isNew) ? expense : new Expense();
        newExpense.setDay(Integer.parseInt(expenseDay));
        newExpense.setCategory(expenseCategory);
        newExpense.setValue(Double.parseDouble(expenseValue));
        newExpense.setStatus(expenseStatus);
        newExpense.setDescription(expenseDescription);
        newExpense.setMonthId(selectedMonth.getId());
        newExpense.setUserId(loggedUser.getUid());

        if (isNew) {
            db.expenseModel().insert(newExpense);
            Toast.makeText(requireContext(), "A despesa foi adicionada com sucesso", Toast.LENGTH_SHORT).show();
        } else {
            db.expenseModel().update(newExpense);
            Toast.makeText(requireContext(), "A despesa foi alterada com sucesso", Toast.LENGTH_SHORT).show();
        }

        Navigation.findNavController(view).navigateUp();
    }

    private boolean validateExpenseFields(String expenseDay, String expenseMonth, String expenseYear, String expenseCategory, String expenseValue, String expenseStatus) {
        if (expenseDay.isEmpty() || !isValidInteger(expenseDay)) {
            showToast("Insira um valor válido para o dia");
            return false;
        }
        if (expenseMonth.isEmpty() || !isValidInteger(expenseMonth)) {
            showToast("Insira um valor válido para o mês");
            return false;
        }
        if (expenseYear.isEmpty() || !isValidInteger(expenseYear)) {
            showToast("Insira um valor válido para o ano");
            return false;
        }
        if (!validateDate(expenseDay, expenseMonth, expenseYear)) {
            return false;
        }
        if (expenseCategory.isEmpty()) {
            showToast("A categoria da despesa é obrigatória");
            return false;
        }
        if (expenseValue.isEmpty() || !isValidDouble(expenseValue)) {
            showToast("Insira um valor válido para o valor da despesa");
            return false;
        }
        if (expenseStatus.isEmpty() || expenseStatus.equals("Escolha um status")) {
            showToast("O status da despesa é obrigatório");
            return false;
        }
        return true;
    }

    private boolean validateDate(String expenseDay, String expenseMonth, String expenseYear) {
        int day = Integer.parseInt(expenseDay);
        int month = Integer.parseInt(expenseMonth);
        int year = Integer.parseInt(expenseYear);

        if (month < 1 || month > 12) {
            showToast("Insira um valor válido para o mês (1-12)");
            return false;
        }

        int[] daysInMonth = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        if (month == 2 && isLeapYear(year)) {
            daysInMonth[2] = 29;
        }

        if (day < 1 || day > daysInMonth[month]) {
            showToast("Insira um valor válido para o dia");
            return false;
        }

        return true;
    }

    private boolean isValidInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void removeExpense(View view) {
        db.expenseModel().delete(expense);
        Toast.makeText(requireContext(), "A despesa foi excluída com sucesso", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(view).navigateUp();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
