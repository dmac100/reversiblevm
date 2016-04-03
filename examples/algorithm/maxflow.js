@bounds(fit: 'full');

var vertices = {};
var edges = [];
var source;
var sink;

function addVertex(label, x, y) {
	vertices[label] = { label: label, x: x, y: y, edges: [] };
}

function setSource(label) {
	source = label;
}

function setSink(label) {
	sink = label;
}

function addEdge(start, end, capacity) {
	var edge = {start: start, end: end, flow: 0, capacity: capacity};
	edges.push(edge);
	vertices[start].edges.push(edge);
}

function findMaxFlow() {
	while(findAugmentingPath()) {
	}
}

function findAugmentingPath() {
	var item;
	@for(v <- getPath(item))
		@circle[vertex: v.label](stroke: '#f33', strokeWidth: 2);
	@for(e <- eachCons(getPath(item), 2)) {
		@line[start: e[0].label, end: e[1].label](stroke: '#f33', strokeWidth: 2);
		@line[start: e[1].label, end: e[0].label](stroke: '#f33', strokeWidth: 2);
	}
	
	var queue = [];
	var visited = {};
	queue.push({label: source, prev: null});
	while(queue.length() > 0) {
		item = queue.shift();
		if(visited[item.label] != true) {
			visited[item.label] = true;
			
			edges.forEach(edge => {
				if(edge.end == item.label && edge.flow > 0) {
					queue.push({label: edge.start, prev: item, add: x => edge.flow-- });
				}
				if(edge.start == item.label && edge.flow < edge.capacity) {
					queue.push({label: edge.end, prev: item, add: x => edge.flow++ });
				}
			});
			
			if(item.label == sink) {
				while(item.label != source) {
					item.add();
					item = item.prev;
				}
				return true;
			}
		}
	}
	return false;
}

function eachCons(array, count) {
	var groups = [];
	for(var i = 0; i <= array.length() - count; i++) {
		var group = [];
		for(var j = 0; j < count; j++) {
			group.push(array[i + j]);
		}
		groups.push(group);
	}
	return groups;
}

function getPath(item) {
	path = [];
	while(item != null) {
		path.push(vertices[item.label]);
		item = item.prev;
	}
	path = path.reverse();
	return path;
}

addVertex('s', 60, 150);
addVertex('o', 180, 75);
addVertex('q', 330, 75);
addVertex('t', 450, 150);
addVertex('p', 180, 225);
addVertex('r', 330, 225);

setSource('s');
setSink('t');

addEdge('s', 'o', 3);
addEdge('o', 'q', 3);
addEdge('q', 't', 2);
addEdge('r', 't', 3);
addEdge('p', 'r', 2);
addEdge('s', 'p', 3);
addEdge('q', 'r', 4);
addEdge('o', 'p', 2);

@for(v <- [vertices[source], vertices[sink]]) {
	@circle(
		vertex: v.label,
		cx: v.x,
		cy: v.y,
		r: 27,
		strokeWidth: 1,
		fill: 'none'
	);
}

@for(v <- vertices.values()) {
	@circle(
		vertex: v.label,
		cx: v.x,
		cy: v.y,
		r: 30,
		strokeWidth: 1,
		fill: 'none'
	);
	@text(
		vertex: v.label,
		x: v.x,
		y: v.y,
		text: v.label,
		textAlign: 'centerMiddle',
		fontStyle: 'bold',
		fontSize: 14
	);
}
	
@for(v <- vertices.values(), e <- v.edges.values()) {
	@line(
		start: e.start,
		end: e.end,
		x1: vertices[e.start].x,
		y1: vertices[e.start].y,
		x2: vertices[e.end].x,
		y2: vertices[e.end].y,
		startOffset: 30,
		endOffset: 30,
		arrowLength: 13
	);
	@text(
		start: e.start.label,
		end: e.end.label,
		x: (vertices[e.start].x + vertices[e.end].x) / 2 + 2,
		y: (vertices[e.start].y + vertices[e.end].y) / 2 + 2,
		text: e.flow + '/' + e.capacity
	);
}

findMaxFlow();