@rect(
	x: 50,
	y: 50,
	width: 40,
	height: 40,
	fill: 'red'
);

@rect(
	x: 250,
	y: 50,
	width: 40,
	height: 40,
	rx: 20,
	ry: 20,
	fill: 'green',
	stroke: 'darkgrey',
	strokeWidth: 1
);

@ellipse(
	cx: 250,
	cy: 150,
	rx: 50,
	ry: 20,
	fill: 'blue',
	strokeWidth: 5,
	stroke: 'green'
);

@circle(
	cx: 100,
	cy: 240,
	r: 60,
	strokeWidth: 2,
	stroke: 'black'
);

@line(
	x1: 80,
	y1: 120,
	x2: 250,
	y2: 270,
	stroke: 'black',
	strokeWidth: 5,
	arrowLength: 15,
	arrowAngle: 30
);

@text(
	x: 80,
	y: 320,
	fontFamily: 'Verdana',
	stroke: 'blue',
	fontStyle: 'bold-italic',
	fontSize: 55,
	text: 'Hello'
);