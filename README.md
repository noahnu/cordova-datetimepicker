# cordova-datetimepicker

Straightforward interface to access the native Date and Time Pickers.

## Install

```
cordova plugin add https://github.com/noahnu/cordova-datetimepicker.git
```

## Usage

Both invocations of the DateTimePicker.show method return a Promise.

### Date Picker

```
DateTimePicker.show({
	mode: 'date'
}).then((date) => {
	console.log(date.year);
	console.log(date.month);
	console.log(date.day);
	console.log(date.date); // unix timestamp in millis
}).catch(er => {
	// Invalid options, or user canceled.
	console.log(er);
});
```

### Time Picker

```
DateTimePicker.show({
	mode: 'time'
}).then((date) => {
	console.log(date.year);
	console.log(date.month);
	console.log(date.day);
	console.log(date.date); // unix timestamp in millis
}).catch(er => {
	// Invalid options, or user canceled.
	console.log(er);
});
```

### Options

Only `mode` is required.

```
{
	mode: String, // 'date' or 'time'
	timezone: String, // Java timezone id, e.g. America/New_York
	date: Int, // Unix timestamp in seconds or milliseconds
	minDate: Int, // Unix timestamp in milliseconds
	maxDate: Int // Unix timestamp in milliseconds
}
```