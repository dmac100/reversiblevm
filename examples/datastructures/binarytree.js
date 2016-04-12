@delay(instruction: 0, transition: 1000);
@bounds(fit: 'full');

root = {};

function treePositions(root) {
	function _treePositions(node, parent, y) {
		var positions = [];
		if(node != null && node.value != null) {
			var newNode = {
				value: node.value,
				parent: parent,
				x: x,
				y: y,
				key: node
			};
			positions.pushAll(_treePositions(node.left, newNode, y + 50));
			positions.push(newNode);
			newNode.x = x;
			x += 50;
			positions.pushAll(_treePositions(node.right, newNode, y + 50));
		}
		return positions;
	}
	var x = 0;
	return _treePositions(root, null, 0);
}

@for(node <- treePositions(root)) {
	@circle(
		cx: node.x,
		cy: node.y,
		strokeWidth: 1,
		r: 20,
		fill: 'none'
	);
	@text(
		x: node.x,
		y: node.y,
		text: '' + node.value,
		textAlign: 'middleCenter'
	);
}

@for(node <- treePositions(root), node.parent != null) {
	@line(
		x1: node.parent.x,
		y1: node.parent.y,
		x2: node.x,
		y2: node.y,
		arrowLength: 10,
		strokeWidth: 1,
		startOffset: 20,
		endOffset: 20
	);
}

function add(node, value) {
	if(node.value == null) {
		node.value = value;
		node.left = {};
		node.right = {};
	} else {
		if(value < node.value) {
			add(node.left, value);
		} else {
			add(node.right, value);
		}
	}
}

function remove(node, value, parent) {
	if(node.value != null) {
		if(value < node.value) {
			remove(node.left, value, node);
		} else if(value > node.value) {
			remove(node.right, value, node);
		} else if(value == node.value) {
			if(node.left.value != null && node.right.value != null) {
				node.value = minValue(node.right);
				remove(node.right, node.value, node);
			} else if(parent == null) {
				root = (node.left.value != null) ? node.left : node.right;
			} else if(parent.left == node) {
				parent.left = (node.left.value != null) ? node.left : node.right;
			} else if(parent.right == node) {
				parent.right = (node.left.value != null) ? node.left : node.right;
			}
		}
	}
}

function contains(node, value) {
	if(node.value == null) {
		return false;
	} else if(node.value == value) {
		return true;
	} else if(value < node.value) {
		return contains(node.left, value);
	} else {
		return contains(node.right, value);
	}
}

function minValue(node) {
	if(node.left.value == null) {
		return node.value;
	} else {
		return minValue(node.left);
	}
}

add(root, 10);
add(root, 5);
add(root, 20);
add(root, 30);
add(root, 15);
add(root, 12);
add(root, 9);
add(root, 11);
add(root, 13);
add(root, 4);

remove(root, 15);