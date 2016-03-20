var cx1 = 0.9;
var cy1 = 0.1;
var cx2 = 0.1;
var cy2 = 0.9;

function b(t, c1, c2) {
	return 3*(1-t)*(1-t)*t*c1 + 3*(1-t)*t*t*c2 + t*t*t;
}

function by(x, cx1, cx2, cy1, cy2) {
	var minT = 0;
	var maxT = 1;
	for(var i = 0; i < 20; i++) {
		var midT = (minT + maxT) / 2;
		var foundX = b(midT, cx1, cx2);
		if(foundX > x) {
			maxT = midT;
		} else {
			minT = midT;
		}
	}
	y = b(midT, cy1, cy2);
	return y;
}

function sx(x) {
	return x * 300 + 50;
}

function sy(y) {
	return (1 - y) * 300 + 50;
}

var points = [];

@for(p <- points) @rect(x: sx(p[0]), y: sy(p[1]), width: 4, height: 4, color: 'green');

@rect(x: sx(cx1), y: sy(cy1), width: 4, height: 4, color: 'cyan');
@rect(x: sx(cx2), y: sy(cy2), width: 4, height: 4, color: 'cyan');
@rect(x: sx(0), y: sy(0), width: 4, height: 4, color: 'cyan');
@rect(x: sx(1), y: sy(1), width: 4, height: 4, color: 'cyan');

var s = 0.001;

for(var x = 0; x <= 1; x += s) {
	y = by(x, cx1, cy1, cx2, cy2);
	points.push([x, y]);
}