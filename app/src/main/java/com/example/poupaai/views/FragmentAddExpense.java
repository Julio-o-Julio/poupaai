package com.example.poupaai.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.poupaai.database.LocalDatabase;
import com.example.poupaai.databinding.FragmentAddExpensesBinding;
import com.example.poupaai.entities.Expense;
import com.example.poupaai.entities.Month;

import java.util.List;

public class FragmentAddExpense extends Fragment {
    private FragmentAddExpensesBinding binding;
    private LocalDatabase db;
    private Expense expense;
    private List<Month> monthList;
    private Month selectedMonth = null;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentAddExpensesBinding.inflate(inflater, container, false);

        db = LocalDatabase.getDatabase(requireContext());

        Bundle arguments = getArguments();
        if (arguments != null) {
            expense = arguments.getParcelable("expense");
        }

        if (expense != null) {
            String street = "";
            int number = -1;

            try {
                android.location.Address locationAddress = searchAddress(address.getLatitude(), address.getLongitude());

                street = locationAddress.getThoroughfare();
                number = Integer.parseInt(locationAddress.getSubThoroughfare());

            }catch (Exception e){
                Toast.makeText(requireContext(),
                        "Erro ao tentar obter a Rua e o Numero através da latitude e longitude",
                        Toast.LENGTH_SHORT).show();
            }

            selectedCity = db.cityModel().findById(address.getCityId());

            binding.edtAddressCityName.setText(selectedCity.getName());
            binding.edtAddressCityState.setText(selectedCity.getState());
            binding.edtAddressDescription.setText(address.getDescription());
            binding.edtAddressStreet.setText(street);
            binding.edtAddressNumber.setText(String.valueOf(number));
        } else {
            cityList = db.cityModel().getAll();

            if (cityList != null) {
                List<City> cityListWithPrompt = new ArrayList<>();
                City promptCity = new City();
                promptCity.setName("Escolha uma cidade");
                cityListWithPrompt.add(promptCity);
                cityListWithPrompt.addAll(cityList);

                ArrayAdapter<City> adapter = getCityArrayAdapter(cityListWithPrompt);
                binding.nameCitySpinner.setAdapter(adapter);

                binding.nameCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position != 0) {
                            City city = (City) parent.getItemAtPosition(position);
                            binding.edtAddressCityName.setText(city.getName());
                            binding.edtAddressCityState.setText(city.getState());
                            selectedCity = city;
                        } else {
                            binding.edtAddressCityName.setText("");
                            binding.edtAddressCityState.setText("");
                            selectedCity = null;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            }
        }

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (address == null) {
            binding.btnAddAddress.setOnClickListener(this::addAddress);
        } else {
            binding.btnAddAddress.setOnClickListener(this::saveAddress);
            binding.btnAddAddress.setText(R.string.save_address);
            binding.btnRemoveAddress.setVisibility(View.VISIBLE);
            binding.btnRemoveAddress.setOnClickListener(this::removeAddress);
            binding.nameCitySpinner.setVisibility(View.GONE);
            binding.btnViewInMap.setVisibility(View.VISIBLE);
            binding.btnViewInMap.setOnClickListener(v ->
                    binding.frameMap.setVisibility(View.VISIBLE));
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private ArrayAdapter<City> getCityArrayAdapter(List<City> cityListWithPrompt) {
        ArrayAdapter<City> adapter = new ArrayAdapter<City>(requireContext(), android.R.layout.simple_spinner_item, cityListWithPrompt) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
                }
                City city = getItem(position);
                if (city != null) {
                    TextView textView = convertView.findViewById(android.R.id.text1);
                    textView.setText(city.getName());
                }
                return convertView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
                }
                City city = getItem(position);
                if (city != null) {
                    TextView textView = convertView.findViewById(android.R.id.text1);
                    textView.setText(city.getName());
                }
                return convertView;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private void addAddress(View view) {
        if (selectedCity == null) {
            Toast.makeText(requireContext(), "Escolha uma cidade", Toast.LENGTH_SHORT).show();
            return;
        }

        String addressDescription = binding.edtAddressDescription.getText().toString();
        String addressStreet = binding.edtAddressStreet.getText().toString();
        String addressNumberText = binding.edtAddressNumber.getText().toString();
        int addressNumber = 0;
        double addressLatitude = 0.0;
        double addressLongitude = 0.0;

        if (!validateAddressFields(addressDescription, addressStreet, addressNumberText)) {
            return;
        }
        addressNumber = Integer.parseInt(addressNumberText);

        try {
            android.location.Address locationAddress = searchLatitudeAndLongitude(selectedCity.getName(), addressStreet, addressNumber);

            addressLatitude = locationAddress.getLatitude();
            addressLongitude = locationAddress.getLongitude();

        }catch (Exception e){
            Toast.makeText(requireContext(),
                    "Erro ao tentar obter a Latitude e Longitude através do endereço",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Address newAddress = new Address();
        newAddress.setDescription(addressDescription);
        newAddress.setLatitude(addressLatitude);
        newAddress.setLongitude(addressLongitude);
        newAddress.setCityId(selectedCity.getId());

        db.addressModel().insert(newAddress);
        Toast.makeText(requireContext(), "O endereço " + addressDescription + " foi adicionado com sucesso",
                Toast.LENGTH_SHORT).show();

        NavController navController = Navigation.findNavController(view);
        navController.navigateUp();
    }

    private void saveAddress(View view) {
        if (selectedCity == null) {
            Toast.makeText(requireContext(), "Escolha uma cidade", Toast.LENGTH_SHORT).show();
            return;
        }

        String addressDescription = binding.edtAddressDescription.getText().toString();
        String addressStreet = binding.edtAddressStreet.getText().toString();
        String addressNumberText = binding.edtAddressNumber.getText().toString();
        int addressNumber = 0;
        double addressLatitude = address.getLatitude();
        double addressLongitude = address.getLongitude();

        String street = "";
        int number = -1;
        try {
            android.location.Address locationAddress = searchAddress(addressLatitude, addressLongitude);

            street = locationAddress.getThoroughfare();
            number = Integer.parseInt(locationAddress.getSubThoroughfare());

        }catch (Exception e){
            Toast.makeText(requireContext(),
                    "Erro ao tentar obter a Rua e o Numero através da latitude e longitude",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validateAddressFields(addressDescription, addressStreet, addressNumberText)) {
            return;
        }
        addressNumber = Integer.parseInt(addressNumberText);

        if (addressDescription.equalsIgnoreCase(address.getDescription()) &&
                addressStreet.equalsIgnoreCase(street) &&
                addressNumber == number) {
            Toast.makeText(requireContext(), "Nenhum dado foi alterado", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            android.location.Address locationAddress = searchLatitudeAndLongitude(selectedCity.getName(), addressStreet, addressNumber);

            addressLatitude = locationAddress.getLatitude();
            addressLongitude = locationAddress.getLongitude();

        }catch (Exception e){
            Toast.makeText(requireContext(),
                    "Erro ao tentar obter a Latitude e Longitude através do endereço",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        address.setDescription(addressDescription);
        address.setLatitude(addressLatitude);
        address.setLongitude(addressLongitude);

        db.addressModel().update(address);
        Toast.makeText(requireContext(), "O endereço " + addressDescription + " foi alterado com sucesso",
                Toast.LENGTH_SHORT).show();
    }

    private android.location.Address searchLatitudeAndLongitude(String cityName, String streetName, int number) throws IOException {
        android.location.Address locationAddress = null;
        List<android.location.Address> listLocationAddress;
        String numberText = String.valueOf(number);

        Geocoder geocoder = new Geocoder(getContext());

        listLocationAddress = geocoder.getFromLocationName(streetName + ", " + numberText + ", " + cityName, 1);

        if (!listLocationAddress.isEmpty()) {
            locationAddress = listLocationAddress.get(0);
        }

        return locationAddress;
    }

    private android.location.Address searchAddress(double latitude, double longitude) {
        android.location.Address locationAddress = null;
        List<android.location.Address> listLocationAddress;

        Geocoder geocoder = new Geocoder(requireContext());

        if (!Geocoder.isPresent()) {
            return null;
        }

        try {
            listLocationAddress = geocoder.getFromLocation(latitude, longitude, 2);

            if (!listLocationAddress.isEmpty()) {
                locationAddress = listLocationAddress.get(0);
            }
        } catch (IOException e) {
        }

        return locationAddress;
    }

    private boolean validateAddressFields(String description, String street, String numberText) {
        if(description.isEmpty()) {
            Toast.makeText(requireContext(), "A descrição do endereço é obrigatória", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(street.isEmpty()) {
            Toast.makeText(requireContext(), "O nome da rua é obrigatório", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(numberText.isEmpty()) {
            Toast.makeText(requireContext(), "O número do endereço é obrigatório", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            Integer.parseInt(numberText);
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Insira um valor válido para o número do endereço", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void removeAddress(View view) {
        int addressId = address.getId();

        db.addressModel().delete(address);

        Address address = db.addressModel().findById(addressId);

        if(address != null) {
            Toast.makeText(requireContext(), "Erro ao tentar excluir o endereço " + address.getDescription(),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(requireContext(), "O endereço foi excluido com sucesso",
                Toast.LENGTH_SHORT).show();

        NavController navController = Navigation.findNavController(view);
        navController.navigateUp();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (address != null) {
            mMap = googleMap;

            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title(address.getDescription()));
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
                    15));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
