ArrayProto.every = function(self, callback) {
	var length = ArrayProto.length(self);
	for(var i = 0; i < length; i++) {
		if(!callback(self[i])) {
			return false;
		}
	}
	return true;
};

ArrayProto.filter = function(self, callback) {
	var a = [];
	var length = ArrayProto.length(self);
	for(var i = 0; i < length; i++) {
		if(callback(self[i])) {
			ArrayProto.push(a, self[i]);
		}
	}
	return a;
};

ArrayProto.find = function(self, callback) {
	var length = ArrayProto.length(self);
	for(var i = 0; i < length; i++) {
		if(callback(self[i])) {
			return self[i];
		}
	}
	return null;
};

ArrayProto.findIndex = function(self, callback) {
	var length = ArrayProto.length(self);
	for(var i = 0; i < length; i++) {
		if(callback(self[i])) {
			return i;
		}
	}
	return -1;
};

ArrayProto.forEach = function(self, callback) {
	var length = ArrayProto.length(self);
	for(var i = 0; i < length; i++) {
		callback(self[i]);
	}
};

ArrayProto.map = function(self, callback) {
	var a = [];
	var length = ArrayProto.length(self);
	for(var i = 0; i < length; i++) {
		ArrayProto.push(a, callback(self[i]));
	}
	return a;
};

ArrayProto.reduce = function(self, callback, initialValue) {
	var value = initialValue;
	var length = ArrayProto.length(self);
	for(var i = 0; i < length; i++) {
		value = callback(value, self[i]);
	}
	return value;
};

ArrayProto.reduceRight = function(self, callback, initialValue) {
	var value = initialValue;
	var length = ArrayProto.length(self);
	for(var i = length - 1; i >= 0; i--) {
		value = callback(value, self[i]);
	}
	return value;
};

ArrayProto.some = function(self, callback) {
	var length = ArrayProto.length(self);
	for(var i = 0; i < length; i++) {
		if(callback(self[i])) {
			return true;
		}
	}
	return false;
};