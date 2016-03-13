a = [7, 3, 1, 8, 4, 6, 2, 6, 2, 3, 8, 1, 10, 7, 3, 6, 2, 3, 8, 1];

@for(i <- a.keys())
	rect(
		x: 10,
		y: 15 * i + 20,
		width: 20 * a[i],
		height: 8,
		color: a[i] > 5 ? 'green' : 'red'
	);

for(var x = 0; x < a.length(); x++) {
	for(var y = x; y < a.length(); y++) {
		if(a[x] > a[y]) {
			var t = a[x];
			a[x] = a[y];
			a[y] = t;
		}
	}
}