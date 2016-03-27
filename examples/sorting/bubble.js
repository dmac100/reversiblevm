@delay(transition: 50, instruction: 8);

a = [7, 3, 1, 8, 4, 6, 2, 6, 2, 3, 8, 1, 10, 7, 3, 6, 2, 3, 8, 1];
a = a.map(x => { value: x });

@for(item <- a)
	@rect(
		x: 40,
		y: 15 * a.indexOf(item) + 20,
		width: 20 * item.value,
		height: 8,
		fill: item.value > 5 ? 'green' : 'red',
		strokeWidth: 1,
		stroke: '#333'
	);

@rect(
	x: 10,
	y: 15 * x + 20,
	width: 20,
	height: 8,
	fill: 'cyan',
	strokeWidth: 1,
	stroke: '#000'
);

@rect(
	x: 10,
	y: 15 * y + 20,
	width: 20,
	height: 8,
	fill: 'yellow',
	strokeWidth: 1,
	stroke: '#000'
);

for(var x = 0; x < a.length(); x++) {
	for(var y = x; y < a.length(); y++) {
		if(a[x].value > a[y].value) {
			@vizUpdatesOff;
			var t = a[x];
			a[x] = a[y];
			a[y] = t;
			@vizUpdatesOn;
		}
	}
}