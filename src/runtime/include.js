ArrayProto.every = function(callback) {
	var length = this.length();
	for(var i = 0; i < length; i++) {
		if(!callback(this[i])) {
			return false;
		}
	}
	return true;
};

ArrayProto.filter = function(callback) {
	var a = [];
	var length = this.length();
	for(var i = 0; i < length; i++) {
		if(callback(this[i])) {
			a.push(this[i]);
		}
	}
	return a;
};

ArrayProto.find = function(callback) {
	var length = this.length();
	for(var i = 0; i < length; i++) {
		if(callback(this[i])) {
			return this[i];
		}
	}
	return null;
};

ArrayProto.findIndex = function(callback) {
	var length = this.length();
	for(var i = 0; i < length; i++) {
		if(callback(this[i])) {
			return i;
		}
	}
	return -1;
};

ArrayProto.forEach = function(callback) {
	var length = this.length();
	for(var i = 0; i < length; i++) {
		callback(this[i]);
	}
};

ArrayProto.map = function(callback) {
	var a = [];
	var length = this.length();
	for(var i = 0; i < length; i++) {
		a.push(callback(this[i]));
	}
	return a;
};

ArrayProto.reduce = function(callback, initialValue) {
	var value = initialValue;
	var length = this.length();
	for(var i = 0; i < length; i++) {
		value = callback(value, this[i]);
	}
	return value;
};

ArrayProto.reduceRight = function(callback, initialValue) {
	var value = initialValue;
	var length = this.length();
	for(var i = length - 1; i >= 0; i--) {
		value = callback(value, this[i]);
	}
	return value;
};

ArrayProto.some = function(callback) {
	var length = this.length();
	for(var i = 0; i < length; i++) {
		if(callback(this[i])) {
			return true;
		}
	}
	return false;
};