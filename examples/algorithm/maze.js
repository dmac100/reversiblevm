@bounds(fit: 'full');

var width = 79;
var height = 79;
var grid = newArray(width, height);

@for(x <- range(width), y <- range(height)) {
	@rect(
		x: x * 100,
		y: y * 100,
		width: 105,
		height: 105,
		fill: grid[x][y] == '#' ? '#c93' : '#ffe'
	);
}

function createGrid() {
	for(var x = 0; x < width; x++) {
		for(var y = 0; y < height; y++) {
			if(x == 0 || y == 0 || x == width - 1 || y == height - 1) {
				grid[x][y] = '#';
			} else {
				grid[x][y] = '-';
			}
		}
	}
	grid[1][0] = '-';
	grid[width - 2][height - 1] = '-';
}

function addWall() {
	var x = Math.floor(Math.random() * width / 2) * 2;
	var y = Math.floor(Math.random() * height / 2) * 2;
	
	var d = Math.floor(Math.random() * 4);
	var dx = [-1, 1, 0, 0][d];
	var dy = [0, 0, -1, 1][d];
	
	while(true) {
		if(x < 0 || x >= width || y < 0 || y >= height) return;
		if(grid[x][y] == '#') return;
		
		grid[x][y] = '#';
		x += dx;
		y += dy;
	}
}

createGrid();
for(var x = 0; x < 200; x++) {
	addWall();
}