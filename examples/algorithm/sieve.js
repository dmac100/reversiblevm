var a = [];
var max = 100000;
for(var i = 2; i < max; i++) {
	if(a[i] == null) {
		print(i);
		for(var j = i; j < max; j += i) {
			a[j] = true;
		}
	}
}