package com.noahnu.cordova.datetimepicker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.TimeZone;

public class DateTimePicker extends CordovaPlugin {

    public enum MODE {
        DATE,
        TIME
    }

    private static final String LOG_TAG = "DateTimePicker";
    CallbackContext callbackContext;

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;

        if (this.cordova.getActivity().isFinishing()) return true;

        try {
            final PickerOptions opts = new PickerOptions(args.getJSONObject(0));
            if (action.equals("exec")) {
                this.cordova.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (opts.mode == MODE.DATE) {
                            DateTimePicker.this.showDatePicker(opts);
                        } else if (opts.mode == MODE.TIME) {
                            DateTimePicker.this.showTimePicker(opts);
                        }
                    }
                });
                return true;
            }
        } catch (BadPickerOptions ex) {
            Log.e(LOG_TAG, ex.getMessage());
            this.callbackContext.error(ex.getMessage());
        }

        return false;
    }

    private void showDatePicker(final PickerOptions opts) {
        Context context = DateTimePicker.this.cordova.getActivity();
        final DatePickerDialog picker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                JSONObject response = new JSONObject();
                Calendar calendar = Calendar.getInstance(opts.timezone);
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);

                try {
                    response.put("year", year);
                    response.put("month", month);
                    response.put("day", day);
                    response.put("date", calendar.getTimeInMillis());
                } catch (JSONException ex) {
                    Log.e(LOG_TAG, ex.getMessage());
                    DateTimePicker.this.callbackContext.error("Error");
                    return;
                }

                DateTimePicker.this.callbackContext.success(response);
            }
        }, opts.year, opts.month, opts.day);
        picker.getDatePicker().setMinDate(opts.minDate);
        picker.getDatePicker().setMaxDate(opts.maxDate);
        picker.setCancelable(true);
        picker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                DateTimePicker.this.callbackContext.success("cancel");

                try {
                    if (picker.isShowing()) {
                        picker.hide();
                    }
                } catch (Exception ex){}
            }
        });

        picker.show();
    }

    private void showTimePicker(final PickerOptions opts) {
        Context context = DateTimePicker.this.cordova.getActivity();
        final TimePickerDialog picker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker datePicker, int hour, int minute) {
                JSONObject response = new JSONObject();
                Calendar calendar = Calendar.getInstance(opts.timezone);
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);

                try {
                    response.put("hour", hour);
                    response.put("minute", minute);
                    response.put("date", calendar.getTimeInMillis());
                } catch (JSONException ex) {
                    Log.e(LOG_TAG, ex.getMessage());
                    DateTimePicker.this.callbackContext.error("Error");
                    return;
                }

                DateTimePicker.this.callbackContext.success(response);
            }
        }, opts.hour, opts.minute, false);
        picker.setCancelable(true);
        picker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                DateTimePicker.this.callbackContext.success("cancel");

                try {
                    if (picker.isShowing()) {
                        picker.hide();
                    }
                } catch (Exception ex) {}
            }
        });
        picker.show();
    }

    class BadPickerOptions extends Exception {
        public BadPickerOptions(String option, String reason) {
            super(String.format("Invalid use of %s. Reason: %s", option, reason));
        }
    }

    class PickerOptions {
        static final long MAX_DATE = 4099680000000l;

        public final MODE mode;

        public final int year;
        public final int month;
        public final int day;
        public final int hour;
        public final int minute;

        public final long minDate;
        public final long maxDate;

        public final TimeZone timezone;

        public PickerOptions(JSONObject obj) throws JSONException, BadPickerOptions {
            // Set mode
            if (obj.has("mode")) {
                try {
                    this.mode = MODE.valueOf(obj.getString("mode").toUpperCase());
                } catch (IllegalArgumentException ex) {
                    throw new BadPickerOptions("mode", "Must be one of DATE, TIME.");
                }
            } else {
                this.mode = MODE.DATE;
            }

            Calendar calendar;

            if (obj.has("timezone")) {
                try {
                    this.timezone = TimeZone.getTimeZone(obj.getString("timezone"));
                    calendar = Calendar.getInstance(this.timezone);
                } catch (Exception ex) {
                    throw new BadPickerOptions("timezone", "Invalid timezone.");
                }
            } else {
                this.timezone = TimeZone.getDefault();
                calendar = Calendar.getInstance();
            }

            if (obj.has("date")) {
                try {
                    long epoch = obj.getLong("date");
                    if (Math.floor(Math.log10((double) epoch)) <= 9) {
                        // Assume seconds since epoch, thus normalize.
                        epoch = epoch * 1000;
                    }

                    calendar.setTimeInMillis(epoch);
                } catch (JSONException ex) {
                    throw new BadPickerOptions("date", "Expected unix timestamp.");
                }
            }

            if (obj.has("minDate")) {
                this.minDate = obj.getLong("minDate");
            } else {
                this.minDate = 0;
            }

            if (obj.has("maxDate")) {
                this.maxDate = obj.getLong("maxDate");
            } else {
                this.maxDate = MAX_DATE;
            }

            this.year = calendar.get(Calendar.YEAR);
            this.month = calendar.get(Calendar.MONTH);
            this.day = calendar.get(Calendar.DAY_OF_MONTH);
            this.hour = calendar.get(Calendar.HOUR_OF_DAY);
            this.minute = calendar.get(Calendar.MINUTE);
        }
    }
}
