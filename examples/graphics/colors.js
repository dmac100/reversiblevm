digits = ['0', '3', '6', '9', 'c', 'f'];

colors = [];
digits.forEach(x =>
	digits.forEach(y =>
		digits.forEach(z =>
			colors.push('#' + z + y + x)
		)
	)
)

@for(i <- colors.keys())
	@rect(
		x: 50 + (i % 6) * 20 + (Math.floor(i / 36) % 2) * 20 * 6,
		y: 50 + Math.floor(i / 6) * 20 - Math.floor((i + 36) / (36 * 2)) * 20 * 6,
		width: 20,
		height: 20,
		fill: colors[i],
		strokeWidth: 1,
		stroke: '#333'
	);