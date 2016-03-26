@delay(transition: 200, instruction: 25);

discs = [[1, 2, 3, 4, 5, 6, 7, 8, 9, 10], [], []];

function allDiscs() {
	return discs[0].concat(discs[1]).concat(discs[2]);
}

function discTower(d) {
	return discs.keys().find(i => discs[i].indexOf(d) >= 0);
}

function discHeight(d) {
	var height = 0;
	discs[discTower(d)].forEach(x => height += (x > d) ? 1 : 0);
	return height;
}

@for(d <- discs.keys())
	@rect(
		x: 95 + d * 180,
		y: 197,
		width: 10,
		height: 110,
		fill: 'yellow',
		strokeWidth: 1
	);
	
@for(d <- discs.keys())
	@rect(
		x: 100 + d * 180 - 80,
		y: 307,
		width: 160,
		height: 10,
		fill: 'yellow',
		strokeWidth: 1
	);

@for(d <- allDiscs())
	@rect(
		x: 100 + discTower(d) * 180 - d * 8,
		y: 300 - discHeight(d) * 11,
		width: d * 16,
		height: 7,
		fill: ['red', 'green'][d % 2],
		strokeWidth: 1
	);

function move(source, dest) {
	discs[dest].unshift(discs[source].shift());
}

function solve(n, source, dest, by) {
	if(n == 1) {
		move(source, dest);
	} else {
		solve(n - 1, source, by, dest);
		move(source, dest);
		solve(n - 1, by, dest, source);
	}
}

solve(discs[0].length(), 0, 2, 1);