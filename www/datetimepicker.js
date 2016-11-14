var exec = require('cordova/exec');

var DateTimePicker = {
	show: function(opts) {
		return new Promise(function (resolve, reject) {
			exec(function(resp){
				if (typeof resp === 'string' && resp.toLowerCase() === 'cancel') {
					reject('cancel');
				} else {
					resolve(resp);
				}
			}, reject, "DateTimePicker", "exec", [opts]);
		});
	}
};

module.exports = DateTimePicker;
