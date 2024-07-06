package com.example.projekatmobilne.adapters;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekatmobilne.R;
import com.example.projekatmobilne.activities.AccommodationsDifferencesCompareActivity;
import com.example.projekatmobilne.model.Enum.RequestType;
import com.example.projekatmobilne.model.requestDTO.AvailabilityDTO;
import com.example.projekatmobilne.model.responseDTO.innerDTO.AvailabilityDTOInner;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AvailabilitiesAdapter extends  RecyclerView.Adapter<AvailabilitiesViewHolder>{
    private Context context;
    private List<AvailabilityDTO> dataList;
    private List<AvailabilityDTO> existingAvailabilitiesMarkedDeletion;
    private Map<Long, AvailabilityDTO> existingAvailabilitiesForEdit;
    private Dialog addAvailabilityDialog;
    private EditText dateRangeEdit, cancelDeadlineEdit, priceEdit;
    private TextInputLayout dateRangeInput, cancelDeadlineInput, priceInput;
    private CheckBox checkBoxIsPerGuest;
    private int red, green, orange;


    private LocalDate dateStart, dateEnd, dateCancel;
    private FragmentManager supportFragmentManager;
    private int editingPosition = -1;
    public void setSearchList(List<AvailabilityDTO> dataSearchList){
        this.dataList = dataSearchList;

        notifyDataSetChanged();
    }

    public AvailabilitiesAdapter(Context context, List<AvailabilityDTO> dataList, FragmentManager supportFragmentManager){
        this.context = context;
        this.dataList = dataList;
        this.supportFragmentManager = supportFragmentManager;
        this.existingAvailabilitiesMarkedDeletion = new ArrayList<>();
        this.existingAvailabilitiesForEdit = new HashMap<>();
        green = ContextCompat.getColor(context, R.color.green);
        red = ContextCompat.getColor(context, R.color.red);
        orange = ContextCompat.getColor(context, R.color.orange);
    }

    public List<AvailabilityDTO> getEditList(){
        existingAvailabilitiesMarkedDeletion.addAll(dataList);
        return existingAvailabilitiesMarkedDeletion;
    }

    public void addAvailability(AvailabilityDTO avail){
        dataList.add(avail);
        notifyItemInserted(dataList.size() - 1);
    }

    public void addExistingAvailability(AvailabilityDTO avail){
        existingAvailabilitiesForEdit.put(avail.getId(), avail);
    }

    @NonNull
    @Override
    public AvailabilitiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.availability_add, parent, false);

        return new AvailabilitiesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvailabilitiesViewHolder holder, int position) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        AvailabilityDTO item = dataList.get(position);
        holder.txtPrice.setText("Price: " + item.getPrice().toString());
        holder.txtIsPerGuest.setText("Is per guest price: " + item.getPricePerGuest().toString());
        holder.txtDateRange.setText("Date range: " + item.getStartDate().format(formatter) + " " + item.getEndDate().format(formatter));
        holder.txtCancelDate.setText("Cancel date: " + item.getCancellationDeadline().format(formatter));

        if(context instanceof AccommodationsDifferencesCompareActivity){
            holder.btnRemoveAvailability.setVisibility(View.GONE);
            holder.btnChangeAvailability.setVisibility(View.GONE);

            if(item.getRequestType().equals(RequestType.CREATE)){
                holder.txtPrice.setTextColor(green);
                holder.txtIsPerGuest.setTextColor(green);
                holder.txtDateRange.setTextColor(green);
                holder.txtCancelDate.setTextColor(green);
            }

            if(item.getRequestType().equals(RequestType.DELETE)){
                holder.txtPrice.setTextColor(red);
                holder.txtIsPerGuest.setTextColor(red);
                holder.txtDateRange.setTextColor(red);
                holder.txtCancelDate.setTextColor(red);
            }

            if(item.getRequestType().equals(RequestType.EDIT)){

                AvailabilityDTO avail = existingAvailabilitiesForEdit.get(item.getId());
                if(avail != null){
                    System.out.println(avail);

                    holder.txtPriceOld.setText("Price: " + avail.getPrice().toString());
                    holder.txtIsPerGuestOld.setText("Is per guest price: " + avail.getPricePerGuest().toString());
                    holder.txtDateRangeOld.setText("Date range: " + avail.getStartDate().format(formatter) + " " + avail.getEndDate().format(formatter));
                    holder.txtCancelDateOld.setText("Cancel date: " + avail.getCancellationDeadline().format(formatter));



                    if(!avail.getPricePerGuest().equals(item.getPricePerGuest())){
                        holder.txtChangeFromMessage.setVisibility(View.VISIBLE);
                        holder.linearLayoutAvailabilityOld.setVisibility(View.VISIBLE);
                        holder.txtIsPerGuestOld.setVisibility(View.VISIBLE);
                        holder.txtIsPerGuest.setTextColor(orange);
                    }

                    if(!avail.getPrice().equals(item.getPrice())){
                        holder.txtChangeFromMessage.setVisibility(View.VISIBLE);
                        holder.linearLayoutAvailabilityOld.setVisibility(View.VISIBLE);
                        holder.txtPriceOld.setVisibility(View.VISIBLE);
                        holder.txtPrice.setTextColor(orange);
                    }

                    if(!avail.getEndDate().equals(item.getEndDate()) || !avail.getStartDate().equals(item.getStartDate())){
                        holder.txtChangeFromMessage.setVisibility(View.VISIBLE);
                        holder.linearLayoutAvailabilityOld.setVisibility(View.VISIBLE);
                        holder.txtDateRangeOld.setVisibility(View.VISIBLE);
                        holder.txtDateRange.setTextColor(orange);
                    }

                    if(!avail.getCancellationDeadline().equals(item.getCancellationDeadline())){
                        holder.txtChangeFromMessage.setVisibility(View.VISIBLE);
                        holder.linearLayoutAvailabilityOld.setVisibility(View.VISIBLE);
                        holder.txtCancelDateOld.setVisibility(View.VISIBLE);
                        holder.txtCancelDate.setTextColor(orange);
                    }
                }


            }

        }

        holder.btnRemoveAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AvailabilityDTO avail = dataList.get(holder.getAdapterPosition());
                if(avail.getId() >= 1){
                    avail.setRequestType(RequestType.DELETE);
                    existingAvailabilitiesMarkedDeletion.add(avail);
                    dataList.remove(holder.getAdapterPosition());
                }


                notifyDataSetChanged();
                notifyItemRemoved(holder.getAdapterPosition());
                System.out.println("SADA JE DATALIST:" + dataList.size());
                System.out.println("SADA JE EXISTING BLA BLA:" + existingAvailabilitiesMarkedDeletion.size());
            }
        });

        holder.btnChangeAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editingPosition = holder.getAdapterPosition();
                setupAddAvailabilityDialog();
                dateRangeInput.setError(null);
                cancelDeadlineInput.setError(null);
                priceInput.setError(null);

                checkBoxIsPerGuest.setChecked(Boolean.parseBoolean(item.getPricePerGuest().toString()));
                dateRangeEdit.setText(item.getStartDate().format(formatter) + " " + item.getEndDate().format(formatter));
                cancelDeadlineEdit.setText(item.getCancellationDeadline().format(formatter));
                priceEdit.setText(item.getPrice().toString());
                dateStart = item.getStartDate();
                dateEnd = item.getEndDate();
                dateCancel = item.getCancellationDeadline();

                addAvailabilityDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private void setupAddAvailabilityDialog(){
        addAvailabilityDialog = new Dialog(context);
        addAvailabilityDialog.setContentView(R.layout.custom_dialog_availability);
        addAvailabilityDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addAvailabilityDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.custom_dialog_bg));

        dateRangeEdit = addAvailabilityDialog.findViewById(R.id.inputEditTextDateRange);
        cancelDeadlineEdit = addAvailabilityDialog.findViewById(R.id.inputEditTextDateCancel);
        priceEdit = addAvailabilityDialog.findViewById(R.id.inputEditTextPricePerGuest);
        dateRangeInput = addAvailabilityDialog.findViewById(R.id.inputLayoutDateRange);
        cancelDeadlineInput = addAvailabilityDialog.findViewById(R.id.inputLayoutDateCancel);
        priceInput = addAvailabilityDialog.findViewById(R.id.inputLayoutPricePerGuest);

        checkBoxIsPerGuest = addAvailabilityDialog.findViewById(R.id.checkBoxIsPerGuest);


        addAvailabilityDialog.findViewById(R.id.btnCancelInfo).setOnClickListener(v -> {
            addAvailabilityDialog.dismiss();
        });

        addAvailabilityDialog.findViewById(R.id.btnConfirmInfo).setOnClickListener(v -> {


            dateRangeInput.setError(null);
            cancelDeadlineInput.setError(null);
            priceInput.setError(null);

            boolean isValid = true;
            if (dateRangeEdit.getText().toString().isEmpty()) {
                dateRangeInput.setError("This field cannot be empty");
                isValid = false;
            }
            if (priceEdit.getText().toString().isEmpty() || Long.parseLong(priceEdit.getText().toString()) == 0) {
                priceInput.setError("This field cannot be empty");
                isValid = false;
            }
            if (cancelDeadlineEdit.getText().toString().isEmpty()) {
                cancelDeadlineInput.setError("This field cannot be empty");
                isValid = false;
            }
            if(!isValid) return;


            for(AvailabilityDTO a: dataList){
                if(!a.equals(dataList.get(editingPosition))){
                    if((dateStart.isAfter(a.getStartDate()) && dateStart.isBefore(a.getEndDate()))
                            || dateStart.isEqual(a.getStartDate()) || dateStart.isEqual(a.getEndDate())){
                        isValid = false;
                        Toast.makeText(context, "Date range interferes with existing availability", Toast.LENGTH_SHORT).show();
                    }
                    if((dateEnd.isAfter(a.getStartDate()) && dateEnd.isBefore(a.getEndDate()))
                            || dateEnd.isEqual(a.getStartDate()) || dateEnd.isEqual(a.getEndDate())){
                        isValid = false;
                        Toast.makeText(context, "Date range interferes with existing availability", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            if(!dateCancel.isBefore(dateStart)){
                isValid = false;
                Toast.makeText(context, "Cancellation date must be before start date", Toast.LENGTH_SHORT).show();
            }

            if (!isValid) return;



            AvailabilityDTO availabilityDTO = dataList.get(editingPosition);
            availabilityDTO.setPrice(Long.parseLong(priceEdit.getText().toString()));
            availabilityDTO.setStartDate(dateStart);
            availabilityDTO.setEndDate(dateEnd);
            availabilityDTO.setCancellationDeadline(dateCancel);
            availabilityDTO.setPricePerGuest(checkBoxIsPerGuest.isChecked());

            notifyItemChanged(editingPosition);
            addAvailabilityDialog.dismiss();
        });


        setupDateRangePicker();
        cancelDeadlineEdit.setOnClickListener(v -> {


            DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    String date = dayOfMonth + "-" + (month+1) + "-" + year;
                    dateCancel = LocalDate.of(year, month+1, dayOfMonth);
                    cancelDeadlineEdit.setText(date);
                }
            }, LocalDate.now().getYear(), LocalDate.now().getMonthValue() - 1, LocalDate.now().getDayOfMonth());

            final Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            dialog.show();
        });
    }

    private void setupDateRangePicker() {

        dateRangeEdit.setOnClickListener(v -> {

            final Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 1);

            CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder().setValidator(DateValidatorPointForward.from(calendar.getTimeInMillis()));
            CalendarConstraints constraints = constraintsBuilder.build();

            MaterialDatePicker<Pair<Long, Long>> materialDatePicker = MaterialDatePicker.Builder.dateRangePicker().setSelection(new Pair<>(
                    null,
                    null
            )).setCalendarConstraints(constraints).build();


            materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                @Override
                public void onPositiveButtonClick(Pair<Long, Long> selection) {
                    String start = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(selection.first));
                    String end = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(selection.second));
                    String display = start + "  " + end;


                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

                    dateStart = LocalDate.parse(start, formatter);
                    if(dateStart.equals(LocalDate.now()) || dateStart.isBefore(LocalDate.now())){
                        Toast.makeText(context, "Start date needs to be in the future", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dateEnd = LocalDate.parse(end,formatter);
                    dateRangeEdit.setText(display);

                }
            });

            materialDatePicker.show(supportFragmentManager, "iksde");
        });
    }



}

class AvailabilitiesViewHolder extends RecyclerView.ViewHolder{
    TextView txtDateRange, txtCancelDate, txtPrice, txtIsPerGuest, txtChangeFromMessage;
    TextView txtDateRangeOld, txtCancelDateOld, txtPriceOld, txtIsPerGuestOld;
    Button btnRemoveAvailability, btnChangeAvailability;
    LinearLayout linearLayoutAvailabilityAdd, linearLayoutAvailabilityOld;
    public AvailabilitiesViewHolder(@NonNull View itemView) {
        super(itemView);

        linearLayoutAvailabilityAdd = itemView.findViewById(R.id.linearLayoutAvailabilityAdd);
        linearLayoutAvailabilityOld = itemView.findViewById(R.id.linearLayoutAvailabilityOld);
        btnRemoveAvailability = itemView.findViewById(R.id.btnRemoveAvailability);
        btnChangeAvailability = itemView.findViewById(R.id.btnChangeAvailability);
        txtDateRange = itemView.findViewById(R.id.txtAvailabilityDateRange);
        txtCancelDate = itemView.findViewById(R.id.txtAvailabilityCancelDifferences);
        txtPrice = itemView.findViewById(R.id.txtAvailabilityUnitPrice);
        txtIsPerGuest = itemView.findViewById(R.id.txtIsPricePerGuest);
        txtChangeFromMessage = itemView.findViewById(R.id.txtChangedFromMessage);
        txtDateRangeOld = itemView.findViewById(R.id.txtAvailabilityDateRangeOld);
        txtCancelDateOld = itemView.findViewById(R.id.txtAvailabilityCancelDifferencesOld);
        txtPriceOld = itemView.findViewById(R.id.txtAvailabilityUnitPriceOld);
        txtIsPerGuestOld = itemView.findViewById(R.id.txtIsPricePerGuestOld);

    }
}
